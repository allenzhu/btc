package org.allen.btc.future.okcoin.domain;

import java.util.List;


/**
 * @auther lansheng.zj
 */
public class OkTradeQueryResponseOriginal {

    private boolean result;
    private List<OkTradeQueryResponse> orders;


    public boolean isResult() {
        return result;
    }


    public void setResult(boolean result) {
        this.result = result;
    }


    public List<OkTradeQueryResponse> getOrders() {
        return orders;
    }


    public void setOrders(List<OkTradeQueryResponse> orders) {
        this.orders = orders;
    }
}
