package org.allen.btc.future.okcoin.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * @auther lansheng.zj
 */
public class OkDepthsOriginal {

    private List<List<String>> asks;
    private List<List<String>> bids;


    public OkDepthsOriginal() {
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


    public OkDepths convertToOkDepths() {
        OkDepths okDepths = new OkDepths(System.currentTimeMillis() / 1000);
        for (List<String> entry : asks) {
            OkDeputer deputer = new OkDeputer();
            deputer.setPrice(Float.parseFloat(entry.get(0)));
            deputer.setAmount(Float.parseFloat(entry.get(1)));
            okDepths.addAskDeputer(deputer);
        }

        for (List<String> entry : bids) {
            OkDeputer deputer = new OkDeputer();
            deputer.setPrice(Float.parseFloat(entry.get(0)));
            deputer.setAmount(Float.parseFloat(entry.get(1)));
            okDepths.addBidDeputer(deputer);
        }

        return okDepths;
    }
}
