package org.allen.btc.hedging;

import org.allen.btc.utils.DiffPriceType;


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
    private DiffPriceType type;

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


    public DiffPriceType getType() {
        return type;
    }


    public void setType(DiffPriceType type) {
        this.type = type;
    }

}
