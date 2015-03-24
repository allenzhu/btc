package org.allen.btc;

/**
 * @auther lansheng.zj
 */
public class HedgingConfig {

    private float returnPrice; // 回归价
    private float diffPrice; // 差价
    private float bigDiffPrice; // 大查价
    private float hugeDiffPrice; // 极大差价

    private int interval = 100;
    private int timeout = 100;


    public float getReturnPrice() {
        return returnPrice;
    }


    public void setReturnPrice(float returnPrice) {
        this.returnPrice = returnPrice;
    }


    public float getDiffPrice() {
        return diffPrice;
    }


    public void setDiffPrice(float diffPrice) {
        this.diffPrice = diffPrice;
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
