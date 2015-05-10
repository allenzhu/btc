package trading;

import org.allen.btc.HedgingConfig;
import org.allen.btc.hedging.FutureHedging;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class FutureHedgingTest {

    private FutureHedging hedging;


    @Before
    public void before() {
        HedgingConfig config = new HedgingConfig();
        config.setTotalAmount(10);
        config.setBigDiffPriceRatio(0.1f);
        config.setHugeDiffPriceRatio(0.1f);
        config.setNormalDiffPriceRatio(0.1f);
        config.setSmallDiffPriceRatio(0.1f);
        config.setMinOpenAmount(0.4f);
        config.setMinReverseAmount(0.4f);

        hedging = new FutureHedging(config);
        hedging.start();
    }


    @Test
    public void testOpen() {
        hedging.open();
    }


    public void testReverse() {
        hedging.reverse();
    }
}