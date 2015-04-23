package org.allen.btc.market;

import static org.allen.btc.utils.HedgingUtils.bigDifference;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcDepths;
import org.allen.btc.future.bitvc.domain.VcDeputer;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkDepths;
import org.allen.btc.future.okcoin.domain.OkDeputer;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.hedging.FutureHedging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther lansheng.zj
 */
public class MarketDetector {

    public static Logger log = LoggerFactory.getLogger(FutureHedging.class);
    public static final int LIMIT_LEN = 100;

    private HedgingConfig hedgingConfig;

    private BitVcTrading bitVc;
    private OkCoinTrading okCoin;

    private LinkedList<VcTicker> vcSnapshot;
    private LinkedList<OkTicker> okSnapshot;

    private volatile VcDepths vcDepths;
    private volatile OkDepths okDepths;

    private ScheduledExecutorService ses;


    public MarketDetector(BitVcTrading vc, OkCoinTrading ok, HedgingConfig config) {
        bitVc = vc;
        okCoin = ok;
        hedgingConfig = config;
        okSnapshot = new LinkedList<OkTicker>();
        vcSnapshot = new LinkedList<VcTicker>();

        ses = Executors.newScheduledThreadPool(4, new ThreadFactory() {
            AtomicInteger index = new AtomicInteger(0);


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "market-detector-" + index.getAndIncrement());
            }
        });
    }


    public void start() {
        // vc ticker
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    VcTicker ticker = bitVc.getTicker(100);
                    vcSnapshot.add(ticker);
                    if (vcSnapshot.size() > LIMIT_LEN) {
                        vcSnapshot.removeFirst();
                    }
                }
                catch (Exception e) {
                    log.error("vc ticker request error.", e);
                }

            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        // vc depth
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    VcDepths depths = bitVc.getDepths(100);
                    vcDepths = depths;
                }
                catch (Exception e) {
                    log.error("vc depth request error.", e);
                }

            }

        }, 0, 100, TimeUnit.MILLISECONDS);

        // ok ticker
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    OkTicker ticker = okCoin.getTicker(100);
                    okSnapshot.add(ticker);
                    if (okSnapshot.size() > LIMIT_LEN) {
                        okSnapshot.removeFirst();
                    }
                }
                catch (Exception e) {
                    log.error("ok ticker request error.", e);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        // ok depth
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    OkDepths depths = okCoin.getDepths(100);
                    okDepths = depths;
                }
                catch (Exception e) {
                    log.error("ok depth request error.", e);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }


    public void close() {
        ses.shutdown();
    }


    public VcTicker getNowVcTicker() {
        return vcSnapshot.isEmpty() ? null : vcSnapshot.getLast();
    }


    public OkTicker getNowOkTicker() {
        return okSnapshot.isEmpty() ? null : okSnapshot.getLast();
    }


    public VcDepths getVcDepths() {
        return vcDepths;
    }


    public OkDepths getOkDepths() {
        return okDepths;
    }


    // vc buy satisfied
    public boolean isVcBuyAmountSatisfied(float expectedAmount) {
        VcDeputer deputer = vcDepths.getBids().get(0);
        float amount = deputer.getAmount();
        return amount >= expectedAmount;
    }


    // vc sell satisfied
    public boolean isVcSellAmountSatisfied(float expectedAmount) {
        VcDeputer deputer = vcDepths.getAsks().get(0);
        float amount = deputer.getAmount();
        return amount >= expectedAmount;
    }


    // Ok buy satisfied
    public boolean isOkBuyAmountSatisfied(float expectedAmount) {
        OkDeputer deputer = okDepths.getBids().get(0);
        float amount = deputer.getAmount();
        return amount >= expectedAmount;
    }


    // Ok sell satisfied
    public boolean isOkSellAmountSatisfied(float expectedAmount) {
        OkDeputer deputer = okDepths.getAsks().get(0);
        float amount = deputer.getAmount();
        return amount >= expectedAmount;
    }


    public static void main(String[] args) throws InterruptedException {
        MarketDetector detector =
                new MarketDetector(new BitVcTrading(), new OkCoinTrading(), new HedgingConfig());
        detector.start();

        long okTime = 0;
        long vcTime = 0;
        int total = 0;
        int sameTimes = 0;
        int diffTimes = 0;
        int diffNow = 0;

        while (true) {
            OkTicker okTicker = detector.getNowOkTicker();
            VcTicker vcTicker = detector.getNowVcTicker();

            if (null == okTicker || null == vcTicker) {
                System.out.println("wait for a second.");
                Thread.sleep(1000);
                continue;
            }
            if (okTime == okTicker.getDate() && vcTime == vcTicker.getTime()) {
                Thread.sleep(10);
                continue;
            }

            total++;
            okTime = okTicker.getDate();
            vcTime = vcTicker.getTime();

            long now = System.currentTimeMillis() / 1000;
            if (bigDifference(okTime, vcTime)) {
                // System.err.println("big different. okTime=" + okTime +
                // ", vcTime=" + vcTime);
                diffTimes++;
            }
            else if (bigDifference(okTime, now)) {
                // System.err.println("big different. okTime=" + okTime +
                // ", now=" + now);
                diffNow++;
            }
            else {
                // System.out.println("same. okTime=" + okTime + ", vcTime=" +
                // vcTime);
                sameTimes++;
            }

            if (total % 20 == 0) {
                System.out.println("total: " + total + ", same ratio: " + (float) (sameTimes) / (float) total
                        + ", diffTimes ratio: " + (float) (diffTimes) / (float) total + ", diffNow ratio: "
                        + (float) (diffNow) / (float) total);
            }
        }
    }

}
