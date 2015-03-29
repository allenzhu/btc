package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class Ticker {

    private String buy; // 买一价
    private long contract_id; // 合约ID
    private String high; // 最高价
    private String last; // 最新成交价
    private String low; // 最低价
    private String sell; // 卖一价
    private String unit_amount; // 合约面值
    private String vol;// 成交量(最近的24小时)


    public String getBuy() {
        return buy;
    }


    public void setBuy(String buy) {
        this.buy = buy;
    }


    public long getContract_id() {
        return contract_id;
    }


    public void setContract_id(long contract_id) {
        this.contract_id = contract_id;
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


    public String getUnit_amount() {
        return unit_amount;
    }


    public void setUnit_amount(String unit_amount) {
        this.unit_amount = unit_amount;
    }


    public String getVol() {
        return vol;
    }


    public void setVol(String vol) {
        this.vol = vol;
    }


    @Override
    public String toString() {
        return "Ticker [buy=" + buy + ", contract_id=" + contract_id + ", high=" + high + ", last=" + last
                + ", low=" + low + ", sell=" + sell + ", unit_amount=" + unit_amount + ", vol=" + vol + "]";
    }

}
