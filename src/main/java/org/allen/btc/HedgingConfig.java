package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public class HedgingConfig {

    private float returnPrice; // 回归价
    private float smallDiffPrice; // 小差价
    private float normalDiffPrice; // 普通差价
    private float bigDiffPrice; // 大差价
    private float hugeDiffPrice; // 极大差价

    private float smallDiffPriceRatio; // 小差价操作仓位比例
    private float normalDiffPriceRatio; // 普通操作仓位比例
    private float bigDiffPriceRatio; // 大差价操作仓位比例
    private float hugeDiffPriceRatio; // 极大差价操作仓位比例

    private int interval = 300;
    private int timeout = 300;


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

}
