package org.allen.btc.future.okcoin;

import static org.allen.btc.Constants.MAX_RETRY_TIMES;
import static org.allen.btc.Constants.OKCOIN_ORDER_STATUS_CANCEL;
import static org.allen.btc.Constants.OKCOIN_ORDER_STATUS_CANCEL_PENDING;
import static org.allen.btc.Constants.OKCOIN_ORDER_STATUS_DONE;
import static org.allen.btc.Constants.OKCOIN_ORDER_STATUS_DONE_HALF;
import static org.allen.btc.Constants.OKCOIN_ORDER_STATUS_PENDING;
import static org.allen.btc.Constants.PARAM_OKCOIN_CONTRACT_F_WEEK;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL_F_VALUE;

import java.util.concurrent.locks.LockSupport;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkTradeQueryRequest;
import org.allen.btc.future.okcoin.domain.OkTradeQueryResponse;
import org.allen.btc.future.okcoin.domain.OkTradeRequest;
import org.allen.btc.future.okcoin.domain.OkTradeResponse;
import org.allen.btc.market.MarketDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther lansheng.zj
 */
public class OkCoinTradingManager {

    private static Logger log = LoggerFactory.getLogger(OkCoinTradingManager.class);

    private OkCoinTrading okCoin;
    private MarketDetector marketDetector;
    private HedgingConfig config;


    public OkCoinTradingManager(HedgingConfig hedgingConfig, MarketDetector detector, OkCoinTrading ok) {
        config = hedgingConfig;
        marketDetector = detector;
        okCoin = ok;
    }


    public OkTradeQueryResponse queryTradeOrder(String id) throws Exception {
        OkTradeQueryRequest okRequest = new OkTradeQueryRequest();
        okRequest.setAccessKey(config.getAccessKey());
        okRequest.setSecretKey(config.getSecretKey());

        okRequest.setContract_type(PARAM_OKCOIN_CONTRACT_F_WEEK);
        okRequest.setCurrent_page("1");
        okRequest.setOrder_id(id);
        okRequest.setPage_length("40");
        okRequest.setStatus("1");
        okRequest.setSymbol(PARAM_OKCOIN_SYMBOL_F_VALUE);

        OkTradeQueryResponse response = okCoin.getTradeOrder(okRequest, 1000);
        return response;
    }


    public OkTradeResponse trade(String price, String amount, boolean matchPrice, String type)
            throws Exception {
        // B 开多,看B卖1价格
        OkTradeRequest okRequest = new OkTradeRequest();
        okRequest.setAccessKey(config.getAccessKey());
        okRequest.setSecretKey(config.getSecretKey());

        okRequest.setSymbol(PARAM_OKCOIN_SYMBOL_F_VALUE);
        okRequest.setAmount(amount);
        okRequest.setContract_type(PARAM_OKCOIN_CONTRACT_F_WEEK);
        okRequest.setLever_rate(10 + "");
        okRequest.setMatch_price(matchPrice ? "1" : "0");
        okRequest.setPrice(price);
        okRequest.setType(type);

        OkTradeResponse response = okCoin.trade(okRequest, 1000);
        return response;
    }


