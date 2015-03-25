package org.allen.btc.utils;

/**
 * @auther lansheng.zj
 */
public class DiffPriceResult {

    private DiffPriceType type;
    private float diffPrice;


    public DiffPriceType getType() {
        return type;
    }


    public void setType(DiffPriceType type) {
        this.type = type;
    }


    public float getDiffPrice() {
        return diffPrice;
    }


    public void setDiffPrice(float diffPrice) {
        this.diffPrice = diffPrice;
    }

}
