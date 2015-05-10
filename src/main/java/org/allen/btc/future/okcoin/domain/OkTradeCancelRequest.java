package org.allen.btc.future.okcoin.domain;

import org.allen.btc.Credentials;
import org.allen.btc.utils.NotNull;


/**
 * @auther lansheng.zj
 */
public class OkTradeCancelRequest extends Credentials {

    @NotNull
    private String symbol; // btc_usd:比特币 ltc_usd :莱特币
    private String order_id; // 订单ID(多个订单ID中间以","分隔,一次最多允许撤消3个订单)
    @NotNull
    private String contract_type; // 合约类型: this_week:当周 next_week:下周 quarter:季度


    public String getSymbol() {
        return symbol;
    }


    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public String getOrder_id() {
        return order_id;
    }


    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public String getContract_type() {
        return contract_type;
    }


    public void setContract_type(String contract_type) {
        this.contract_type = contract_type;
    }


    @Override
    public String toString() {
        return "OkTradeCancelRequest [symbol=" + symbol + ", order_id=" + order_id + ", contract_type="
                + contract_type + "]";
    }
}
