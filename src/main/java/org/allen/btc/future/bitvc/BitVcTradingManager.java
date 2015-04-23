package org.allen.btc.future.bitvc;

import static org.allen.btc.Constants.BITVC_ORDER_STATUS_CANCEL;
import static org.allen.btc.Constants.BITVC_ORDER_STATUS_DONE;
import static org.allen.btc.Constants.BITVC_ORDER_STATUS_DONE_HALF;
import static org.allen.btc.Constants.BITVC_ORDER_STATUS_PENDING;
import static org.allen.btc.Constants.BITVC_ORDER_STATUS_UNDONE;
import static org.allen.btc.Constants.MAX_RETRY_TIMES;
import static org.allen.btc.Constants.PARAM_BITVC_COINTYPE_BTC;
import static org.allen.btc.Constants.PARAM_BITVC_CONTRACTTYPE_WEEK;

import java.util.concurrent.locks.LockSupport;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.domain.VcOrderQueryRequest;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.market.MarketDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther lansheng.zj
 */
public class BitVcTradingManager {

    private static Logger log = LoggerFactory.getLogger(BitVcTradingManager.class);

    private BitVcTrading bitVc;
    private MarketDetector marketDetector;
    private HedgingConfig config;


    public BitVcTradingManager(HedgingConfig hedgingConfig, MarketDetector detector, BitVcTrading vc) {
        config = hedgingConfig;
        marketDetector = detector;
        bitVc = vc;
    }


    public VcOrderResponse trade(String orderType, String tradeType, String price, String money)
            throws Exception {
        VcOrderRequest vcRequest = new VcOrderRequest();
        vcRequest.setAccessKey(config.getAccessKey());
        vcRequest.setSecretKey(config.getSecretKey());

        vcRequest.setCoinType(PARAM_BITVC_COINTYPE_BTC);
        vcRequest.setContractType(PARAM_BITVC_CONTRACTTYPE_WEEK);
        vcRequest.setCreated(System.currentTimeMillis() / 1000 + "");
        vcRequest.setOrderType(orderType);
        vcRequest.setTradeType(tradeType);
        vcRequest.setPrice(price);
        vcRequest.setMoney(money);
        vcRequest.setLeverage("10");

        VcOrderResponse response = bitVc.trade(vcRequest, 1000);
        return response;
    }


    public VcOrderResponse queryTradeOrder(String id) throws Exception {
        VcOrderQueryRequest vcOrderQueryRequest = new VcOrderQueryRequest();
        vcOrderQueryRequest.setAccessKey(config.getAccessKey());
        vcOrderQueryRequest.setSecretKey(config.getSecretKey());
        vcOrderQueryRequest.setCoinType(PARAM_BITVC_COINTYPE_BTC);
        vcOrderQueryRequest.setContractType(PARAM_BITVC_CONTRACTTYPE_WEEK);
        vcOrderQueryRequest.setCreated(System.currentTimeMillis() / 1000 + "");
        vcOrderQueryRequest.setId(id);

        VcOrderResponse response = bitVc.getTradeOrder(vcOrderQueryRequest, 1000);
        return response;
    }


    private String computeMoney(VcOrderResponse response, int status, String nowPrice, String oldPrice,
            String oldMoney) {
        float tmpAmount = Float.parseFloat(oldPrice) / Float.parseFloat(oldMoney);
        if (BITVC_ORDER_STATUS_DONE_HALF == status) {
            tmpAmount = tmpAmount - Float.parseFloat(response.getProcessedAmount());
        }
        return tmpAmount * Float.parseFloat(nowPrice) + "";
    }


    private String computeNowBuyPrice() {
        VcTicker vcTicker = marketDetector.getNowVcTicker();
        return vcTicker.getBuy();
    }


    private String computeNowSellPrice() {
        VcTicker vcTicker = marketDetector.getNowVcTicker();
        return vcTicker.getSell();
    }


