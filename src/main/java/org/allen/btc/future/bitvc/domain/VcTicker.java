package org.allen.btc.future.bitvc.domain;

/**
 * @auther lansheng.zj
 */
public class VcTicker {

    private String high; // 最高价
    private String low; // 最低价
    private String buy; // 买一价
    private String sell; // 卖一价
    private String last; // 当前价
    private String vol; // 成交量
    private String hold; // 持仓量
    private String open; // 开盘价
    private String limit_highest_price; // 最高限价
    private String limit_lowest_price; // 最低限价
    private String contract_type; // 合约类型
    private String contract_id; // 合约ID
    private long time; // 更新时间


    public String getHigh() {
        return high;
    }


    public void setHigh(String high) {
        this.high = high;
    }


    public String getLow() {
        return low;
    }


    public void setLow(String low) {
        this.low = low;
    }


    public String getBuy() {
        return buy;
    }


    public void setBuy(String buy) {
        this.buy = buy;
    }


    public String getSell() {
        return sell;
    }


    public void setSell(String sell) {
        this.sell = sell;
    }


    public String getLast() {
        return last;
    }


    public void setLast(String last) {
        this.last = last;
    }


    public String getVol() {
        return vol;
    }


    public void setVol(String vol) {
        this.vol = vol;
    }


    public String getHold() {
        return hold;
    }


    public void setHold(String hold) {
        this.hold = hold;
    }


    public String getOpen() {
        return open;
    }


    public void setOpen(String open) {
        this.open = open;
    }


    public String getLimit_highest_price() {
        return limit_highest_price;
    }


    public void setLimit_highest_price(String limit_highest_price) {
        this.limit_highest_price = limit_highest_price;
    }


    public String getLimit_lowest_price() {
        return limit_lowest_price;
    }


    public void setLimit_lowest_price(String limit_lowest_price) {
        this.limit_lowest_price = limit_lowest_price;
    }


    public String getContract_type() {
        return contract_type;
    }


    public void setContract_type(String contract_type) {
        this.contract_type = contract_type;
    }


    public String getContract_id() {
        return contract_id;
    }


    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }


    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "VcTicker [high=" + high + ", low=" + low + ", buy=" + buy + ", sell=" + sell + ", last="
                + last + ", vol=" + vol + ", hold=" + hold + ", open=" + open + ", limit_highest_price="
                + limit_highest_price + ", limit_lowest_price=" + limit_lowest_price + ", contract_type="
                + contract_type + ", contract_id=" + contract_id + ", time=" + time + "]";
    }
}
