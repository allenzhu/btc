package org.allen.btc.future.bitvc.domain;

/**
 * @auther lansheng.zj
 */
public class VcDeputer {

    private float price;
    private float amount;
    private float total;


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


    public float getTotal() {
        return total;
    }


    public void setTotal(float total) {
        this.total = total;
    }


    @Override
    public String toString() {
        return "VcDeputer [price=" + price + ", amount=" + amount + ", total=" + total + "]";
    }
}