    private boolean tradeOpenAirAndPending(String id, String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse queryResponse = null;
        int failTimes = 0;
        while (null == queryResponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryResponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("bit vc query trade open air order fail " + failTimes + " times. id=" + id
                        + ", price=" + price + ", money=" + money, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryResponse) {
            log.error("[never expected] bit vc query tarde open air order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryResponse.getId());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            log.warn("vc trade open air success. " + queryResponse);
            isSuccess = true;
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowBuyPrice(); // 买
            tmpMoney = computeMoney(queryResponse, status, tmpPrice, price, money);
            isSuccess = tradeOpenAir(tmpPrice, tmpMoney, times + 1);
            log.warn("vc trade open air not exactly success, so retry open air. id=" + id + ", tmpPrice="
                    + tmpPrice + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc open air pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("vc trade open air still in pending. id=" + id + ", price=" + price + ", money="
                        + money + ", times=" + times);
                isSuccess = tradeOpenAirAndPending(id, price, money, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("bitvc open air pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }


    // 开空
    public boolean tradeOpenAir(String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse response = null;
        int failTimes = 0;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade("1", "2", tmpPrice, tmpMoney);
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("bitvc open air fail " + failTimes + " times. price=" + price + ", money=" + money,
                    e);
            }
        }
        if (null == response) {
            log.error("[never expected] bit vc open air" + MAX_RETRY_TIMES + " times, but still fail. price="
                    + price + ", money=" + money);
            return false;
        }

        int status = Integer.parseInt(response.getStatus());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            isSuccess = true;
            log.warn("vc trade open air success. " + response);
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowBuyPrice();
            tmpMoney = computeMoney(response, status, tmpPrice, price, money);
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc open air retry " + times + " times but still fail. price="
                        + price + ", money=" + money);
            }
            else {
                isSuccess = tradeOpenAir(tmpPrice, tmpMoney, times + 1);
                log.warn("vc trade open air not exactly success, so retry open air. tmpPrice=" + tmpPrice
                        + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            }
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            isSuccess = tradeOpenAirAndPending(response.getId(), price, money, times + 1);
            break;
        default:
            throw new UnsupportedOperationException("bitvc open air unsupported operation. status=" + status);
        }

        return isSuccess;
    }


    // 平空
    public boolean tradeReverseAir(String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse response = null;
        int failTimes = 0;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade("2", "1", tmpPrice, tmpMoney);
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("bitvc reverse air fail " + failTimes + " times. price=" + price + ", money="
                        + money, e);
            }
        }
        if (null == response) {
            log.error("[never expected] bit vc reverse air" + MAX_RETRY_TIMES
                    + " times, but still fail. price=" + price + ", money=" + money);
            return false;
        }

        int status = Integer.parseInt(response.getStatus());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            isSuccess = true;
            log.warn("vc trade reverse air success. " + response);
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowSellPrice();
            tmpMoney = computeMoney(response, status, tmpPrice, price, money);
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc reverse air retry " + times
                        + " times but still fail. price=" + price + ", money=" + money);
            }
            else {
                isSuccess = tradeReverseAir(tmpPrice, tmpMoney, times + 1);
                log.warn("vc trade reverse air not exactly success, so retry reverse air. tmpPrice="
                        + tmpPrice + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            }
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            isSuccess = tradeReverseAirAndPending(response.getId(), price, money, times + 1);
            break;
        default:
            throw new UnsupportedOperationException("bitvc reverse air unsupported operation. status="
                    + status);
        }