    // 平空
    public boolean tradeReverseAir(String price, String amount, boolean matchPrice, int times) {
        boolean isSuccess = false;
        int failTimes = 0;
        OkTradeResponse response = null;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade(price, amount, matchPrice, "4");
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("okcoin reverse air fail " + failTimes + " times. price=" + price + ", amount="
                        + amount + ", matchPrice=" + matchPrice, e);
            }
        }
        if (null == response) {
            log.error("[never expected] okcoin trade reverse air " + MAX_RETRY_TIMES
                    + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                    + matchPrice);
            return false;
        }

        if (response.isResult()) {
            log.warn("okcoin trade reverse air delegate done. id=" + response.getOrder_id() + ", price="
                    + price + ", amount=" + amount + ", matchPrice=" + matchPrice + ", times=" + times);
            isSuccess = tradeReverseAirAndPending(response.getOrder_id(), price, amount, times);
        }
        else {
            log.error("okcoin trade reverse air fail, order_id =" + response.getOrder_id());
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin trade reverse air response fail " + MAX_RETRY_TIMES
                        + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                        + matchPrice);
            }
            else {
                // 市价单
                isSuccess = tradeReverseAir(price, amount, true, times + 1);
            }
        }

        return isSuccess;
    }


    private boolean tradeReverseAirAndPending(String id, String price, String amount, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpAmount = amount;
        int failTimes = 0;
        OkTradeQueryResponse queryReponse = null;
        while (null == queryReponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryReponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("okcoin query trade reverse air fail " + failTimes + " times. id=" + id
                        + ", price=" + price + ", amount=" + amount, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryReponse) {
            log.error("[never expected] okcoin query trade reverse air order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryReponse.getStatus());
        switch (status) {
        case OKCOIN_ORDER_STATUS_DONE: // 成交
            isSuccess = true;
            log.warn("okcoin trade reverse air success. id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_DONE_HALF: // 部分成交
        case OKCOIN_ORDER_STATUS_CANCEL: // 撤单
        case OKCOIN_ORDER_STATUS_CANCEL_PENDING: // 撤单处理中
            // 市价单
            tmpPrice = computNowSellPrice(); // 卖
            tmpAmount = computeAmount(queryReponse, status, amount);
            isSuccess = tradeReverseAir(tmpPrice, tmpAmount, true, times + 1);
            log.warn("okcoin trade reverse air not exactly success, so retry reverse. tmpPrice=" + tmpPrice
                    + ", tmpAmount=" + tmpAmount + ", price=" + price + ", amount=" + amount + ", id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_PENDING: // 等待成交
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin reverse air pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("okcoin trade reverse air still in pending. id=" + id + ", price=" + price
                        + ", amount=" + amount + ", times=" + times);
                isSuccess = tradeReverseAirAndPending(id, price, amount, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException(
                "okcoin reverse air pending unsupported operation. status=" + status + ", id=" + id);
        }

        return isSuccess;
    }


    // 开空
    public boolean tradeOpenAir(String price, String amount, boolean matchPrice, int times) {
        boolean isSuccess = false;
        int failTimes = 0;
        OkTradeResponse response = null;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade(price, amount, matchPrice, "2");
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("okcoin open air fail " + failTimes + " times. price=" + price + ", amount="
                        + amount + ", matchPrice=" + matchPrice, e);
            }
        }
        if (null == response) {
            log.error("[never expected] okcoin trade open air " + MAX_RETRY_TIMES
                    + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                    + matchPrice);
            return false;
        }

        if (response.isResult()) {
            log.warn("okcoin trade open air delegate done. id=" + response.getOrder_id() + ", price=" + price
                    + ", amount=" + amount + ", matchPrice=" + matchPrice + ", times=" + times);
            isSuccess = tradeOpenAirAndPending(response.getOrder_id(), price, amount, times);
        }
        else {
            log.error("okcoin trade open air fail, order_id =" + response.getOrder_id());
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin trade open air response fail " + MAX_RETRY_TIMES
                        + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                        + matchPrice);
            }
            else {
                // 市价单
                isSuccess = tradeOpenAir(price, amount, true, times + 1);
            }
        }

        return isSuccess;
    }


    private boolean tradeOpenAirAndPending(String id, String price, String amount, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpAmount = amount;
        int failTimes = 0;
        OkTradeQueryResponse queryReponse = null;
        while (null == queryReponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryReponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("okcoin query trade open air fail " + failTimes + " times. id=" + id + ", price="
                        + price + ", amount=" + amount, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryReponse) {
            log.error("[never expected] okcoin query trade open air order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryReponse.getStatus());
        switch (status) {
        case OKCOIN_ORDER_STATUS_DONE: // 成交
            isSuccess = true;
            log.warn("okcoin trade open air success. id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_DONE_HALF: // 部分成交
        case OKCOIN_ORDER_STATUS_CANCEL: // 撤单
        case OKCOIN_ORDER_STATUS_CANCEL_PENDING: // 撤单处理中
            // 市价单
            tmpPrice = computNowBuyPrice(); // 卖
            tmpAmount = computeAmount(queryReponse, status, amount);
            isSuccess = tradeOpenAir(tmpPrice, tmpAmount, true, times + 1);
            log.warn("okcoin trade open air not exactly success, so retry open. tmpPrice=" + tmpPrice
                    + ", tmpAmount=" + tmpAmount + ", price=" + price + ", amount=" + amount + ", id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_PENDING: // 等待成交
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin open air pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("okcoin trade open air still in pending. id=" + id + ", price=" + price
                        + ", amount=" + amount + ", times=" + times);
                isSuccess = tradeOpenAirAndPending(id, price, amount, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("okcoin open air pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }


    // 平多
    public boolean tradeReverse(String price, String amount, boolean matchPrice, int times) {
        boolean isSuccess = false;
        int failTimes = 0;
        OkTradeResponse response = null;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade(price, amount, matchPrice, "3");
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("okcoin reverse fail " + failTimes + " times. price=" + price + ", amount="
                        + amount + ", matchPrice=" + matchPrice, e);
            }
        }
        if (null == response) {
            log.error("[never expected] okcoin trade reverse" + MAX_RETRY_TIMES
                    + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                    + matchPrice);
            return false;
        }

        if (response.isResult()) {
            log.warn("okcoin trade reverse delegate done. id=" + response.getOrder_id() + ", price=" + price
                    + ", amount=" + amount + ", matchPrice=" + matchPrice + ", times=" + times);
            isSuccess = tradeReverseAndPending(response.getOrder_id(), price, amount, times);
        }
        else {
            log.error("okcoin trade reverse fail, order_id =" + response.getOrder_id());
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin trade reverse response fail " + MAX_RETRY_TIMES
                        + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                        + matchPrice);
            }
            else {
                // 市价单
                isSuccess = tradeReverse(price, amount, true, times + 1);
            }
        }

        return isSuccess;
    }


    private boolean tradeReverseAndPending(String id, String price, String amount, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpAmount = amount;
        int failTimes = 0;
        OkTradeQueryResponse queryReponse = null;
        while (null == queryReponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryReponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("okcoin query trade reverse fail " + failTimes + " times. id=" + id + ", price="
                        + price + ", amount=" + amount, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryReponse) {
            log.error("[never expected] okcoin query trade reverse order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryReponse.getStatus());
        switch (status) {
        case OKCOIN_ORDER_STATUS_DONE: // 成交
            isSuccess = true;
            log.warn("okcoin trade reverse success. id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_DONE_HALF: // 部分成交
        case OKCOIN_ORDER_STATUS_CANCEL: // 撤单
        case OKCOIN_ORDER_STATUS_CANCEL_PENDING: // 撤单处理中
            // 市价单
            tmpPrice = computNowBuyPrice(); // 卖
            tmpAmount = computeAmount(queryReponse, status, amount);
            isSuccess = tradeReverse(tmpPrice, tmpAmount, true, times + 1);
            log.warn("okcoin trade reverse not exactly success, so retry reverse. tmpPrice=" + tmpPrice
                    + ", tmpAmount=" + tmpAmount + ", price=" + price + ", amount=" + amount + ", id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_PENDING: // 等待成交
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin reverse pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("okcoin trade reverse still in pending. id=" + id + ", price=" + price + ", amount="
                        + amount + ", times=" + times);
                isSuccess = tradeReverseAndPending(id, price, amount, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("okcoin reverse pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }


    // 开多
    public boolean tradeOpen(String price, String amount, boolean matchPrice, int times) {
        boolean isSuccess = false;
        int failTimes = 0;
        OkTradeResponse response = null;
        while (null == response && failTimes < MAX_RETRY_TIMES) {
            try {
                response = trade(price, amount, matchPrice, "1");
            }
            catch (Exception e) {
                failTimes++;
                LockSupport.parkNanos(1000 * 1000 * 200); // wait 200ms
                log.error("okcoin open fail " + failTimes + " times. price=" + price + ", amount=" + amount
                        + ", matchPrice=" + matchPrice, e);
            }
        }
        if (null == response) {
            log.error("[never expected] okcoin trade open" + MAX_RETRY_TIMES
                    + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                    + matchPrice);
            return false;
        }

        if (response.isResult()) {
            log.warn("okcoin trade open delegate done. id=" + response.getOrder_id() + ", price=" + price
                    + ", amount=" + amount + ", matchPrice=" + matchPrice + ", times=" + times);
            isSuccess = tradeOpenAndPending(response.getOrder_id(), price, amount, times);
        }
        else {
            log.error("okcoin trade open fail, order_id =" + response.getOrder_id());
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin trade open response fail " + MAX_RETRY_TIMES
                        + " times, but still fail. price=" + price + ", amount=" + amount + ", matchPrice="
                        + matchPrice);
            }
            else {
                // 市价单
                isSuccess = tradeOpen(price, amount, true, times + 1);
            }
        }

        return isSuccess;
    }


    private boolean tradeOpenAndPending(String id, String price, String amount, int times) {
        boolean isSuccess = false;
        String tmpPrice = price;
        String tmpAmount = amount;
        int failTimes = 0;
        OkTradeQueryResponse queryReponse = null;
        while (null == queryReponse && failTimes < MAX_RETRY_TIMES) {
            try {
                queryReponse = queryTradeOrder(id);
            }
            catch (Exception e) {
                failTimes++;
                log.error("okcoin query trade open fail " + failTimes + " times. id=" + id + ", price="
                        + price + ", amount=" + amount, e);
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
            }
        }
        if (null == queryReponse) {
            log.error("[never expected] okcoin query trade open order " + MAX_RETRY_TIMES
                    + " times, but still fail.");
            return false;
        }

        int status = Integer.parseInt(queryReponse.getStatus());
        switch (status) {
        case OKCOIN_ORDER_STATUS_DONE: // 成交
            isSuccess = true;
            log.warn("okcoin trade open success. id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_DONE_HALF: // 部分成交
        case OKCOIN_ORDER_STATUS_CANCEL: // 撤单
        case OKCOIN_ORDER_STATUS_CANCEL_PENDING: // 撤单处理中
            // 市价单
            tmpPrice = computNowSellPrice(); // 卖
            tmpAmount = computeAmount(queryReponse, status, amount);
            isSuccess = tradeOpen(tmpPrice, tmpAmount, true, times + 1);
            log.warn("okcoin trade open not exactly success, so retry open. tmpPrice=" + tmpPrice
                    + ", tmpAmount=" + tmpAmount + ", price=" + price + ", amount=" + amount + ", id=" + id);
            break;
        case OKCOIN_ORDER_STATUS_PENDING: // 等待成交
            if (times > MAX_RETRY_TIMES) {
                log.error("[never expected] okcoin open pending retry " + times
                        + " times but still fail. id=" + id);
            }
            else {
                LockSupport.parkNanos(1000 * 1000 * 500); // wait 500ms
                log.warn("okcoin trade open still in pending. id=" + id + ", price=" + price + ", amount="
                        + amount + ", times=" + times);
                isSuccess = tradeOpenAndPending(id, price, amount, times + 1);
            }
            break;
        default:
            throw new UnsupportedOperationException("okcoin open pending unsupported operation. status="
                    + status + ", id=" + id);
        }

        return isSuccess;
    }


    private String computeAmount(OkTradeQueryResponse response, int status, String oldAmount) {
        if (OKCOIN_ORDER_STATUS_DONE_HALF == status) {
            return (Float.parseFloat(oldAmount) - Float.parseFloat(response.getDeal_amount())) + "";
        }
        return oldAmount;
    }


    private String computNowBuyPrice() {
        OkTicker okTicker = marketDetector.getNowOkTicker();
        return okTicker.getTicker().getBuy();
    }


    private String computNowSellPrice() {
        OkTicker okTicker = marketDetector.getNowOkTicker();
        return okTicker.getTicker().getSell();
    }


    public OkTicker getNowOkTicker() {
        return marketDetector.getNowOkTicker();
    }
}
