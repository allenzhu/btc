package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class OkTicker {

    private long date; // 返回数据时服务器时间
    private Ticker ticker;


    public long getDate() {
        return date;
    }


    public void setDate(long date) {
        this.date = date;
    }


    public Ticker getTicker() {
        return ticker;
    }


    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }


    @Override
    public String toString() {
        return "OkTicker [date=" + date + ", ticker=" + ticker + "]";
    }

}