        return isSuccess;
    }


    private boolean tradeReverseAirAndPending(String id, String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse queryResponse = null;
        int failTimes = 0;
        while (null == queryResponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryResponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("bit vc query trade reverse air order fail " + failTimes + " times. id=" + id
                        + ", price=" + price + ", money=" + money, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryResponse) {
            log.error("[never expected] bit vc query tarde reverse air order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryResponse.getId());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            log.warn("vc trade reverse air success. " + queryResponse);
            isSuccess = true;
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowSellPrice(); // 买
            tmpMoney = computeMoney(queryResponse, status, tmpPrice, price, money);
            isSuccess = tradeReverseAir(tmpPrice, tmpMoney, times + 1);
            log.warn("vc trade reverse air not exactly success, so retry reverse air. id=" + id
                    + ", tmpPrice=" + tmpPrice + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times="
                    + times);
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc reverse air pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("vc trade reverse air still in pending. id=" + id + ", price=" + price + ", money="
                        + money + ", times=" + times);
                isSuccess = tradeReverseAirAndPending(id, price, money, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException(
                "bitvc reverse air pending unsupported operation. status=" + status + ", id=" + id);
        }

        return isSuccess;
    }


    // 平多
    public boolean tradeReverse(String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse response = null;
        int failTimes = 0;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade("2", "2", tmpPrice, tmpMoney);
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("bitvc reverse fail " + failTimes + " times. price=" + price + ", money=" + money,
                    e);
            }
        }
        if (null == response) {
            log.error("[never expected] bit vc reverse " + MAX_RETRY_TIMES + " times, but still fail. price="
                    + price + ", money=" + money);
            return false;
        }
        int status = Integer.parseInt(response.getStatus());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            isSuccess = true;
            log.warn("vc trade reverse success. " + response);
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowBuyPrice();
            tmpMoney = computeMoney(response, status, tmpPrice, price, money);
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc reverse retry " + times + " times but still fail. price="
                        + price + ", money=" + money);
            }
            else {
                isSuccess = tradeReverse(tmpPrice, tmpMoney, times + 1);
                log.warn("vc trade reverse not exactly success, so retry reverse. tmpPrice=" + tmpPrice
                        + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            }
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            isSuccess = tradeReverseAndPending(response.getId(), price, money, times + 1);
            break;
        default:
            throw new UnsupportedOperationException("bitvc reverse air unsupported operation. status="
                    + status);
        }

        return isSuccess;
    }


    private boolean tradeReverseAndPending(String id, String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse queryResponse = null;
        int failTimes = 0;
        while (null == queryResponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryResponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("bit vc query trade reverse order fail " + failTimes + " times. id=" + id
                        + ", price=" + price + ", money=" + money, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryResponse) {
            log.error("[never expected] bit vc query tarde reverse order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryResponse.getId());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            log.warn("vc trade reverse success. " + queryResponse);
            isSuccess = true;
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowBuyPrice(); // 买
            tmpMoney = computeMoney(queryResponse, status, tmpPrice, price, money);
            isSuccess = tradeReverse(tmpPrice, tmpMoney, times + 1);
            log.warn("vc trade reverse not exactly success, so retry reverse. id=" + id + ", tmpPrice="
                    + tmpPrice + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc reverse pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("vc trade reverse still in pending. id=" + id + ", price=" + price + ", money="
                        + money + ", times=" + times);
                isSuccess = tradeReverseAndPending(id, price, money, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("bitvc reverse pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }


    public VcTicker getNowVcTicker() {
        return marketDetector.getNowVcTicker();
    }


    // 开多
    public boolean tradeOpen(String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse response = null;
        int failTimes = 0;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade("1", "1", tmpPrice, tmpMoney);
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("bitvc open fail " + failTimes + " times. price=" + price + ", money=" + money, e);
            }
        }
        if (null == response) {
            log.error("[never expected] bit vc open " + MAX_RETRY_TIMES + " times, but still fail. price="
                    + price + ", money=" + money);
            return false;
        }
        int status = Integer.parseInt(response.getStatus());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            isSuccess = true;
            log.warn("vc trade open success. " + response);
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowSellPrice();
            tmpMoney = computeMoney(response, status, tmpPrice, price, money);
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc open retry " + times + " times but still fail. price="
                        + price + ", money=" + money);
            }
            else {
                isSuccess = tradeOpen(tmpPrice, tmpMoney, times + 1);
                log.warn("vc trade open not exactly success, so retry open. tmpPrice=" + tmpPrice
                        + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            }
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            isSuccess = tradeOpenAndPending(response.getId(), price, money, times + 1);
            break;
        default:
            throw new UnsupportedOperationException("bitvc open air unsupported operation. status=" + status);
        }

        return isSuccess;
    }


    private boolean tradeOpenAndPending(String id, String price, String money, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpMoney = money;
        VcOrderResponse queryResponse = null;
        int failTimes = 0;
        while (null == queryResponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryResponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("bit vc query trade open order fail " + failTimes + " times. id=" + id + ", price="
                        + price + ", money=" + money, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryResponse) {
            log.error("[never expected] bit vc query tarde open order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryResponse.getId());
        switch (status) {
        case BITVC_ORDER_STATUS_DONE: // 已成交
            log.warn("vc trade open success. " + queryResponse);
            isSuccess = true;
            break;
        case BITVC_ORDER_STATUS_UNDONE: // 未成交
        case BITVC_ORDER_STATUS_CANCEL: // 撤单
        case BITVC_ORDER_STATUS_DONE_HALF: // 部分成交
            tmpPrice = computeNowSellPrice(); // 买
            tmpMoney = computeMoney(queryResponse, status, tmpPrice, price, money);
            isSuccess = tradeOpen(tmpPrice, tmpMoney, times + 1);
            log.warn("vc trade open not exactly success, so retry open. id=" + id + ", tmpPrice=" + tmpPrice
                    + ", tmpMoney=" + tmpMoney + ", status=" + status + ", times=" + times);
            break;
        case BITVC_ORDER_STATUS_PENDING: // 队列中
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] bit vc open pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("vc trade open still in pending. id=" + id + ", price=" + price + ", money=" + money
                        + ", times=" + times);
                isSuccess = tradeOpenAndPending(id, price, money, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("bitvc open pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }
}
