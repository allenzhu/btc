package org.allen.btc.hedging;

import java.util.ArrayList;
import java.util.List;


/**
 * @auther lansheng.zj
 */
public class TransactionHolder {

    // 正
    private List<Record> positive;
    // 负
    private List<Record> negative;


    public TransactionHolder() {
        positive = new ArrayList<Record>();
        negative = new ArrayList<Record>();
    }


    public List<Record> getPositive() {
        return positive;
    }


    public void setPositive(List<Record> positive) {
        this.positive = positive;
    }


    public List<Record> getNegative() {
        return negative;
    }


    public void setNegative(List<Record> negative) {
        this.negative = negative;
    }


    public void addPositive(Record record) {
        positive.add(record);
    }


    public void addNegative(Record record) {
        negative.add(record);
    }
}
