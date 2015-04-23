package org.allen.btc.future.bitvc.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @auther lansheng.zj
 */
public class VcDepthsOriginal {

    private List<List<String>> asks;
    private List<List<String>> bids;


    public VcDepthsOriginal() {
        asks = new ArrayList<List<String>>();
        bids = new ArrayList<List<String>>();
    }


    public List<List<String>> getAsks() {
        return asks;
    }


    public void setAsks(List<List<String>> asks) {
        this.asks = asks;
    }


    public List<List<String>> getBids() {
        return bids;
    }


    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }


    public VcDepths convertToVcDepths() {
        VcDepths vcDepths = new VcDepths(System.currentTimeMillis() / 1000);
        for (List<String> entry : asks) {
            VcDeputer deputer = new VcDeputer();
            deputer.setPrice(Float.parseFloat(entry.get(0)));
            deputer.setAmount(Float.parseFloat(entry.get(1)));
            deputer.setTotal(Float.parseFloat(entry.get(2)));
            vcDepths.addAskDeputer(deputer);
        }

        for (List<String> entry : bids) {
            VcDeputer deputer = new VcDeputer();
            deputer.setPrice(Float.parseFloat(entry.get(0)));
            deputer.setAmount(Float.parseFloat(entry.get(1)));
            deputer.setTotal(Float.parseFloat(entry.get(2)));
            vcDepths.addBidDeputer(deputer);
        }
        return vcDepths;
    }


    @Override
    public String toString() {
        return "VcDepthsOriginal [asks=" + asks + ", bids=" + bids + "]";
    }
}
