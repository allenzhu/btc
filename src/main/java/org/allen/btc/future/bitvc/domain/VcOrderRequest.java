package org.allen.btc.future.bitvc.domain;

import org.allen.btc.Credentials;
import org.allen.btc.utils.NotNull;


/**
 * @auther lansheng.zj
 */
public class VcOrderRequest extends Credentials {
    @NotNull
    private String coinType; // 币种 1比特币
    @NotNull
    private String contractType; // 合约类型 (week 周 next_week 次周 quarter 季合约),
                                 // next_week只出现在比特币中
    @NotNull
    private String created; // 提交时间 10位时间戳
    @NotNull
    private String orderType; // 订单类型
    @NotNull
    private String tradeType; // 交易类型
    @NotNull
    private String price; // 价格
    @NotNull
    private String money; // 金额数量
    private String leverage; // 杠杆倍数（BTC周: 5、10、20 BTC季: 5、10 ）
    private String tradePassword; // 资金密码（用户开启交易输入资金密码，需要传入资金密码进行验证）
    private String storeId; // 下单仓位（默认为 0）


    public String getCoinType() {
        return coinType;
    }


    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }


    public String getContractType() {
        return contractType;
    }


    public void setContractType(String contractType) {
        this.contractType = contractType;
    }


    public String getCreated() {
        return created;
    }


    public void setCreated(String created) {
        this.created = created;
    }


    public String getOrderType() {
        return orderType;
    }


    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }


    public String getTradeType() {
        return tradeType;
    }


    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }


    public String getMoney() {
        return money;
    }


    public void setMoney(String money) {
        this.money = money;
    }


    public String getLeverage() {
        return leverage;
    }


    public void setLeverage(String leverage) {
        this.leverage = leverage;
    }


    public String getTradePassword() {
        return tradePassword;
    }


    public void setTradePassword(String tradePassword) {
        this.tradePassword = tradePassword;
    }


    public String getStoreId() {
        return storeId;
    }


    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }


    @Override
    public String toString() {
        return "VcOrderRequest [coinType=" + coinType + ", contractType=" + contractType + ", created="
                + created + ", orderType=" + orderType + ", tradeType=" + tradeType + ", price=" + price
                + ", money=" + money + ", leverage=" + leverage + ", tradePassword=" + tradePassword
                + ", storeId=" + storeId + ", getAccessKey()=" + getAccessKey() + ", getSecretKey()="
                + getSecretKey() + ", getSign()=" + getSign() + "]";
    }

}
