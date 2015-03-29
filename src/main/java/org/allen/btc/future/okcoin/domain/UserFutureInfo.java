package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class UserFutureInfo {

    private String account_rights;
    private String keep_deposit;
    private String profit_real;
    private String profit_unreal;
    private String risk_rate;


    public String getAccount_rights() {
        return account_rights;
    }


    public void setAccount_rights(String account_rights) {
        this.account_rights = account_rights;
    }


    public String getKeep_deposit() {
        return keep_deposit;
    }


    public void setKeep_deposit(String keep_deposit) {
        this.keep_deposit = keep_deposit;
    }


    public String getProfit_real() {
        return profit_real;
    }


    public void setProfit_real(String profit_real) {
        this.profit_real = profit_real;
    }


    public String getProfit_unreal() {
        return profit_unreal;
    }


    public void setProfit_unreal(String profit_unreal) {
        this.profit_unreal = profit_unreal;
    }


    public String getRisk_rate() {
        return risk_rate;
    }


    public void setRisk_rate(String risk_rate) {
        this.risk_rate = risk_rate;
    }


    @Override
    public String toString() {
        return "UserFutureInfo [account_rights=" + account_rights + ", keep_deposit=" + keep_deposit
                + ", profit_real=" + profit_real + ", profit_unreal=" + profit_unreal + ", risk_rate="
                + risk_rate + "]";
    }

}
