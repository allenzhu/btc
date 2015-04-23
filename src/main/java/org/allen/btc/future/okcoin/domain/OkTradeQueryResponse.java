package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class OkTradeQueryResponse {

    private String amount; // 委托数量
    private String contract_name; // 合约名称
    private String created_date; // 委托时间 13位
    private String deal_amount; // 成交数量
    private String fee; // 手续费
    private String order_id; // 订单ID
    private String price; // 订单价格
    private String price_avg; // 平均价格
    private String status; // 订单状态(0等待成交 1部分成交 2全部成交 -1撤单)
    private String symbol; // btc_usd:比特币,ltc_usd:莱特币
    private String type; // 订单类型 1：开多 2：开空 3：平多 4： 平空
    private String unit_amount; // 合约面值
    private String lever_rate; // 杠杆倍数 value:10\20 默认10


    public String getAmount() {
        return amount;
    }


    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getContract_name() {
        return contract_name;
    }


    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }


    public String getCreated_date() {
        return created_date;
    }


    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }


    public String getDeal_amount() {
        return deal_amount;
    }


    public void setDeal_amount(String deal_amount) {
        this.deal_amount = deal_amount;
    }


    public String getFee() {
        return fee;
    }


    public void setFee(String fee) {
        this.fee = fee;
    }


    public String getOrder_id() {
        return order_id;
    }


    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }


    public String getPrice_avg() {
        return price_avg;
    }


    public void setPrice_avg(String price_avg) {
        this.price_avg = price_avg;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


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


    public String getUnit_amount() {
        return unit_amount;
    }


    public void setUnit_amount(String unit_amount) {
        this.unit_amount = unit_amount;
    }


    public String getLever_rate() {
        return lever_rate;
    }


    public void setLever_rate(String lever_rate) {
        this.lever_rate = lever_rate;
    }


    @Override
    public String toString() {
        return "OkTradeQueryResponse [amount=" + amount + ", contract_name=" + contract_name
                + ", created_date=" + created_date + ", deal_amount=" + deal_amount + ", fee=" + fee
                + ", order_id=" + order_id + ", price=" + price + ", price_avg=" + price_avg + ", status="
                + status + ", symbol=" + symbol + ", type=" + type + ", unit_amount=" + unit_amount
                + ", lever_rate=" + lever_rate + "]";
    }
}
