package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class OkTradeCancelResponse {

    private String order_id; // 订单ID(用于单笔订单)
    private boolean result; // 订单交易成功或失败(用于单笔订单)


    public String getOrder_id() {
        return order_id;
    }


    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public boolean isResult() {
        return result;
    }


    public void setResult(boolean result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "OkTradeCancelResponse [order_id=" + order_id + ", result=" + result + "]";
    }
}
