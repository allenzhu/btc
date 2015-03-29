package org.allen.btc.future.okcoin.domain;

import org.allen.btc.Credentials;


/**
 * @auther lansheng.zj
 */
public class OkUserFutureRequest extends Credentials {

    @Override
    public String toString() {
        return "OkUserFutureRequest [getAccessKey()=" + getAccessKey() + ", getSecretKey()=" + getSecretKey()
                + ", getSign()=" + getSign() + "]";
    }

}
