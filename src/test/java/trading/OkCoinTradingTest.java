package trading;

import org.allen.btc.future.okcoin.OkCoinTrading;
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


    @Before
    public void before() throws Exception {
        trading = new OkCoinTrading();
        trading.start();
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
}
