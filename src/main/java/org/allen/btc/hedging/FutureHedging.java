package org.allen.btc.hedging;

import static java.lang.Float.parseFloat;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.NON_DIF;
import static org.allen.btc.utils.HedgingUtils.bigDifference;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.Hedging;
import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.BitVcTradingManager;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.OkCoinTradingManager;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.market.MarketDetector;
import org.allen.btc.utils.DiffPriceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther lansheng.zj
 */
public class FutureHedging implements Hedging {

    private static Logger log = LoggerFactory.getLogger(FutureHedging.class);
    private static Logger logNew = LoggerFactory.getLogger("NewFormat");

    private boolean isShutdown = false;
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
        this(hedgingConfig, new BitVcTrading(), new OkCoinTrading());
    }


    public FutureHedging(HedgingConfig hedgingConfig, BitVcTrading vc, OkCoinTrading ok) {
        config = hedgingConfig;
        bitVc = vc;
        okCoin = ok;
        marketDetector = new MarketDetector(bitVc, okCoin, config);
        bitVcTradingManager = new BitVcTradingManager(config, marketDetector, bitVc);
        okCoinTradingManager = new OkCoinTradingManager(config, marketDetector, okCoin);
        transactionManager = new TransactionManager(hedgingConfig);

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


    public boolean isShutdown() {
        return isShutdown;
    }


    public void setShutdown(boolean isShutdown) {
        this.isShutdown = isShutdown;
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
            setShutdown(true);
            ses.shutdown();
            eveningUp.shutdown();

            marketDetector.close();
            transactionManager.close();

            bitVc.shutdown();
            okCoin.shutdown();

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


    private void positiveWave(float expectedAmount, String aBuy, String bSell, float m, float n,
            DiffPriceType dType, float wave) {
        // vcBuy的量，okSell的量
        if (expectedAmount > 0 && marketDetector.isVcBuyAmountSatisfied(expectedAmount)
                && marketDetector.isOkSellAmountSatisfied(expectedAmount)) {
            // A 开空,看A买1价格
            float totalMoney = Float.parseFloat(aBuy) * expectedAmount;
            bitVcTradingManager.tradeOpenAir(aBuy, totalMoney + "", 1);

            // B 开多,看B卖1价格(1张是$100)
            float totalPiece = Float.parseFloat(bSell) * expectedAmount / 100;
            int intTotalPiece = Math.round(totalPiece);
            okCoinTradingManager.tradeOpen(bSell, intTotalPiece + "", false, 1);

            // record
            addRecord(expectedAmount, m, n, dType, wave);
        }
    }


    private void negativeWave(float expectedAmount, String aSell, String bBuy, float m, float n,
            DiffPriceType dType, float wave) {
        // vcSell的量，okBuy的量
        if (expectedAmount > 0 && marketDetector.isVcSellAmountSatisfied(expectedAmount)
                && marketDetector.isOkBuyAmountSatisfied(expectedAmount)) {
            // A 开多,看A卖1价格
            bitVcTradingManager.tradeOpen(aSell, Float.parseFloat(aSell) * expectedAmount + "", 1);

            // B 开空,看B买1价格
            float totalPiece = Float.parseFloat(bBuy) * expectedAmount / 100;
            int intTotalPiece = Math.round(totalPiece);
            okCoinTradingManager.tradeOpenAir(bBuy, intTotalPiece + "", false, 1);

            // record
            addRecord(expectedAmount, m, n, dType, wave);
        }
    }


    // 开仓
    public void open() {
        VcTicker vcTicker = bitVcTradingManager.getNowVcTicker();
        OkTicker okTicker = okCoinTradingManager.getNowOkTicker();
        if (null == vcTicker || null == okTicker) {
            log.warn("detector not ready, wait for the next invoke.");
            return;
        }
        long nowSecond = System.currentTimeMillis() / 1000;
        if (bigDifference(vcTicker.getTime(), okTicker.getDate())) {
            log.error("kaicang vcTime big different than okTime, vcTime=" + vcTicker.getTime() + ",okTime="
                    + okTicker.getDate() + ", now=" + nowSecond);
        }
        else if (bigDifference(okTicker.getDate(), nowSecond)) {
            log.error("kaicang okTime big different than now, vcTime=" + vcTicker.getTime() + ",okTime="
                    + okTicker.getDate() + ", now=" + nowSecond);
        }
        else {
            // 汇率
            float vcRate = marketDetector.getVcRate();
            float okRate = marketDetector.getOkRate();

            float vcBuy = Float.parseFloat(vcTicker.getBuy()) * vcRate;
            float vcSell = Float.parseFloat(vcTicker.getSell()) * vcRate;
            float okSell = Float.parseFloat(okTicker.getTicker().getSell()) * okRate;
            float okBuy = Float.parseFloat(okTicker.getTicker().getBuy()) * okRate;

            // vcBuy-okSell
            float m = vcBuy - okSell;
            // vcSell-OkBuy
            float n = vcSell - okBuy;

            // 计算差价类型
            DiffPriceType dType = transactionManager.computeDiffPriceType(m, n);
            if (NON_DIF == dType) {
                return;
            }
            // 波动
            float wave = transactionManager.getWaveByDiffPriceType(dType);
            // 期望交易量
            float expectedAmount = transactionManager.computOpenOrOpenAirAmount(dType);

            // 找下一个小波动
            while (expectedAmount <= 0f) {
                // 下滑差价类型
                dType = dType.skateToNext();
                if (NON_DIF == dType) {
                    break;
                }
                // 波动
                wave = transactionManager.getWaveByDiffPriceType(dType);
                // 期望交易量
                expectedAmount = transactionManager.computOpenOrOpenAirAmount(dType);
            }

            switch (dType) {
            // M>=回+波，正向波动，A开空，A买一价格&量，B开多，B卖一价格&量
            case HUGE_DIF_POS:
            case BIG_DIF_POS:
            case NORMAL_DIF_POS:
            case SMALL_DIF_POS:
                float aPrice1 = parseFloat(vcTicker.getBuy()) - config.getSkaterPrice() / vcRate;
                float bPrice1 = parseFloat(okTicker.getTicker().getSell()) + config.getSkaterPrice() / okRate;
                // aPrice1,bPrice1和原本的币种一致
                positiveWave(expectedAmount, aPrice1 + "", bPrice1 + "", m, n, dType, wave);
                break;
            // N<=回-波，负向波动，A开多，A卖一价格&量，B开空，B买一价格&量
            case HUGE_DIF_NEGA:
            case BIG_DIF_NEGA:
            case NORMAL_DIF_NEGA:
            case SMALL_DIF_NEGA:
                float aPrice2 = parseFloat(vcTicker.getSell()) + config.getSkaterPrice() / vcRate;
                float bPrice2 = parseFloat(okTicker.getTicker().getBuy()) - config.getSkaterPrice() / okRate;
                negativeWave(expectedAmount, aPrice2 + "", bPrice2 + "", m, n, dType, wave);
                break;
            // M<回+波 and 回-波<N
            case NON_DIF:
                break;
            default:
                throw new UnsupportedOperationException("unknown DiffPriceType, dType=" + dType);

            }
        }
    }


    public void reverse() {
        VcTicker vcTicker = bitVcTradingManager.getNowVcTicker();
        OkTicker okTicker = okCoinTradingManager.getNowOkTicker();

        if (null == vcTicker || null == okTicker) {
            log.warn("detector not ready, wait for the next invoke.");
            return;
        }
        long nowSecond = System.currentTimeMillis() / 1000;
        if (bigDifference(vcTicker.getTime(), okTicker.getDate())) {
            log.error("pingcang vcTime big different than okTime, vcTime=" + vcTicker.getTime() + ",okTime="
                    + okTicker.getDate() + ", now=" + nowSecond);
        }
        else if (bigDifference(okTicker.getDate(), nowSecond)) {
            log.error("pingcang okTime big different than now, vcTime=" + vcTicker.getTime() + ",okTime="
                    + okTicker.getDate() + ", now=" + nowSecond);
        }
        else {
            // 汇率
            float vcRate = marketDetector.getVcRate();
            float okRate = marketDetector.getOkRate();

            float vcBuy = Float.parseFloat(vcTicker.getBuy()) * vcRate;
            float vcSell = Float.parseFloat(vcTicker.getSell()) * vcRate;
            float okSell = Float.parseFloat(okTicker.getTicker().getSell()) * okRate;
            float okBuy = Float.parseFloat(okTicker.getTicker().getBuy()) * okRate;

            // vcBuy-okSell
            float m = vcBuy - okSell;
            // vcSell-OkBuy
            float n = vcSell - okBuy;

            // n<=回
            if (n <= config.getReturnPrice()) {
                // 正向波动的交易平仓
                float expectedAmount = transactionManager.computeReverseOrReverseAirAmount(HUGE_DIF_POS);
                if (expectedAmount > 0f && marketDetector.isVcSellAmountSatisfied(expectedAmount)
                        && marketDetector.isOkBuyAmountSatisfied(expectedAmount)) {
                    // A平空,看A卖1价格&量
                    float aPrice = parseFloat(vcTicker.getSell()) + config.getSkaterPrice() / vcRate;
                    bitVcTradingManager.tradeReverseAir(aPrice + "", aPrice * expectedAmount + "", 1);

                    float bPrice =
                            parseFloat(okTicker.getTicker().getBuy()) - config.getSkaterPrice() / okRate;
                    // B平多,看B买1价格&量
                    float totalPiece = bPrice * expectedAmount / 100;
                    int intTotalPiece = Math.round(totalPiece);
                    okCoinTradingManager.tradeReverse(bPrice + "", intTotalPiece + "", false, 1);

                    // remove
                    transactionManager.removeRecord(expectedAmount, HUGE_DIF_POS);
                }
            }
            // m>=回
            else if (m >= config.getReturnPrice()) {
                // 负向波动的交易平仓
                float expectedAmount = transactionManager.computeReverseOrReverseAirAmount(HUGE_DIF_NEGA);
                if (expectedAmount > 0f && marketDetector.isVcBuyAmountSatisfied(expectedAmount)
                        && marketDetector.isOkSellAmountSatisfied(expectedAmount)) {
                    // A平多,看A买1价格&量
                    float aPrice = parseFloat(vcTicker.getBuy()) - config.getSkaterPrice() / vcRate;
                    bitVcTradingManager.tradeReverse(aPrice + "", aPrice * expectedAmount + "", 1);

                    // B平空,看B卖1价格&量
                    float bPrice =
                            parseFloat(okTicker.getTicker().getSell()) + config.getSkaterPrice() / okRate;
                    float totalPiece = bPrice * expectedAmount / 100;
                    int intTotalPiece = Math.round(totalPiece);
                    okCoinTradingManager.tradeReverseAir(bPrice + "", intTotalPiece + "", false, 1);

                    // remove
                    transactionManager.removeRecord(expectedAmount, HUGE_DIF_NEGA);
                }
            }
            else {
                // do nothing
            }
        }
    }


    @Override
    public void hedge() {
        // 空仓，多仓
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {

                try {
                    if (isShutdown()) {
                        return;
                    }
                    if (config.isSuspendOpen()) {
                        return;
                    }

                    open();
                }
                catch (Exception e) {
                    log.error("duo cang error.", e);
                }
            }
        }, 1000, config.getInterval(), MILLISECONDS);

        // 平空，平多
        eveningUp.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    if (isShutdown()) {
                        return;
                    }

                    if (config.isSuspendReverse()) {
                        return;
                    }

                    reverse();
                }
                catch (Exception e) {
                    log.error("ping cang error.", e);
                }
            }
        }, 1000, config.getInterval(), MILLISECONDS);
    }


    public int smallPositiveSize() {
        return transactionManager.smallPositiveSize();
    }


    public int normalPositiveSize() {
        return transactionManager.normalPositiveSize();
    }


    public int bigPositiveSize() {
        return transactionManager.bigPositiveSize();
    }


    public int hugePositiveSize() {
        return transactionManager.hugePositiveSize();
    }


    public int smallNegativeSize() {
        return transactionManager.smallNegativeSize();
    }


    public int normalNegativeSize() {
        return transactionManager.normalNegativeSize();
    }


    public int bigNegativeSize() {
        return transactionManager.bigNegativeSize();
    }


    public int hugeNegativeSize() {
        return transactionManager.hugeNegativeSize();
    }


    public float getVcRate() {
        return marketDetector.getVcRate();
    }


    public float getOkRate() {
        return marketDetector.getOkRate();
    }


    public static void main(String[] args) {
        FutureHedging futureHedging = new FutureHedging(new HedgingConfig());
        futureHedging.start();
        futureHedging.hedge();
    }
}
