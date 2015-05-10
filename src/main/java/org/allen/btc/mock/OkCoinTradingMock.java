package org.allen.btc.mock;

import static java.lang.Float.parseFloat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.HedgingConfig;
import org.allen.btc.future.okcoin.OkCoinTrading;
import org.allen.btc.future.okcoin.domain.OkDepths;
import org.allen.btc.future.okcoin.domain.OkDepthsOriginal;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkTradeQueryRequest;
import org.allen.btc.future.okcoin.domain.OkTradeQueryResponse;
import org.allen.btc.future.okcoin.domain.OkTradeRequest;
import org.allen.btc.future.okcoin.domain.OkTradeResponse;
import org.allen.btc.future.okcoin.domain.OkUserFutureResponse;
import org.allen.btc.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class OkCoinTradingMock extends OkCoinTrading {

    private static Logger log = LoggerFactory.getLogger(OkCoinTradingMock.class);

    // $ : 6.2015
    private float totalFunds = 32250.26203337902f;

    // private volatile OkTicker okTicker;
    // private volatile OkDepths okDepths;
    private ScheduledExecutorService ses;
    private Map<String, OkTradeQueryResponse> order;
    private AtomicInteger id;
    private String tickerFile;
    private String depthFile;
    // private BufferedReader tickerBr;
    // private BufferedReader depthBr;
    private boolean isShutdown;
    private HedgingConfig config;
    private AtomicInteger reverseOpen;
    private AtomicInteger reverseOpenAir;


    public OkCoinTradingMock(HedgingConfig hedgingConfig) throws Exception {
        config = hedgingConfig;
        // tickerFile = "D:/test/btc_record/okticker.txt";
        // depthFile = "D:/test/btc_record/okdepths.txt";
        // tickerFile = "D:/test/btc_record/real/okticker.real";
        // depthFile = "D:/test/btc_record/real/okdepths.real";

        // createBufferedReader();

        ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "OkCoinTradingMock");
            }

        });
        order = new HashMap<String, OkTradeQueryResponse>();
        id = new AtomicInteger(1000000);

        String totalStr = readTransactionStr();
        if (null != totalStr && !"".equals(totalStr)) {
            totalFunds = Float.parseFloat(totalStr);
        }

        reverseOpen = new AtomicInteger(0);
        reverseOpenAir = new AtomicInteger(0);
    }


    // private void createBufferedReader() throws Exception {
    // tickerBr = new BufferedReader(new FileReader(tickerFile));
    // depthBr = new BufferedReader(new FileReader(depthFile));
    // }

    public boolean isShutdown() {
        return isShutdown;
    }


    public void setShutdown(boolean isShutdown) {
        this.isShutdown = isShutdown;
    }


    // private void closeBufferedReader() throws Exception {
    // tickerBr.close();
    // depthBr.close();
    // }

    @Override
    public void shutdown() throws Exception {
        setShutdown(true);
        ses.shutdown();
        // closeBufferedReader();
        super.shutdown();
    }


    @Override
    public void start() throws Exception {
        ses.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    persistTransactionHolder();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1, 30, TimeUnit.SECONDS);
        // ses.scheduleAtFixedRate(new Runnable() {
        // @Override
        // public void run() {
        // try {
        // if (isShutdown())
        // return;
        //
        // String line = tickerBr.readLine();
        // if (null != line) {
        // okTicker = JSON.parseObject(line.trim(), OkTicker.class);
        // okTicker.setDate(System.currentTimeMillis() / 1000);
        // }
        // else {
        // // restart
        // closeBufferedReader();
        // createBufferedReader();
        // return;
        // }
        //
        // line = depthBr.readLine();
        // if (null != line) {
        // OkDepthsOriginal odo = JSON.parseObject(line.trim(),
        // OkDepthsOriginal.class);
        // okDepths = odo.convertToOkDepths();
        // }
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // }, 1, 1000, TimeUnit.MILLISECONDS);
    }


    private void persistTransactionHolder() throws IOException {
        String fileName = config.getRootPath() + "oktotal";
        FileUtils.string2File(totalFunds + "", fileName);
    }


    private String readTransactionStr() {
        String fileName = config.getRootPath() + "oktotal";
        return FileUtils.file2String(fileName);
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkTicker getTicker(int timeout) throws Exception {
        // return okTicker;
        return super.getTicker(timeout);
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkDepths getDepths(int timeout) throws Exception {
        // return okDepths;
        return super.getDepths(timeout);
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkTradeResponse trade(Object r, int timeout) throws Exception {
        OkTradeRequest request = (OkTradeRequest) r;
        OkTradeResponse resp = new OkTradeResponse();
        String orderId = id.getAndIncrement() + "";
        resp.setOrder_id(orderId);
        resp.setResult(true);

        OkTradeQueryResponse oqr = new OkTradeQueryResponse();
        oqr.setAmount(request.getAmount());
        oqr.setContract_name("contractName_" + orderId);
        oqr.setCreated_date(System.currentTimeMillis() + "");
        oqr.setDeal_amount(request.getAmount());
        oqr.setFee("0");
        oqr.setLever_rate("10");
        oqr.setOrder_id(orderId);
        oqr.setPrice(request.getPrice());
        oqr.setPrice_avg(request.getPrice());
        oqr.setStatus(2 + "");
        oqr.setSymbol("btc_usd");
        oqr.setType(request.getType());
        oqr.setUnit_amount(Float.parseFloat(request.getAmount()) * Float.parseFloat(request.getPrice()) + "");

        // 开多,平空
        if (request.getType().equals("1") || request.getType().equals("4")) {
            getAndDecrement(Float.parseFloat(request.getPrice()) * Float.parseFloat(request.getAmount()));
            // 减去手续费
            if (request.getType().equals("1")) {
                float fee =
                        parseFloat(request.getAmount()) * parseFloat(request.getPrice())
                                * config.getFeeRatio();
                getAndDecrement(fee);
            }
            if (request.getType().equals("4")) {
                reverseOpenAir.getAndIncrement();
            }
        }
        // 开空,平多
        else {
            getAndIncrement(Float.parseFloat(request.getPrice()) * Float.parseFloat(request.getAmount()));
            // 减去手续费
            if (request.getType().equals("2")) {
                float fee =
                        parseFloat(request.getAmount()) * parseFloat(request.getPrice())
                                * config.getFeeRatio();
                getAndDecrement(fee);
            }
            if (request.getType().equals("3")) {
                reverseOpen.getAndIncrement();
            }
        }
        if (totalFunds < 0) {
            log.error("ok baocang. totalFunds=" + totalFunds);
            throw new Exception("ok baocang. totalFunds=" + totalFunds);
        }

        order.put(orderId, oqr);

        return resp;
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkTradeQueryResponse getTradeOrder(Object r, int timeout) throws Exception {
        OkTradeQueryRequest request = (OkTradeQueryRequest) r;
        return order.get(request.getOrder_id());
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkUserFutureResponse userFutureInfo(Object r, int timeout) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    // @Override
    // public Float exchangeRate(int timeout) throws Exception {
    // return 1f;
    // }

    public float getTotalFunds() {
        return totalFunds;
    }


    public void setTotalFunds(float totalFunds) {
        this.totalFunds = totalFunds;
    }


    public synchronized float getAndIncrement(float value) {
        float tmp = totalFunds;
        totalFunds += value;
        return tmp;
    }


    public synchronized float getAndDecrement(float value) {
        float tmp = totalFunds;
        totalFunds -= value;
        return tmp;
    }


    public int getReverseOpen() {
        return reverseOpen.get();
    }


    public int getReverseOpenAir() {
        return reverseOpenAir.get();
    }

}
