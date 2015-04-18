package trading;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkDepths;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkUserFutureRequest;
import org.allen.btc.future.okcoin.domain.OkUserFutureResponse;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class OkCoinTradingTest {

    private OkCoinTrading trading;
    private BitVcTrading vc;
    private boolean isShutDown = false;


    @Before
    public void before() throws Exception {
        trading = new OkCoinTrading();
        trading.start();

        vc = new BitVcTrading();
        vc.start();
    }


    @Test
    public void testTicker() throws Exception {
        OkTicker ticker = trading.getTicker(1000);
        System.out.println(ticker);
    }


    @Test
    public void testuserFutureInfo() throws Exception {
        OkUserFutureRequest request = new OkUserFutureRequest();
        request.setAccessKey("");
        request.setSecretKey("");
        OkUserFutureResponse response = trading.userFutureInfo(request, 1000);
        System.out.println(response);
    }


    @Test
    public void testGetDepth() throws Exception {
        OkDepths okDepths = trading.getDepths(1000);
        System.out.println(okDepths);
    }


    @Test
    public void loop() throws Exception {

        final TreeSet<OkTicker> snapshot = new TreeSet<OkTicker>(new Comparator<OkTicker>() {
            @Override
            public int compare(OkTicker o1, OkTicker o2) {
                if (o1.getDate() > o2.getDate())
                    return 1;
                else if (o1.getDate() < o2.getDate()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(20);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isShutDown) {
                    try {
                        long begin = System.currentTimeMillis();
                        OkTicker ticker = trading.getTicker(1000);
                        // VcTicker ticker = vc.getTicker(1000);
                        snapshot.add(ticker);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);

        Thread.sleep(8000);
        isShutDown = true;
        ses.shutdown();

        Iterator<OkTicker> iterator = snapshot.iterator();
        while (iterator.hasNext()) {
            OkTicker tita = iterator.next();
            System.out.println(tita);
        }
    }
}
