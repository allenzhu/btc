package org.allen.btc.future.okcoin.domain;

/**
 * @auther lansheng.zj
 */
public class OkDeputer {

    private float price;
    private float amount;


    public float getPrice() {
        return price;
    }


    public void setPrice(float price) {
        this.price = price;
    }


    public float getAmount() {
        return amount;
    }


    public void setAmount(float amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "OkDeputer [price=" + price + ", amount=" + amount + "]";
    }
}
