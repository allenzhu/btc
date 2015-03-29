package org.allen.btc.future.okcoin.domain;

import org.allen.btc.Credentials;
import org.allen.btc.utils.NotNull;


/**
 * @auther lansheng.zj
 */
public class OkTradeRequest extends Credentials {

    @NotNull
    private String symbol; // btc_cny: 比特币 ltc_cny: 莱特币
    @NotNull
    private String contract_type; // 合约类型: this_week:当周 next_week:下周 quarter:季度
    @NotNull
    private String price; // 价格
    @NotNull
    private String amount; // 委托数量
    @NotNull
    private String type; // 1:开多 2:开空 3:平多 4:平空
    @NotNull
    private String match_price; // 是否为对手价 0:不是 1:是 ,当取值为1时,price无效
    @NotNull
    private String lever_rate; // 杠杆倍数 value:10\20 默认10


    public String getSymbol() {
        return symbol;
    }


    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getContract_type() {
        return contract_type;
    }


    public void setContract_type(String contract_type) {
        this.contract_type = contract_type;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }


    public String getAmount() {
        return amount;
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getMatch_price() {
        return match_price;
    }


    public void setMatch_price(String match_price) {
        this.match_price = match_price;
    }


    public String getLever_rate() {
        return lever_rate;
    }


    public void setLever_rate(String lever_rate) {
        this.lever_rate = lever_rate;
    }


    @Override
    public String toString() {
        return "OkTradeRequest [symbol=" + symbol + ", contract_type=" + contract_type + ", price=" + price
                + ", amount=" + amount + ", type=" + type + ", match_price=" + match_price + ", lever_rate="
                + lever_rate + ", getAccessKey()=" + getAccessKey() + ", getSecretKey()=" + getSecretKey()
                + ", getSign()=" + getSign() + "]";
    }

}
