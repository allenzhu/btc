package org.allen.btc.hedging;

/**
 * @auther lansheng.zj
 */
public class CompareResult {

    private boolean isSuccess;
    private float diffPrice;
    private String msg;


    public boolean isSuccess() {
        return isSuccess;
    }


    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    public float getDiffPrice() {
        return diffPrice;
    }


    public void setDiffPrice(float diffPrice) {
        this.diffPrice = diffPrice;
    }


    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

}
