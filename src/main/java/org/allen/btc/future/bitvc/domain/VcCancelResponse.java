package org.allen.btc.future.bitvc.domain;

/**
 * @auther lansheng.zj
 */
public class VcCancelResponse {

    private String result; // 成功状态
    private int code;


    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "VcCancelResponse [result=" + result + ", code=" + code + "]";
    }
}
