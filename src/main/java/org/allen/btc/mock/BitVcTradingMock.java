package org.allen.btc.mock;

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
import org.allen.btc.future.bitvc.BitVcTrading;
import org.allen.btc.future.bitvc.domain.VcDepths;
import org.allen.btc.future.bitvc.domain.VcDepthsOriginal;
import org.allen.btc.future.bitvc.domain.VcOrderQueryRequest;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.bitvc.domain.VcUserFutureResponse;
import org.allen.btc.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class BitVcTradingMock extends BitVcTrading {
    private static Logger log = LoggerFactory.getLogger(BitVcTradingMock.class);

    private float totalFunds = 200000f;

    // private VcTicker vcTicker;
    // private VcDepths vcDepths;
    private ScheduledExecutorService ses;
    private Map<String, VcOrderResponse> order;
    private AtomicInteger id;
    // private String tickerFile;
    // private String depthFile;
    // private BufferedReader tickerBr;
    // private BufferedReader depthBr;
    private boolean isShutdown;
    private HedgingConfig config;
    private AtomicInteger reverseOpen;
    private AtomicInteger reverseOpenAir;


    public BitVcTradingMock(HedgingConfig hedgingConfig) throws Exception {
        config = hedgingConfig;
        ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "BitVcTradingMock");
            }

        });
        order = new HashMap<String, VcOrderResponse>();
        id = new AtomicInteger(1000000);
        // tickerFile = "D:/test/btc_record/vcticker.txt";
        // depthFile = "D:/test/btc_record/vcdepths.txt";
        // tickerFile = "D:/test/btc_record/real/vcticker.real";
        // depthFile = "D:/test/btc_record/real/vcdepths.real";

        // createBufferedReader();
        String totalStr = readTransactionStr();
        if (null != totalStr && !"".equals(totalStr)) {
            totalFunds = Float.parseFloat(totalStr);
        }

        reverseOpen = new AtomicInteger(0);
        reverseOpenAir = new AtomicInteger(0);
    }


    public boolean isShutdown() {
        return isShutdown;
    }


    public void setShutdown(boolean isShutdown) {
        this.isShutdown = isShutdown;
    }


    @Override
    public void shutdown() throws Exception {
        setShutdown(true);
        ses.shutdown();
        // closeBufferedReader();
        super.shutdown();
    }


    // private void closeBufferedReader() throws Exception {
    // tickerBr.close();
    // depthBr.close();
    // }

    // private void createBufferedReader() throws Exception {
    // tickerBr = new BufferedReader(new FileReader(tickerFile));
    // depthBr = new BufferedReader(new FileReader(depthFile));
    // }

    private void persistTransactionHolder() throws IOException {
        String fileName = config.getRootPath() + "vctotal";
        FileUtils.string2File(totalFunds + "", fileName);
    }


    private String readTransactionStr() {
        String fileName = config.getRootPath() + "vctotal";
        return FileUtils.file2String(fileName);
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
        // vcTicker = JSON.parseObject(line.trim(), VcTicker.class);
        // vcTicker.setTime(System.currentTimeMillis() / 1000);
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
        // VcDepthsOriginal vdo = JSON.parseObject(line.trim(),
        // VcDepthsOriginal.class);
        // vcDepths = vdo.convertToVcDepths();
        // }
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // }, 1, 1000, TimeUnit.MILLISECONDS);
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcTicker getTicker(int timeout) throws Exception {
        // return vcTicker;
        return super.getTicker(timeout);
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcDepths getDepths(int timeout) throws Exception {
        // return vcDepths;
        return super.getDepths(timeout);
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcOrderResponse trade(Object r, int timeout) throws Exception {
        VcOrderRequest request = (VcOrderRequest) r;
        String orderId = id.getAndIncrement() + "";
        String amount = Float.parseFloat(request.getMoney()) / Float.parseFloat(request.getPrice()) + "";

        VcOrderResponse response = new VcOrderResponse();
        response.setOrderTime(System.currentTimeMillis());
        response.setAmount(amount);
        response.setFee("0");
        response.setId(orderId);
        response.setLastTime(System.currentTimeMillis());
        response.setLever("10");
        response.setMargin("0");
        response.setMoney(request.getMoney());
        response.setOrderType(request.getOrderType());
        response.setPrice(request.getPrice());
        response.setProcessedAmount(amount);
        response.setProcessedMoney(request.getMoney());
        response.setProcessedPrice(request.getPrice());
        response.setStatus("2");
        response.setStoreId(orderId);
        response.setTradeType(request.getTradeType());

        // 开多,平空
        if ((request.getOrderType().equals("1") && request.getTradeType().equals("1"))
                || (request.getOrderType().equals("2") && request.getTradeType().equals("1"))) {
            getAndDecrement(Float.parseFloat(request.getMoney()));
            if (request.getOrderType().equals("1")) {
                float fee = Float.parseFloat(request.getMoney()) * config.getFeeRatio();
                getAndDecrement(fee);
            }
            if (request.getOrderType().equals("2")) {
                reverseOpenAir.getAndIncrement();
            }
        }
        // 开空,平多
        else {
            getAndIncrement(Float.parseFloat(request.getMoney()));
            if (request.getOrderType().equals("1")) {
                float fee = Float.parseFloat(request.getMoney()) * config.getFeeRatio();
                getAndDecrement(fee);
            }
            if (request.getOrderType().equals("2")) {
                reverseOpen.getAndIncrement();
            }
        }

        if (totalFunds < 0) {
            log.error("vc baocang. totalFunds=" + totalFunds);
            throw new Exception("vc baocang. totalFunds=" + totalFunds);
        }

        order.put(orderId, response);

        return response;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcOrderResponse getTradeOrder(Object r, int timeout) throws Exception {
        VcOrderQueryRequest request = (VcOrderQueryRequest) r;
        return order.get(request.getId());
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcUserFutureResponse userFutureInfo(Object r, int timeout) throws Exception {

        return null;
    }


    // @Override
    // public Float exchangeRate(int timeout) throws Exception {
    // return 1f;
    // }

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


    public float getTotalFunds() {
        return totalFunds;
    }


    public void setTotalFunds(float totalFunds) {
        this.totalFunds = totalFunds;
    }


    public int getReverseOpen() {
        return reverseOpen.get();
    }


    public int getReverseOpenAir() {
        return reverseOpenAir.get();
    }

}
