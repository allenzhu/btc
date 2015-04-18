package org.allen.btc.hedging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL_F_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.Constants;
import org.allen.btc.Hedging;
import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkTradeRequest;
import org.allen.btc.future.okcoin.domain.OkTradeResponse;
import org.allen.btc.market.MarketDetector;
import org.allen.btc.utils.DiffPriceResult;
import org.allen.btc.utils.FileUtils;
import org.allen.btc.utils.HedgingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class FutureHedging implements Hedging {

    private static Logger log = LoggerFactory.getLogger(FutureHedging.class);
    private static Logger logNew = LoggerFactory.getLogger("NewFormat");

    private HedgingConfig config;
    private BitVcTrading bitVc;
    private OkCoinTrading okCoin;
    private MarketDetector marketDetector;

    private ScheduledExecutorService ses;
    private ScheduledExecutorService eveningUp;

    private TransactionManager transactionManager;


    public FutureHedging(HedgingConfig config) {
        this.config = config;
        bitVc = new BitVcTrading();
        okCoin = new OkCoinTrading();
        marketDetector = new MarketDetector(bitVc, okCoin, config);

        ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger();


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "hedge-runner-" + index.getAndIncrement());
            }
        });
        eveningUp = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger();


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "eveningUp-" + index.getAndIncrement());
            }
        });
    }


    @Override
    public void start() {
        try {
            transactionManager.start();

            bitVc.start();
            okCoin.start();
            marketDetector.start();
        }
        catch (Exception e) {
            log.error("init FutureHedging error.", e);
        }
    }


    @Override
    public void shutdown() {
        try {
            marketDetector.close();
            bitVc.shutdown();
            okCoin.shutdown();

            transactionManager.close();
        }
        catch (Exception e) {
            log.error("shutdown future hedging error.", e);
        }
    }


    @Override
    public void hedge() {
        // 空仓，多仓
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                long nowSecond = System.currentTimeMillis() / 1000;
                VcTicker vcTicker = marketDetector.getNowVcTicker();
                OkTicker okTicker = marketDetector.getNowOkTicker();
                if (HedgingUtils.bigDifference(vcTicker.getTime(), okTicker.getDate())) {
                    // TODO
                }
                else if (HedgingUtils.bigDifference(okTicker.getDate(), nowSecond)) {
                    // TODO
                }
                else {
                    float vcBuy = Float.parseFloat(vcTicker.getBuy());
                    float vcSell = Float.parseFloat(vcTicker.getSell());
                    float okSell = Float.parseFloat(okTicker.getTicker().getSell());
                    float okBuy = Float.parseFloat(okTicker.getTicker().getBuy());

                    // vcBuy-okSell
                    float m = vcBuy - okSell;
                    // vcSell-OkBuy
                    float n = vcSell - okBuy;
                    float expectedAmount = transactionManager.computAmount();

                    // M<回+波 and 回-波<N
                    if (config.getReturnPrice() + config.getSmallDiffPrice() > m
                            && config.getReturnPrice() - config.getSmallDiffPrice() < n) {
                        // continue;
                    }
                    // M>=回+波
                    else if (m >= config.getReturnPrice() + config.getSmallDiffPrice()) {
                        // 正溢

                        // TODO 量是否满足
                        if (marketDetector.isOkBuyAmountSatisfied(expectedAmount, okBuy)
                                && marketDetector.isVcSellAmountSatisfied(expectedAmount, vcSell)) {
                            // A 空仓,看B买1
                            VcOrderRequest vcRequest = new VcOrderRequest();
                            vcRequest.setAccessKey(config.getAccessKey());
                            vcRequest.setCoinType(1 + "");
                            vcRequest.setContractType("week");
                            vcRequest.setCreated(System.currentTimeMillis() / 1000 + "");
                            vcRequest.setOrderType();
                            vcRequest.setTradeType();
                            vcRequest.setPrice();
                            vcRequest.setMoney();

                            try {
                                VcOrderResponse response = bitVc.trade(vcRequest, 1000);

                            }
                            catch (Exception e) {
                                log.error("bitVc trade fail.", e);
                            }

                            // B 多仓,看A卖1
                            OkTradeRequest okRequest = new OkTradeRequest();
                            okRequest.setAccessKey(config.getAccessKey());
                            okRequest.setSecretKey(config.getSecretKey());
                            okRequest.setSymbol(PARAM_OKCOIN_SYMBOL_F_VALUE);
                            okRequest.setAmount(expectedAmount + "");
                            okRequest.setContract_type("this_week");
                            okRequest.setLever_rate();
                            okRequest.setMatch_price();
                            okRequest.setPrice();
                            okRequest.setType();

                            try {
                                OkTradeResponse response = okCoin.trade(okRequest, 1000);

                            }
                            catch (Exception e) {
                                log.error("okCoin trade fail.", e);
                            }

                            // FIXME 期货中是否会出现量不足交易失败的情况
                            // 如果当前一个平台交易失败立即以市价单成交

                            // 记录Record
                            Record record = new Record();
                            record.setAmount(expectedAmount);
                            record.setM(m);
                            record.setN(n);
                            record.setReturnPrice(config.getReturnPrice());
                            record.setWave();
                            transactionManager.addPositive(record);
                        }
                    }
                    // N<=回-波
                    else if (n <= config.getReturnPrice() - config.getSmallDiffPrice()) {
                        // 负溢

                        // TODO 量是否满足
                        if (marketDetector.isOkSellAmountSatisfied(expectedAmount, okSell)
                                && marketDetector.isVcBuyAmountSatisfied(expectedAmount, vcBuy)) {
                            // A 多仓,看B卖1
                            // B 空仓,看A买1

                        }
                    }
                }
            }

        }, 1000, config.getInterval(), MILLISECONDS);

        // 平仓
        eveningUp.scheduleAtFixedRate(new Runnable() {
            public void run() {
                // FIXME
                CompareResult m = new CompareResult();
                CompareResult n = new CompareResult();
                compareBuyDecSellPrice(m, n);
                if (m.isSuccess() && n.isSuccess()) {
                    // n<=回
                    if (n.getDiffPrice() <= config.getReturnPrice()) {
                        // 平
                        // A 多仓,看A卖1
                        // B 空仓,看B买1

                    }
                    // m>=回
                    else if (m.getDiffPrice() >= config.getReturnPrice()) {
                        // 平
                        // A 空仓,看A买1
                        // B 多仓,看B卖1

                    }
                    else {
                        // do nothing
                    }
                }
                else {

                }
            }
        }, 1000, config.getInterval(), MILLISECONDS);
    }


    @SuppressWarnings("deprecation")
    public void mock() {
        // CompareResult result = comparePrice();
        // if (result.isSuccess()) {
        // log.warn(result.getDiffPrice() + "");
        // logNew.warn("[" + System.currentTimeMillis() + "," +
        // result.getDiffPrice() + "]");
        // }

        try {
            VcTicker ticker = bitVc.getTicker(config.getTimeout());
            System.out.println(new Date().toLocaleString() + " " + ticker.getLast());

            // OkTicker ticker = okCoin.getTicker(config.getTimeout());
            // System.out.println(new Date().toLocaleString() + " " +
            // ticker.getTicker().getLast());
        }
        catch (Exception e) {
        }
    }


    public static void main(String[] args) {
        FutureHedging futureHedging = new FutureHedging(new HedgingConfig());
        futureHedging.start();
        futureHedging.hedge();
    }
}
