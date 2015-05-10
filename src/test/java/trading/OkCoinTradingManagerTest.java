package trading;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.OkCoinTradingManager;
import org.allen.btc.future.okcoin.domain.OkTradeCancelResponse;
import org.allen.btc.future.okcoin.domain.OkTradeQueryResponse;
import org.allen.btc.future.okcoin.domain.OkUserFutureRequest;
import org.allen.btc.future.okcoin.domain.OkUserFutureResponse;
import org.allen.btc.market.MarketDetector;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class OkCoinTradingManagerTest {

    private OkCoinTrading okCoin;
    private BitVcTrading bitVc;
    private OkCoinTradingManager okCoinTradingManager;
    private HedgingConfig config;


    @Before
    public void before() throws Exception {
        config = new HedgingConfig();
        config.setAccessKey("6dc36238-248a-433c-8f5d-d20c469cf12b");
        config.setSecretKey("87539F49B952458BC55A764096AA8FFD");
        // config.setAccessKey("abc");
        // config.setSecretKey("123");

        okCoin = new OkCoinTrading();
        okCoin.start();
        bitVc = new BitVcTrading();
        bitVc.start();

        MarketDetector marketDetector = new MarketDetector(bitVc, okCoin, config);
        marketDetector.start();

        okCoinTradingManager = new OkCoinTradingManager(config, marketDetector, okCoin);
    }


    public void testuserFutureInfo() throws Exception {
        OkUserFutureRequest request = new OkUserFutureRequest();
        request.setAccessKey(config.getAccessKey());
        request.setSecretKey(config.getSecretKey());
        OkUserFutureResponse response = okCoin.userFutureInfo(request, 1000);
        System.out.println(response);
    }


    // 开多
    public void testTradeOpen() throws Exception {
        // $
        String price = "237.49";
        String amount = "1";
        boolean result = okCoinTradingManager.tradeOpen(price, amount, false, 1);
        System.out.println(result);
    }


    public void testQueryTradeOrder() throws Exception {
        String id = "313563464";
        OkTradeQueryResponse response = okCoinTradingManager.queryTradeOrder(id);
        System.out.println(response);
    }


    // 平多
    public void testTradeReverse() throws Exception {
        String price = "237.03";
        String amount = "1";
        boolean result = okCoinTradingManager.tradeReverse(price, amount, false, 1);
        System.out.println(result);
    }


    // 开空
    public void testTradeOpenAir() throws Exception {
        String price = "241.30";
        String amount = "1";
        boolean result = okCoinTradingManager.tradeOpenAir(price, amount, false, 1);
        System.out.println(result);
    }


    @Test
    public void testTradeReverseAir() {
        String price = "241.25";
        String amount = "1";
        boolean result = okCoinTradingManager.tradeReverseAir(price, amount, false, 1);
        System.out.println(result);
    }


    public void testCancel() throws Exception {
        String id = "313563464";
        OkTradeCancelResponse response = okCoinTradingManager.cancel(id);
        System.out.println(response);
    }

}
