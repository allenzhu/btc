package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class OkTradeResponse {

    private boolean result; // 代表成功返回
    private String order_id; // 订单ID


    public boolean isResult() {
        return result;
    }


    public void setResult(boolean result) {
        this.result = result;
    }


    public String getOrder_id() {
        return order_id;
    }


    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    @Override
    public String toString() {
        return "OkTradeResponse [result=" + result + ", order_id=" + order_id + "]";
    }

}
