package org.allen.btc.future.bitvc.domain;

import org.allen.btc.Credentials;
import org.allen.btc.utils.NotNull;


/**
 * @auther lansheng.zj
 */
public class VcOrderQueryRequest extends Credentials {

    @NotNull
    private String coinType; // 币种 1比特币
    @NotNull
    private String contractType; // 合约类型 (week 周 next_week 次周 quarter 季合约),
                                 // next_week只出现在比特币中
    @NotNull
    private String created; // 提交时间 10位时间戳
    @NotNull
    private String id; // 订单ID


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


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "VcOrderQueryRequest [coinType=" + coinType + ", contractType=" + contractType + ", created="
                + created + ", id=" + id + ", getAccessKey()=" + getAccessKey() + ", getSecretKey()="
                + getSecretKey() + ", getSign()=" + getSign() + "]";
    }
}
