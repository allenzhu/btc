package org.allen.btc.future.okcoin.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @auther lansheng.zj
 */
public class OkDepths {

    private final long time; // s
    // 卖方深度
    private List<OkDeputer> asks;
    // 买方深度
    private List<OkDeputer> bids;

    public OkDepths() {
        time = 0;
    }

    public OkDepths(long t) {
        time = t;
        asks = new ArrayList<OkDeputer>();
        bids = new ArrayList<OkDeputer>();
    }


    public List<OkDeputer> getAsks() {
        return asks;
    }


    public void setAsks(List<OkDeputer> asks) {
        this.asks = asks;
    }


    public List<OkDeputer> getBids() {
        return bids;
    }


    public void setBids(List<OkDeputer> bids) {
        this.bids = bids;
    }


    public long getTime() {
        return time;
    }


    public void addAskDeputer(OkDeputer deputer) {
        asks.add(deputer);
    }


    public void addBidDeputer(OkDeputer deputer) {
        bids.add(deputer);
    }


    @Override
    public String toString() {
        return "OkDepths [asks=" + asks + ", bids=" + bids + "]";
    }
}
