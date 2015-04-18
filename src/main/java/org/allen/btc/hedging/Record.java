package org.allen.btc.hedging;

/**
 * @auther lansheng.zj
 */
public class Record {

    // 交易量
    private float amount;
    // 回归价
    private float returnPrice;
    // 波动
    private float wave;
    private float m;
    private float n;


    public float getAmount() {
        return amount;
    }


    public void setAmount(float amount) {
        this.amount = amount;
    }


    public float getReturnPrice() {
        return returnPrice;
    }


    public void setReturnPrice(float returnPrice) {
        this.returnPrice = returnPrice;
    }


    public float getWave() {
        return wave;
    }


    public void setWave(float wave) {
        this.wave = wave;
    }


    public float getM() {
        return m;
    }


    public void setM(float m) {
        this.m = m;
    }


    public float getN() {
        return n;
    }


    public void setN(float n) {
        this.n = n;
    }


    @Override
    public String toString() {
        return "Record [amount=" + amount + ", returnPrice=" + returnPrice + ", wave=" + wave + ", m=" + m
                + ", n=" + n + "]";
    }
}
