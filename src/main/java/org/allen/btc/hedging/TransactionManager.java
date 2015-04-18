package org.allen.btc.hedging;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.allen.btc.HedgingConfig;
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


    public float computAmount() {

        return 0;
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


    public void addPositive(Record record) {
        transactionHolder.addPositive(record);
    }


    public void addNegative(Record record) {
        transactionHolder.addNegative(record);
    }
}
