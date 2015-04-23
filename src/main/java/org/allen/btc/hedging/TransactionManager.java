package org.allen.btc.hedging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.allen.btc.utils.DiffPriceType.BIG_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.BIG_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.HUGE_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.NON_DIF;
import static org.allen.btc.utils.DiffPriceType.NORMAL_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.NORMAL_DIF_POS;
import static org.allen.btc.utils.DiffPriceType.SMALL_DIF_NEGA;
import static org.allen.btc.utils.DiffPriceType.SMALL_DIF_POS;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.HedgingConfig;
import org.allen.btc.utils.DiffPriceType;
import org.allen.btc.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class TransactionManager {
    private static Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private ScheduledExecutorService taskService;
    private TransactionHolder transactionHolder;
    private HedgingConfig hedgingConfig;


    public TransactionManager(HedgingConfig config) {
        hedgingConfig = config;
        // init transactionHolder
        initTransactionHolder();

        taskService = Executors.newScheduledThreadPool(1, new ThreadFactory() {

            private AtomicInteger index = new AtomicInteger();


            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "taskService-" + index.getAndIncrement());
            }
        });
    }


    /**
     * 期望交易量， 最小开仓量和可交易量的最小值
     * 
     * @return
     */
    public float computAmount(DiffPriceType dType) {
        float totalAmount = 0;
        switch (dType) {
        case SMALL_DIF_NEGA:
        case SMALL_DIF_POS:
            totalAmount = hedgingConfig.getTotalAmount() * hedgingConfig.getSmallDiffPriceRatio();
            break;
        case NORMAL_DIF_NEGA:
        case NORMAL_DIF_POS:
            totalAmount = hedgingConfig.getTotalAmount() * hedgingConfig.getNormalDiffPriceRatio();
            break;
        case BIG_DIF_NEGA:
        case BIG_DIF_POS:
            totalAmount = hedgingConfig.getTotalAmount() * hedgingConfig.getBigDiffPriceRatio();
            break;
        case HUGE_DIF_NEGA:
        case HUGE_DIF_POS:
            totalAmount = hedgingConfig.getTotalAmount() * hedgingConfig.getHugeDiffPriceRatio();
            break;
        default:
            throw new IllegalArgumentException("computAmount illegal argument dType=" + dType);
        }

        float leftAmount = transactionHolder.leftAmout(totalAmount, dType);
        return (leftAmount <= 0.0) ? 0 : Math.min(hedgingConfig.getMinOpenAmount(), leftAmount);
    }


    public DiffPriceType computeDiffPriceType(float m, float n) {
        if (m >= hedgingConfig.getReturnPrice() + hedgingConfig.getHugeDiffPrice()) {
            return HUGE_DIF_POS;
        }
        else if (m >= hedgingConfig.getReturnPrice() + hedgingConfig.getBigDiffPrice()) {
            return BIG_DIF_POS;
        }
        else if (m >= hedgingConfig.getReturnPrice() + hedgingConfig.getNormalDiffPrice()) {
            return NORMAL_DIF_POS;
        }
        else if (m >= hedgingConfig.getReturnPrice() + hedgingConfig.getSmallDiffPrice()) {
            return SMALL_DIF_POS;
        }
        else if (n <= hedgingConfig.getReturnPrice() - hedgingConfig.getHugeDiffPrice()) {
            return HUGE_DIF_NEGA;
        }
        else if (n <= hedgingConfig.getReturnPrice() - hedgingConfig.getBigDiffPrice()) {
            return BIG_DIF_NEGA;
        }
        else if (n <= hedgingConfig.getReturnPrice() - hedgingConfig.getNormalDiffPrice()) {
            return NORMAL_DIF_NEGA;
        }
        else if (n <= hedgingConfig.getReturnPrice() - hedgingConfig.getSmallDiffPrice()) {
            return SMALL_DIF_NEGA;
        }
        else {
            return NON_DIF;
        }
    }


    public float getWaveByDiffPriceType(DiffPriceType dType) {
        switch (dType) {
        case HUGE_DIF_POS:
        case HUGE_DIF_NEGA:
            return hedgingConfig.getHugeDiffPrice();
        case BIG_DIF_POS:
        case BIG_DIF_NEGA:
            return hedgingConfig.getBigDiffPrice();
        case NORMAL_DIF_POS:
        case NORMAL_DIF_NEGA:
            return hedgingConfig.getNormalDiffPrice();
        case SMALL_DIF_POS:
        case SMALL_DIF_NEGA:
            return hedgingConfig.getSmallDiffPrice();
        default:
            throw new UnsupportedOperationException("unknown DiffPriceType, dType=" + dType);
        }
    }


    public void initTransactionHolder() {
        String json = readTransactionStr();

        if (null != json) {
            transactionHolder = JSON.parseObject(json, TransactionHolder.class);
        }
        else {
            transactionHolder = new TransactionHolder();
        }
    }


    public void persistTransactionHolder() throws IOException {
        String json = JSON.toJSONString(transactionHolder, true);
        String fileName = hedgingConfig.getRecordPath();
        FileUtils.string2File(json, fileName);
    }


    private String readTransactionStr() {
        String fileName = hedgingConfig.getRecordPath();
        log.warn("record file path " + fileName);
        return FileUtils.file2String(fileName);
    }


    public void start() {
        taskService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    persistTransactionHolder();
                }
                catch (IOException e) {
                    log.error("taskService persistTransactionHolder error.", e);
                }
            }
        }, 10000, hedgingConfig.getInterval(), MILLISECONDS);
    }


    public void close() {
        try {
            persistTransactionHolder();
        }
        catch (IOException e) {
            log.error("close TransactionManager error.", e);
        }
    }


    public void addRecord(Record record, DiffPriceType dType) {
        transactionHolder.addRecord(record, dType);
    }
}
