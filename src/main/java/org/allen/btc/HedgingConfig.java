package org.allen.btc;

import java.io.File;


/**
 * @auther lansheng.zj
 */
public class HedgingConfig {

    public String rootPath = System.getProperty("recordPath", System.getProperty("user.home")
            + File.separator);
    public String recordFilename = "transactions";

    private float returnPrice; // 回归价

    private float totalAmount; // 总仓量

    private float smallDiffPrice; // 小差价
    private float normalDiffPrice; // 普通差价
    private float bigDiffPrice; // 大差价
    private float hugeDiffPrice; // 极大差价

    private float smallDiffPriceRatio; // 小差价操作仓位比例
    private float normalDiffPriceRatio; // 普通操作仓位比例
    private float bigDiffPriceRatio; // 大差价操作仓位比例
    private float hugeDiffPriceRatio; // 极大差价操作仓位比例

    private float feeRatio = 0.0003f;

    private float skaterPrice; // 滑价

    private float minOpenAmount; // 最小开仓量
    private float minReverseAmount; // 最小平仓量

    private int interval = 300;
    private int timeout = 300;

    private int bigDiffTime = 5; // 5s

    private String accessKey;
    private String secretKey;

    private boolean suspendOpen;
    private boolean suspendReverse;


    public float getFeeRatio() {
        return feeRatio;
    }


    public void setFeeRatio(float feeRatio) {
        this.feeRatio = feeRatio;
    }


    public boolean isSuspendOpen() {
        return suspendOpen;
    }


    public void setSuspendOpen(boolean suspendOpen) {
        this.suspendOpen = suspendOpen;
    }


    public boolean isSuspendReverse() {
        return suspendReverse;
    }


    public void setSuspendReverse(boolean suspendReverse) {
        this.suspendReverse = suspendReverse;
    }


    public float getReturnPrice() {
        return returnPrice;
    }


    public void setReturnPrice(float returnPrice) {
        this.returnPrice = returnPrice;
    }


    public float getSmallDiffPrice() {
        return smallDiffPrice;
    }


    public void setSmallDiffPrice(float smallDiffPrice) {
        this.smallDiffPrice = smallDiffPrice;
    }


    public float getNormalDiffPrice() {
        return normalDiffPrice;
    }


    public void setNormalDiffPrice(float normalDiffPrice) {
        this.normalDiffPrice = normalDiffPrice;
    }


    public float getBigDiffPrice() {
        return bigDiffPrice;
    }


    public void setBigDiffPrice(float bigDiffPrice) {
        this.bigDiffPrice = bigDiffPrice;
    }


    public float getHugeDiffPrice() {
        return hugeDiffPrice;
    }


    public void setHugeDiffPrice(float hugeDiffPrice) {
        this.hugeDiffPrice = hugeDiffPrice;
    }


    public float getSmallDiffPriceRatio() {
        return smallDiffPriceRatio;
    }


    public void setSmallDiffPriceRatio(float smallDiffPriceRatio) {
        this.smallDiffPriceRatio = smallDiffPriceRatio;
    }


    public float getNormalDiffPriceRatio() {
        return normalDiffPriceRatio;
    }


    public void setNormalDiffPriceRatio(float normalDiffPriceRatio) {
        this.normalDiffPriceRatio = normalDiffPriceRatio;
    }


    public float getBigDiffPriceRatio() {
        return bigDiffPriceRatio;
    }


    public void setBigDiffPriceRatio(float bigDiffPriceRatio) {
        this.bigDiffPriceRatio = bigDiffPriceRatio;
    }


    public float getHugeDiffPriceRatio() {
        return hugeDiffPriceRatio;
    }


    public void setHugeDiffPriceRatio(float hugeDiffPriceRatio) {
        this.hugeDiffPriceRatio = hugeDiffPriceRatio;
    }


    public int getInterval() {
        return interval;
    }


    public void setInterval(int interval) {
        this.interval = interval;
    }


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


    public String getRecordFilename() {
        return recordFilename;
    }


    public void setRecordFilename(String recordFilename) {
        this.recordFilename = recordFilename;
    }


    public int getBigDiffTime() {
        return bigDiffTime;
    }


    public void setBigDiffTime(int bigDiffTime) {
        this.bigDiffTime = bigDiffTime;
    }


    public float getSkaterPrice() {
        return skaterPrice;
    }


    public void setSkaterPrice(float skaterPrice) {
        this.skaterPrice = skaterPrice;
    }


    public String getAccessKey() {
        return accessKey;
    }


    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }


    public String getSecretKey() {
        return secretKey;
    }


    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    public float getMinOpenAmount() {
        return minOpenAmount;
    }


    public void setMinOpenAmount(float minOpenAmount) {
        this.minOpenAmount = minOpenAmount;
    }


    public float getMinReverseAmount() {
        return minReverseAmount;
    }


    public void setMinReverseAmount(float minReverseAmount) {
        this.minReverseAmount = minReverseAmount;
    }


    public float getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String recordFilePath() {
        return rootPath + recordFilename;
    }
}
