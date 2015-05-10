package org.allen.btc.mock;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.hedging.FutureHedging;


/**
 * @auther lansheng.zj
 */
public class TestFutureHedging {

    public static TestFutureHedging tfh;
    public static HedgingConfig config;

    private FutureHedging hedging;
    private BitVcTrading bitVc;
    private OkCoinTrading okCoin;


    // [1350,1390]
    public void initConfig() {
        config = new HedgingConfig();
        config.setRootPath("D:/test/btc_record/");
        config.setRecordFilename("record");
        config.setReturnPrice(10);
        config.setMinOpenAmount(1.5f);
        config.setMinReverseAmount(1.5f);
        config.setTotalAmount(100);

        config.setSmallDiffPrice(2);
        config.setSmallDiffPriceRatio(0.2f);
        config.setNormalDiffPrice(4);
        config.setNormalDiffPriceRatio(0.1f);
        config.setBigDiffPrice(6);
        config.setBigDiffPriceRatio(0.1f);
        config.setHugeDiffPrice(10);
        config.setHugeDiffPriceRatio(0.1f);

        config.setAccessKey("accesskey");
        config.setSecretKey("secretkey");
    }


    public void initConfigReal() {
        config = new HedgingConfig();
        config.setAccessKey("accesskey");
        config.setSecretKey("secretkey");

        // config.setRootPath("D:/test/btc_record/real2/");
        config.setRootPath("./");
        config.setRecordFilename("record");
        config.setReturnPrice(-6f);
        config.setMinOpenAmount(1.5f);
        config.setMinReverseAmount(1.5f);
        config.setSkaterPrice(0.4f);
        config.setTotalAmount(100);

        config.setSmallDiffPrice(3.5f);
        config.setSmallDiffPriceRatio(0.15f);
        config.setNormalDiffPrice(6f);
        config.setNormalDiffPriceRatio(0.2f);
        config.setBigDiffPrice(17f);
        config.setBigDiffPriceRatio(0.18f);
        config.setHugeDiffPrice(27f);
        config.setHugeDiffPriceRatio(0.22f);
    }


    public void initFutureHedging() throws Exception {
        bitVc = new BitVcTradingMock(config);
        okCoin = new OkCoinTradingMock(config);
        hedging = new FutureHedging(config, bitVc, okCoin);
    }


    public void shutdown() {
        hedging.shutdown();
    }


    public int smallPositiveSize() {
        return hedging.smallPositiveSize();
    }


    public int normalPositiveSize() {
        return hedging.normalPositiveSize();
    }


    public int bigPositiveSize() {
        return hedging.bigPositiveSize();
    }


    public int hugePositiveSize() {
        return hedging.hugePositiveSize();
    }


    public int smallNegativeSize() {
        return hedging.smallNegativeSize();
    }


    public int normalNegativeSize() {
        return hedging.normalNegativeSize();
    }


    public int bigNegativeSize() {
        return hedging.bigNegativeSize();
    }


    public int hugeNegativeSize() {
        return hedging.hugeNegativeSize();
    }


    public float vcIncome() {
        return ((BitVcTradingMock) bitVc).getTotalFunds();
    }


    public float okIncome() {
        return ((OkCoinTradingMock) okCoin).getTotalFunds();
    }


    public float getVcRate() {
        return hedging.getVcRate();
    }


    public float getOkRate() {
        return hedging.getOkRate();
    }


    public int getVcReverseOpen() {
        return ((BitVcTradingMock) bitVc).getReverseOpen();
    }


    public int getVcReverseOpenAir() {
        return ((BitVcTradingMock) bitVc).getReverseOpenAir();
    }


    public int getOkReverseOpen() {
        return ((OkCoinTradingMock) okCoin).getReverseOpen();
    }


    public int getOkReverseOpenAir() {
        return ((OkCoinTradingMock) okCoin).getReverseOpenAir();
    }


    public void before() throws Exception {
        // initConfig();
        initConfigReal();
        initFutureHedging();

    }


    public void test() throws Exception {
        hedging.start();
        hedging.hedge();
    }


    public static void main(String[] args) throws Exception {
        tfh = new TestFutureHedging();
        tfh.before();
        tfh.test();

        readCommand();
    }


    public static void readCommand() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmd = null;
        while (true) {
            try {
                cmd = br.readLine();
                if ("restart".equals(cmd)) {
                    tfh.shutdown();
                    tfh = new TestFutureHedging();
                    tfh.before();
                    tfh.test();
                    System.out.println("restart success.");
                }
                else if ("shutdown".equals(cmd)) {
                    tfh.shutdown();
                    System.out.println("shutdown success.");
                    break;
                }
                else if ("suspendopen".equals(cmd)) {
                    config.setSuspendOpen(true);
                    System.out.println("suspend open success.");
                }
                else if ("suspendreverse".equals(cmd)) {
                    config.setSuspendReverse(true);
                    System.out.println("suspend reverse success.");
                }
                else if ("income".equals(cmd)) {
                    float vcIncome = tfh.vcIncome();
                    float okIncome = tfh.okIncome();
                    float vcRate = tfh.getVcRate();
                    float okRate = tfh.getOkRate();
                    System.out.println("vc: " + vcIncome + ", rate: " + vcRate + ", rmb: " + vcIncome
                            * vcRate);
                    System.out
                        .println("ok:" + okIncome + ", rate: " + okRate + ", rmb: " + okIncome * okRate);
                    System.out.println("vc reverse open: " + tfh.getVcReverseOpen() + ", reverse open air: "
                            + tfh.getVcReverseOpenAir());
                    System.out.println("ok reverse open: " + tfh.getOkReverseOpen() + ", reverse open air: "
                            + tfh.getOkReverseOpenAir());
                    System.out
                        .println("ok:" + okIncome + ", rate: " + okRate + ", rmb: " + okIncome * okRate);
                    System.out.println("smallPos: " + tfh.smallPositiveSize() + ", normalPos: "
                            + tfh.normalPositiveSize() + ", bigPos: " + tfh.bigPositiveSize() + ", hugePos: "
                            + tfh.hugePositiveSize());
                    System.out.println("smallNega: " + tfh.smallNegativeSize() + ", normalNega: "
                            + tfh.normalNegativeSize() + ", bigNega: " + tfh.bigNegativeSize()
                            + ", hugeNega: " + tfh.hugeNegativeSize());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("cmd over.");
    }
}
