package trading;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.BitVcTradingManager;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.market.MarketDetector;
import org.junit.Before;
import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class BitVcTradingManagerTest {

    private BitVcTrading bitVc;
    private OkCoinTrading okCoin;
    private HedgingConfig config;
    private BitVcTradingManager bitVcTradingManager;


    @Before
    public void before() throws Exception {
        config = new HedgingConfig();
        config.setAccessKey("1d29464d-13f68cb3-0240da12-7dfa54ee");
        config.setSecretKey("b021ebc3-62e8d473-05bdc0a0-7ecb9522");

        bitVc = new BitVcTrading();
        bitVc.start();

        okCoin = new OkCoinTrading();
        okCoin.start();

        MarketDetector marketDetector = new MarketDetector(bitVc, okCoin, config);
        marketDetector.start();

        bitVcTradingManager = new BitVcTradingManager(config, marketDetector, bitVc);
    }


    public void testTradeOpen() throws Exception {
        String price = "1407.7";
        String money = "100";
        boolean result = bitVcTradingManager.tradeOpen(price, money, 1);
        System.out.println(result);
    }


    public void testQueryTradeOrder() throws Exception {
        // String id = "64709344";
        String id = "64749236";

        VcOrderResponse response = bitVcTradingManager.queryTradeOrder(id);
        System.out.println(response);
    }


    public void testTradeReverse() throws Exception {
        String price = "1408.7";
        String money = "100";
        boolean result = bitVcTradingManager.tradeReverse(price, money, 1);
        System.out.println(result);
    }


    public void testTradeOpenAir() throws Exception {
        String price = "1491.02";
        String money = "100";
        boolean result = bitVcTradingManager.tradeOpenAir(price, money, 1);
        System.out.println(result);
    }


    @Test
    public void testTradeReverseAir() throws Exception {
        String price = "1492.40";
        String money = "200";
        boolean result = bitVcTradingManager.tradeReverseAir(price, money, 1);
        System.out.println(result);
    }
}
