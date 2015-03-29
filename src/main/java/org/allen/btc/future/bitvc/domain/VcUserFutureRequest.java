package org.allen.btc.future.bitvc.domain;

import org.allen.btc.Credentials;


/**
 * @auther lansheng.zj
 */
public class VcUserFutureRequest extends Credentials {

    private String coinType; // 币种 1比特币
    private String created; // 提交时间 10位时间戳


    public String getCoinType() {
        return coinType;
    }


    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }


    public String getCreated() {
        return created;
    }


    public void setCreated(String created) {
        this.created = created;
    }


    @Override
    public String toString() {
        return "VcUserFutureRequest [coinType=" + coinType + ", created=" + created + ", getAccessKey()="
                + getAccessKey() + ", getSecretKey()=" + getSecretKey() + ", getSign()=" + getSign() + "]";
    }

}
