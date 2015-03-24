package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class Ticker {

    private String buy; // 买一价
    private String high; // 最高价
    private String last; // 最新成交价
    private String low;// 最低价
    private String sell;// 卖一价
    private String vol;// 成交量(最近的24小时)


    public String getBuy() {
        return buy;
    }


    public void setBuy(String buy) {
        this.buy = buy;
    }


    public String getHigh() {
        return high;
    }


    public void setHigh(String high) {
        this.high = high;
    }


    public String getLast() {
        return last;
    }


    public void setLast(String last) {
        this.last = last;
    }


    public String getLow() {
        return low;
    }


    public void setLow(String low) {
        this.low = low;
    }


    public String getSell() {
        return sell;
    }


    public void setSell(String sell) {
        this.sell = sell;
    }


    public String getVol() {
        return vol;
    }


    public void setVol(String vol) {
        this.vol = vol;
    }


    @Override
    public String toString() {
        return "Ticker [buy=" + buy + ", high=" + high + ", last=" + last + ", low=" + low + ", sell=" + sell
                + ", vol=" + vol + "]";
    }
}
