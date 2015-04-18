package trading;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.market.MarketDetector;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class TestMarket {

    private MarketDetector detector;


    @Before
    public void before() {
        detector = new MarketDetector(new BitVcTrading(), new OkCoinTrading(), new HedgingConfig());
        detector.start();
    }


    @Test
    public void test() {
        long okTime = 0;
        long vcTime = 0;

        while (true) {
            OkTicker okTicker = detector.getNowOkTicker();
            VcTicker vcTicker = detector.getNowVcTicker();

            if (null == okTicker || null == vcTicker) {
                System.out.println("wait for a second.");
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                }
                continue;
            }
            if (okTime == okTicker.getDate() && vcTime == vcTicker.getTime()) {
                continue;
            }

            okTime = okTicker.getDate();
            vcTime = vcTicker.getTime();

            long now = System.currentTimeMillis() / 1000;
            if (bigDifference(okTime, vcTime)) {
                System.err.println("big different. okTime=" + okTime + ", vcTime=" + vcTime);
            }
            else if (bigDifference(okTime, now)) {
                System.err.println("big different. okTime=" + okTime + ", now=" + now);
            }
            else {
                System.out.println("same. okTime=" + okTime + ", vcTime=" + vcTime);
            }
        }
    }


    private boolean bigDifference(long l1, long l2) {
        if (Math.abs(l1 - l2) > 1) {
            return true;
        }
        return false;
    }
}
