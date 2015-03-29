package org.allen.btc.future.okcoin.domain;

import java.util.Map;


/**
 * @auther lansheng.zj
 */
public class OkUserFutureResponse {

    private boolean result;
    private Map<String, UserFutureInfo> info;


    public boolean isResult() {
        return result;
    }


    public void setResult(boolean result) {
        this.result = result;
    }


    public Map<String, UserFutureInfo> getInfo() {
        return info;
    }


    public void setInfo(Map<String, UserFutureInfo> info) {
        this.info = info;
    }


    @Override
    public String toString() {
        return "OkUserFutureResponse [result=" + result + ", info=" + info + "]";
    }

}
