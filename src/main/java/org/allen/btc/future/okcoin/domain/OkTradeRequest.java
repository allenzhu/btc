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
    private String type; // 买卖类型： 限价单（buy/sell） 市价单（buy_market/sell_market）
    @NotNull
    private String price; // 下单价格 [限价买单(必填)： 大于等于0，小于等于1000000 | 市价买单(必填)： BTC
                          // :最少买入0.01个BTC 的金额(金额>0.01*卖一价) / LTC :最少买入0.1个LTC
                          // 的金额(金额>0.1*卖一价)]（市价卖单不传price）
    @NotNull
    private String amount; // 交易数量 [限价卖单（必填）：BTC 数量大于等于0.01 / LTC 数量大于等于0.1 |
                           // 市价卖单（必填）： BTC :最少卖出数量大于等于0.01 / LTC
                           // :最少卖出数量大于等于0.1]（市价买单不传amount）


    public String getSymbol() {
        return symbol;
    }


    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
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


    @Override
    public String toString() {
        return "OkTradeRequest [symbol=" + symbol + ", type=" + type + ", price=" + price + ", amount="
                + amount + ", getAccessKey()=" + getAccessKey() + ", getSecretKey()=" + getSecretKey()
                + ", getSign()=" + getSign() + "]";
    }

}
