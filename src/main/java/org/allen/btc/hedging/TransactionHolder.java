package org.allen.btc.hedging;

import java.util.ArrayList;
import java.util.Iterator;
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


    public int smallPositiveSize() {
        return smallPositive.size();
    }


    public int normalPositiveSize() {
        return normalPositive.size();
    }


    public int bigPositiveSize() {
        return bigPositive.size();
    }


    public int hugePositiveSize() {
        return hugePositive.size();
    }


    public int smallNegativeSize() {
        return smallNegative.size();
    }


    public int normalNegativeSize() {
        return normalNegative.size();
    }


    public int bigNegativeSize() {
        return bigNegative.size();
    }


    public int hugeNegativeSize() {
        return hugeNegative.size();
    }


    public void addRecord(Record record, DiffPriceType dType) {
        switch (dType) {
        case SMALL_DIF_POS:
            addRecord(smallPositive, record);
            break;
        case NORMAL_DIF_POS:
            addRecord(normalPositive, record);
            break;
        case BIG_DIF_POS:
            addRecord(bigPositive, record);
            break;
        case HUGE_DIF_POS:
            addRecord(hugePositive, record);
            break;

        case SMALL_DIF_NEGA:
            addRecord(smallNegative, record);
            break;
        case NORMAL_DIF_NEGA:
            addRecord(normalNegative, record);
            break;
        case BIG_DIF_NEGA:
            addRecord(bigNegative, record);
            break;
        case HUGE_DIF_NEGA:
            addRecord(hugeNegative, record);
            break;
        default:
            throw new IllegalArgumentException("addRecord illegal argument dType=" + dType);
        }
    }


    public void removeRecord(float expectedAmount, DiffPriceType dType) {
        switch (dType) {
        case SMALL_DIF_POS:
        case NORMAL_DIF_POS:
        case BIG_DIF_POS:
        case HUGE_DIF_POS:
            if (isSatifiedToRemove(hugePositive, expectedAmount)) {
                remove(hugePositive, expectedAmount);
            }
            else if (isSatifiedToRemove(bigPositive, expectedAmount)) {
                remove(bigPositive, expectedAmount);
            }
            else if (isSatifiedToRemove(normalPositive, expectedAmount)) {
                remove(normalPositive, expectedAmount);
            }
            else if (isSatifiedToRemove(smallPositive, expectedAmount)) {
                remove(smallPositive, expectedAmount);
            }
            else {
                throw new IllegalArgumentException(
                    "[never expected] remove positive record error. expectedAmount=" + expectedAmount
                            + ", dType=" + dType);
            }
            break;

        case HUGE_DIF_NEGA:
        case BIG_DIF_NEGA:
        case NORMAL_DIF_NEGA:
        case SMALL_DIF_NEGA:
            if (isSatifiedToRemove(hugeNegative, expectedAmount)) {
                remove(hugeNegative, expectedAmount);
            }
            else if (isSatifiedToRemove(bigNegative, expectedAmount)) {
                remove(bigNegative, expectedAmount);
            }
            else if (isSatifiedToRemove(normalNegative, expectedAmount)) {
                remove(normalNegative, expectedAmount);
            }
            else if (isSatifiedToRemove(smallNegative, expectedAmount)) {
                remove(smallNegative, expectedAmount);
            }
            else {
                throw new IllegalArgumentException(
                    "[never expected] remove negative record error. expectedAmount=" + expectedAmount
                            + ", dType=" + dType);
            }
            break;
        default:
            throw new IllegalArgumentException("removeRecord illegal argument dType=" + dType);
        }
    }


    private boolean isSatifiedToRemove(List<Record> rs, float expectedAmount) {
        float total = 0;
        for (Record record : rs) {
            total += record.getAmount();
        }

        return total >= expectedAmount;
    }


    private void remove(List<Record> rs, float expectedAmount) {
        synchronized (rs) {
            Iterator<Record> iterator = rs.iterator();
            while (iterator.hasNext()) {
                Record record = iterator.next();
                if (record.getAmount() == expectedAmount) {
                    iterator.remove();
                }
            }
        }
    }


    private void addRecord(List<Record> rs, Record record) {
        synchronized (rs) {
            rs.add(record);
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


    /**
     * 有限返回大查价订单交易量
     * 
     * @param dType
     * @return
     */
    public float exsitAmount(DiffPriceType dType) {
        float exsitAmount = 0;
        switch (dType) {
        case HUGE_DIF_POS:
        case BIG_DIF_POS:
        case NORMAL_DIF_POS:
        case SMALL_DIF_POS:
            if (!hugePositive.isEmpty()) {
                exsitAmount = computeAmount(hugePositive);
            }
            else if (!bigPositive.isEmpty()) {
                exsitAmount = computeAmount(bigPositive);
            }
            else if (!normalPositive.isEmpty()) {
                exsitAmount = computeAmount(normalPositive);
            }
            else if (!smallPositive.isEmpty()) {
                exsitAmount = computeAmount(smallPositive);
            }
            break;
        case HUGE_DIF_NEGA:
        case BIG_DIF_NEGA:
        case NORMAL_DIF_NEGA:
        case SMALL_DIF_NEGA:
            if (!hugeNegative.isEmpty()) {
                exsitAmount = computeAmount(hugeNegative);
            }
            else if (!bigNegative.isEmpty()) {
                exsitAmount = computeAmount(bigNegative);
            }
            else if (!normalNegative.isEmpty()) {
                exsitAmount = computeAmount(normalNegative);
            }
            else if (!smallNegative.isEmpty()) {
                exsitAmount = computeAmount(smallNegative);
            }
            break;
        default:
            throw new IllegalArgumentException("computAmount illegal argument dType=" + dType);
        }

        return exsitAmount;
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
