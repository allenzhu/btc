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
import org.allen.btc.future.bitvc.BitVcTradingManager;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.OkCoinTradingManager;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkTradeRequest;
import org.allen.btc.future.okcoin.domain.OkTradeResponse;
import org.allen.btc.market.MarketDetector;
import org.allen.btc.utils.DiffPriceResult;
import org.allen.btc.utils.DiffPriceType;
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
    private BitVcTradingManager bitVcTradingManager;
    private OkCoinTradingManager okCoinTradingManager;


    public FutureHedging(HedgingConfig hedgingConfig) {
        config = hedgingConfig;
        bitVc = new BitVcTrading();
        okCoin = new OkCoinTrading();
        marketDetector = new MarketDetector(bitVc, okCoin, config);
        bitVcTradingManager = new BitVcTradingManager(config, marketDetector, bitVc);
        okCoinTradingManager = new OkCoinTradingManager(config, marketDetector, okCoin);

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


    private void addRecord(float expectedAmount, float m, float n, DiffPriceType dType, float wave) {
        // 记录Record
        Record record = new Record();
        record.setAmount(expectedAmount);
        record.setM(m);
        record.setN(n);
        record.setReturnPrice(config.getReturnPrice());
        record.setType(dType);
        record.setWave(wave);
        transactionManager.addRecord(record, dType);
    }


    private void positiveWave(float expectedAmount, float aBuy, String bSell, float m, float n,
            DiffPriceType dType, float wave) {
        // vcBuy的量，okSell的量
        if (marketDetector.isVcBuyAmountSatisfied(expectedAmount)
                && marketDetector.isOkSellAmountSatisfied(expectedAmount)) {
            // A 开空,看A买1价格
            boolean isVcSuccess = bitVcTradingManager.tradeOpenAir(aBuy + "", aBuy * expectedAmount + "", 1);

            // B 开多,看B卖1价格
            boolean isOkSuccess = okCoinTradingManager.tradeOpen(bSell, expectedAmount + "", false, 1);

            // record
            addRecord(expectedAmount, m, n, dType, wave);
        }
    }


    private void negativeWave(float expectedAmount, float aSell, String bBuy, float m, float n,
            DiffPriceType dType, float wave) {
        // vcSell的量，okBuy的量
        if (marketDetector.isVcSellAmountSatisfied(expectedAmount)
                && marketDetector.isOkBuyAmountSatisfied(expectedAmount)) {
            // A 开多,看A卖1价格
            boolean isVcSuccess = bitVcTradingManager.tradeOpen(aSell + "", aSell * expectedAmount + "", 1);

            // B 开空,看B买1价格
            boolean isOkSuccess = okCoinTradingManager.tradeOpenAir(bBuy, expectedAmount + "", false, 1);

            // record
            addRecord(expectedAmount, m, n, dType, wave);
        }
    }


    @Override
    public void hedge() {
        // 空仓，多仓
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                VcTicker vcTicker = bitVcTradingManager.getNowVcTicker();
                OkTicker okTicker = okCoinTradingManager.getNowOkTicker();
                if (null == vcTicker || null == okTicker) {
                    log.warn("detector not ready, wait for the next invoke.");
                    return;
                }
                long nowSecond = System.currentTimeMillis() / 1000;
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

                    DiffPriceType dType = transactionManager.computeDiffPriceType(m, n);
                    // 波
                    float wave = transactionManager.getWaveByDiffPriceType(dType);
                    // 期望交易量
                    float expectedAmount = transactionManager.computAmount(dType);

                    switch (dType) {
                    // M>=回+波
                    case HUGE_DIF_POS:
                    case BIG_DIF_POS:
                    case NORMAL_DIF_POS:
                    case SMALL_DIF_POS:
                        positiveWave(expectedAmount, vcBuy, okTicker.getTicker().getSell(), m, n, dType, wave);
                        break;
                    // N<=回-波
                    case HUGE_DIF_NEGA:
                    case BIG_DIF_NEGA:
                    case NORMAL_DIF_NEGA:
                    case SMALL_DIF_NEGA:
                        negativeWave(expectedAmount, vcSell, okTicker.getTicker().getBuy(), m, n, dType, wave);
                        break;
                    // M<回+波 and 回-波<N
                    case NON_DIF:
                        break;
                    default:
                        throw new UnsupportedOperationException("unknown DiffPriceType, dType=" + dType);

                    }
                    // M<回+波 and 回-波<N
                    if (config.getReturnPrice() + config.getSmallDiffPrice() > m
                            && config.getReturnPrice() - config.getSmallDiffPrice() < n) {
                        // continue;
                    }

                    // M>=回+波
                    else if (m >= config.getReturnPrice() + config.getSmallDiffPrice()) {
                        // 正溢

                        // vcBuy的量，okSell的量
                        if (marketDetector.isVcBuyAmountSatisfied(expectedAmount)
                                && marketDetector.isOkSellAmountSatisfied(expectedAmount)) {
                            // A 开空,看A买1价格
                            boolean isVcSuccess =
                                    bitVcTradingManager.tradeOpenAir(vcTicker.getBuy(), vcBuy
                                            * expectedAmount + "", 1);

                            // B 开多,看B卖1价格
                            boolean isOkSuccess =
                                    okCoinTradingManager.tradeOpen(okTicker.getTicker().getSell(),
                                        expectedAmount + "", false, 1);

                            // 记录Record
                            Record record = new Record();
                            record.setAmount(expectedAmount);
                            record.setM(m);
                            record.setN(n);
                            record.setReturnPrice(config.getReturnPrice());
                            record.setType(dType);
                            record.setWave();
                            transactionManager.addRecord(record, dType);
                        }
                    }
                    // N<=回-波
                    else if (n <= config.getReturnPrice() - config.getSmallDiffPrice()) {
                        // 负溢

                        // vcSell的量，okBuy的量
                        if (marketDetector.isVcSellAmountSatisfied(expectedAmount)
                                && marketDetector.isOkBuyAmountSatisfied(expectedAmount)) {
                            // A 开多,看A卖1价格
                            boolean isVcSuccess =
                                    bitVcTradingManager.tradeOpen(vcTicker.getSell(), vcSell * expectedAmount
                                            + "", 1);

                            // B 开空,看B买1价格
                            boolean isOkSuccess =
                                    okCoinTradingManager.tradeOpenAir(okTicker.getTicker().getBuy(),
                                        expectedAmount + "", false, 1);
                            // record
                            addRecord(expectedAmount, m, n, dType, wave);
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

                        // 最小平仓量
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
