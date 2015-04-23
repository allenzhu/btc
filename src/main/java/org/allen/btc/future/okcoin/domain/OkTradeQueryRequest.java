package org.allen.btc.future.okcoin.domain;

import org.allen.btc.Credentials;
import org.allen.btc.utils.NotNull;


/**
 * @auther lansheng.zj
 */
public class OkTradeQueryRequest extends Credentials {

    @NotNull
    private String symbol; // btc_usd:比特币 ltc_usd :莱特币

    @NotNull
    private String contract_type; // 合约类型: this_week:当周 next_week:下周 quarter:季度
    @NotNull
    private String status; // 查询状态 1:未完成的订单 2:已经完成的订单
    @NotNull
    private String order_id; // 订单ID -1:查询未完成单，否则查询相应订单号的订单
    @NotNull
    private String current_page; // 当前页数
    @NotNull
    private String page_length; // 每页获取条数，最多不超过50


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


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getOrder_id() {
        return order_id;
    }


    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public String getCurrent_page() {
        return current_page;
    }


    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }


    public String getPage_length() {
        return page_length;
    }


    public void setPage_length(String page_length) {
        this.page_length = page_length;
    }


    @Override
    public String toString() {
        return "OkTradeQueryRequest [symbol=" + symbol + ", contract_type=" + contract_type + ", status="
                + status + ", order_id=" + order_id + ", current_page=" + current_page + ", page_length="
                + page_length + ", getAccessKey()=" + getAccessKey() + ", getSecretKey()=" + getSecretKey()
                + ", getSign()=" + getSign() + "]";
    }
}
