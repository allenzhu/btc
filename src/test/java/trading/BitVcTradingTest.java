package trading;

import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcUserFutureRequest;
import org.allen.btc.future.bitvc.domain.VcUserFutureResponse;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class BitVcTradingTest {

    private BitVcTrading trading;


    @Before
    public void before() throws Exception {
        trading = new BitVcTrading();
        trading.start();
    }


    @Test
    public void testUserFutureInfo() throws Exception {
        String accessKey = "b061d22f-d6f3bd71-edd73c54-8464d2c5";
        String secretKey = "fba5a912-8265bb37-3d7f345c-5e5c864c";
        VcUserFutureRequest request = new VcUserFutureRequest();
        request.setAccessKey(accessKey);
        request.setSecretKey(secretKey);
        request.setCoinType("1");
        request.setCreated(System.currentTimeMillis() / 1000 + "");
        VcUserFutureResponse response = trading.userFutureInfo(request, 1000);
        System.out.println(response);
    }

}
