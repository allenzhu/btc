package org.allen.btc.future.bitvc.domain;

/**
 * @auther lansheng.zj
 */
public class VcOrderResponse {

    private String fee; // 手续费
    private String id; // 订单ID
    private String storeId; // 仓位ID
    private String tradeType; // 交易类型（1、买多 2、卖空）
    private String price; // 价格
    private String orderType; // 订单类型（1、开仓 2、平仓）
    private String status; // 订单状态（0、未成交 1、部分成交 2、已成交 3、撤单）
    private String money; // 下单金额
    private String amount; // 订单比特币数量
    private String lever; // 订单杠杆倍数
    private long orderTime; // 下单时间
    private long lastTime; // 最后处理时间
    private String processedMoney; // 已处理金额
    private String processedAmount; // 已处理比特币数量
    private String margin; // 订单冻结保证金
    private String processedPrice; // 成交价格


    public String getFee() {
        return fee;
    }


    public void setFee(String fee) {
        this.fee = fee;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getStoreId() {
        return storeId;
    }


    public void setStoreId(String storeId) {
        this.storeId = storeId;
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


    public String getOrderType() {
        return orderType;
    }


    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getMoney() {
        return money;
    }


    public void setMoney(String money) {
        this.money = money;
    }


    public String getAmount() {
        return amount;
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getLever() {
        return lever;
    }


    public void setLever(String lever) {
        this.lever = lever;
    }


    public long getOrderTime() {
        return orderTime;
    }


    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }


    public long getLastTime() {
        return lastTime;
    }


    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }


    public String getProcessedMoney() {
        return processedMoney;
    }


    public void setProcessedMoney(String processedMoney) {
        this.processedMoney = processedMoney;
    }


    public String getProcessedAmount() {
        return processedAmount;
    }


    public void setProcessedAmount(String processedAmount) {
        this.processedAmount = processedAmount;
    }


    public String getMargin() {
        return margin;
    }


    public void setMargin(String margin) {
        this.margin = margin;
    }


    public String getProcessedPrice() {
        return processedPrice;
    }


    public void setProcessedPrice(String processedPrice) {
        this.processedPrice = processedPrice;
    }


    @Override
    public String toString() {
        return "VcOrderResponse [fee=" + fee + ", id=" + id + ", storeId=" + storeId + ", tradeType="
                + tradeType + ", price=" + price + ", orderType=" + orderType + ", status=" + status
                + ", money=" + money + ", amount=" + amount + ", lever=" + lever + ", orderTime=" + orderTime
                + ", lastTime=" + lastTime + ", processedMoney=" + processedMoney + ", processedAmount="
                + processedAmount + ", margin=" + margin + ", processedPrice=" + processedPrice + "]";
    }
}
