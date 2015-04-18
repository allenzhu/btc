package org.allen.btc;

import java.io.File;


/**
 * @auther lansheng.zj
 */
public class HedgingConfig {

    public String recordPath = System.getProperty("recordPath", System.getProperty("user.home")
            + File.separator + "transactions");

    private float returnPrice; // 回归价

    private float smallDiffPrice; // 小差价
    private float normalDiffPrice; // 普通差价
    private float bigDiffPrice; // 大差价
    private float hugeDiffPrice; // 极大差价

    private float smallDiffPriceRatio; // 小差价操作仓位比例
    private float normalDiffPriceRatio; // 普通操作仓位比例
    private float bigDiffPriceRatio; // 大差价操作仓位比例
    private float hugeDiffPriceRatio; // 极大差价操作仓位比例

    private float skaterPrice; // 滑价

    private int interval = 300;
    private int timeout = 300;

    private int bigDiffTime = 5; // 5s

    private String accessKey;
    private String secretKey;


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


    public String getRecordPath() {
        return recordPath;
    }


    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
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
}
