package org.allen.btc.hedging;

import java.util.ArrayList;
import java.util.List;

import org.allen.btc.utils.DiffPriceType;


/**
 * @auther lansheng.zj
 */
public class TransactionHolder {

    // 正
    private List<Record> smallPositive;
    private List<Record> normalPositive;
    private List<Record> bigPositive;
    private List<Record> hugePositive;

    // 负
    private List<Record> smallNegative;
    private List<Record> normalNegative;
    private List<Record> bigNegative;
    private List<Record> hugeNegative;


    public TransactionHolder() {
        smallPositive = new ArrayList<Record>();
        normalPositive = new ArrayList<Record>();
        bigPositive = new ArrayList<Record>();
        hugePositive = new ArrayList<Record>();

        smallNegative = new ArrayList<Record>();
        normalNegative = new ArrayList<Record>();
        bigNegative = new ArrayList<Record>();
        hugeNegative = new ArrayList<Record>();
    }


    public void addRecord(Record record, DiffPriceType dType) {
        switch (dType) {
        case SMALL_DIF_POS:
            smallPositive.add(record);
            break;
        case NORMAL_DIF_POS:
            normalPositive.add(record);
            break;
        case BIG_DIF_POS:
            bigPositive.add(record);
            break;
        case HUGE_DIF_POS:
            hugePositive.add(record);
            break;

        case SMALL_DIF_NEGA:
            smallNegative.add(record);
            break;
        case NORMAL_DIF_NEGA:
            normalNegative.add(record);
            break;
        case BIG_DIF_NEGA:
            bigNegative.add(record);
            break;
        case HUGE_DIF_NEGA:
            hugeNegative.add(record);
            break;
        default:
            throw new IllegalArgumentException("addRecord illegal argument dType=" + dType);
        }
    }


    public float leftAmout(float totalAmount, DiffPriceType dType) {
        float exsitAmount = 0;

        switch (dType) {
        case SMALL_DIF_POS:
            exsitAmount = computeAmount(smallPositive);
            break;
        case NORMAL_DIF_POS:
            exsitAmount = computeAmount(normalPositive);
            break;
        case BIG_DIF_POS:
            exsitAmount = computeAmount(bigPositive);
            break;
        case HUGE_DIF_POS:
            exsitAmount = computeAmount(hugePositive);
            break;

        case SMALL_DIF_NEGA:
            exsitAmount = computeAmount(smallNegative);
            break;
        case NORMAL_DIF_NEGA:
            exsitAmount = computeAmount(normalNegative);
            break;
        case BIG_DIF_NEGA:
            exsitAmount = computeAmount(bigNegative);
            break;
        case HUGE_DIF_NEGA:
            exsitAmount = computeAmount(hugeNegative);
            break;
        default:
            throw new IllegalArgumentException("leftAmout illegal argument dType=" + dType);
        }

        return totalAmount - exsitAmount;
    }


    private float computeAmount(List<Record> rs) {
        float amount = 0;
        for (Record record : rs) {
            amount += record.getAmount();
        }

        return amount;
    }


    public List<Record> getSmallPositive() {
        return smallPositive;
    }


    public void setSmallPositive(List<Record> smallPositive) {
        this.smallPositive = smallPositive;
    }


    public List<Record> getNormalPositive() {
        return normalPositive;
    }


    public void setNormalPositive(List<Record> normalPositive) {
        this.normalPositive = normalPositive;
    }


    public List<Record> getBigPositive() {
        return bigPositive;
    }


    public void setBigPositive(List<Record> bigPositive) {
        this.bigPositive = bigPositive;
    }


    public List<Record> getHugePositive() {
        return hugePositive;
    }


    public void setHugePositive(List<Record> hugePositive) {
        this.hugePositive = hugePositive;
    }


    public List<Record> getSmallNegative() {
        return smallNegative;
    }


    public void setSmallNegative(List<Record> smallNegative) {
        this.smallNegative = smallNegative;
    }


    public List<Record> getNormalNegative() {
        return normalNegative;
    }


    public void setNormalNegative(List<Record> normalNegative) {
        this.normalNegative = normalNegative;
    }


    public List<Record> getBigNegative() {
        return bigNegative;
    }


    public void setBigNegative(List<Record> bigNegative) {
        this.bigNegative = bigNegative;
    }


    public List<Record> getHugeNegative() {
        return hugeNegative;
    }


    public void setHugeNegative(List<Record> hugeNegative) {
        this.hugeNegative = hugeNegative;
    }

}
