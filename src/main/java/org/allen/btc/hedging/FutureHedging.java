package org.allen.btc.hedging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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

import org.allen.btc.Hedging;
import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.utils.DiffPriceResult;
import org.allen.btc.utils.HedgingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther lansheng.zj
 */
public class FutureHedging implements Hedging {

    private static Logger log = LoggerFactory.getLogger(FutureHedging.class);
    private static Logger logNew = LoggerFactory.getLogger("NewFormat");

    private HedgingConfig config;
    private BitVcTrading bitVc;
    private OkCoinTrading okCoin;
    private ScheduledExecutorService ses;
    private ExecutorService executorService;


    public FutureHedging(HedgingConfig config) {
        this.config = config;
        bitVc = new BitVcTrading();
        okCoin = new OkCoinTrading();

        ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger();


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "hedge-runner-" + index.getAndIncrement());
            }
        });
        executorService = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger();


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "platform-worker-" + index.getAndIncrement());
            }

        });
    }


    @Override
    public void start() {
        try {
            bitVc.start();
            okCoin.start();
        }
        catch (Exception e) {
            log.error("init FutureHedging error.", e);
        }
    }


    @Override
    public void shutdown() {
        try {
            bitVc.shutdown();
            okCoin.shutdown();
        }
        catch (Exception e) {
            log.error("shutdown FutureHedging error.", e);
        }
    }


    @Override
    public void hedge() {
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                CompareResult compareResult = comparePrice();
                if (compareResult.isSuccess()) {
                    DiffPriceResult diffResult =
                            HedgingUtils.computeDiff(compareResult.getDiffPrice(), config);
                    switch (diffResult.getType()) {
                    // huge
                    case HUGE_DIF_POS:
                    case HUGE_DIF_NEGA:
                        
                        break;
                    // big
                    case BIG_DIF_POS:
                    case BIG_DIF_NEGA:

                        break;
                    // normal
                    case NORMAL_DIF_POS:
                    case NORMAL_DIF_NEGA:

                        break;
                    // small
                    case SMALL_DIF_POS:
                    case SMALL_DIF_NEGA:

                        break;
                    // non
                    case NON_DIF:

                        break;
                    }
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


    public CompareResult comparePrice() {
        CompareResult result = new CompareResult();
        Future<Float> r1 = executorService.submit(new Callable<Float>() {

            @Override
            public Float call() throws Exception {
                VcTicker ticker = bitVc.getTicker(config.getTimeout());
                return Float.parseFloat(ticker.getLast());
            }
        });

        Future<Float> r2 = executorService.submit(new Callable<Float>() {

            @Override
            public Float call() throws Exception {
                OkTicker ticker = okCoin.getTicker(config.getTimeout());
                return Float.parseFloat(ticker.getTicker().getLast());
            }
        });
        try {
            float f1 = r1.get(config.getTimeout(), TimeUnit.MILLISECONDS).floatValue();
            try {
                float f2 = r2.get(config.getTimeout(), TimeUnit.MILLISECONDS).floatValue();

                result.setSuccess(true);
                result.setDiffPrice(f1 - f2);
            }
            catch (TimeoutException e) {
                result.setSuccess(false);
                result.setMsg("bitvc request timeout.");
            }
            catch (ExecutionException e) {
                result.setSuccess(false);
                result.setMsg("bitvc request exception." + e.getMessage());
            }
            catch (InterruptedException e) {
                result.setSuccess(false);
                result.setMsg("bitvc request interrupt.");
            }
        }
        catch (TimeoutException e) {
            result.setSuccess(false);
            result.setMsg("bitvc request timeout.");
        }
        catch (ExecutionException e) {
            result.setSuccess(false);
            result.setMsg("bitvc request exception." + e.getMessage());
        }
        catch (InterruptedException e) {
            result.setSuccess(false);
            result.setMsg("bitvc request interrupt.");
        }

        return result;
    }


    public static void main(String[] args) {
        FutureHedging futureHedging = new FutureHedging(new HedgingConfig());
        futureHedging.start();
        futureHedging.hedge();
    }
}
