package org.allen.btc.future.bitvc.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @auther lansheng.zj
 */
public class VcDepths {

    private final long time; // s
    // 卖方深度
    private List<VcDeputer> asks;
    // 买方深度
    private List<VcDeputer> bids;


    public VcDepths() {
        // for test
        time = 0;
    }


    public VcDepths(long t) {
        time = t;
        asks = new ArrayList<VcDeputer>();
        bids = new ArrayList<VcDeputer>();
    }


    public List<VcDeputer> getAsks() {
        return asks;
    }


    public void setAsks(List<VcDeputer> asks) {
        this.asks = asks;
    }


    public List<VcDeputer> getBids() {
        return bids;
    }


    public void setBids(List<VcDeputer> bids) {
        this.bids = bids;
    }


    public long getTime() {
        return time;
    }


    public void addAskDeputer(VcDeputer deputer) {
        asks.add(deputer);
    }


    public void addBidDeputer(VcDeputer deputer) {
        bids.add(deputer);
    }


    @Override
    public String toString() {
        return "VcDepths [asks=" + asks + ", bids=" + bids + "]";
    }
}
