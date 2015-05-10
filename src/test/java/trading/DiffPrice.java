package trading;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class DiffPrice {

    private BitVcTrading vc;
    private OkCoinTrading ok;
    private float vcRate;
    private float okRate;
    private ExecutorService es;
    private long count;
    private long total;
    private float average;
    private PrintWriter pw;


    @Before
    public void before() throws Exception {
        es = Executors.newFixedThreadPool(20);
        vc = new BitVcTrading();
        ok = new OkCoinTrading();

        vc.start();
        ok.start();

        vcRate = 1;
        okRate = ok.exchangeRate(1000);

        String file = "D:/test/btc_record/diff";
        pw = new PrintWriter(new FileWriter(file));
    }


    private void close() throws Exception {
        vc.shutdown();
        ok.shutdown();
        pw.close();
        es.shutdownNow();
    }


    @Test
    public void differPrice() {
        while (true) {
            try {
                // vc buy
                Future<Float> vcFuture = es.submit(new Callable<Float>() {
                    @Override
                    public Float call() throws Exception {
                        VcTicker vcTicker = vc.getTicker(1000);
                        return Float.parseFloat(vcTicker.getBuy());
                    }
                });

                // ok sell
                Future<Float> okFuture = es.submit(new Callable<Float>() {
                    @Override
                    public Float call() throws Exception {
                        OkTicker okTicker = ok.getTicker(1000);
                        return Float.parseFloat(okTicker.getTicker().getSell());
                    }
                });

                float vcBuy = vcFuture.get(10, SECONDS) * vcRate;
                long st1 = System.currentTimeMillis();
                float okSell = okFuture.get(10, SECONDS) * okRate;
                long st2 = System.currentTimeMillis();

                if ((st2 - st1) > 1000) {
                    System.out.println("expired.");
                }
                else {
                    float m = vcBuy - okSell;
                    pw.println("[" + st1 + "," + m + "]");
                    pw.flush();
                    total += m;
                    count++;
                    average = (float) total / (float) count;
                    if (count % 100 == 0)
                        System.out.println("diff average:" + average);
                }

                Thread.sleep(500);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
