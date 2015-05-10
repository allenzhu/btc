package domain;

import java.util.ArrayList;
import java.util.List;

import org.allen.btc.future.bitvc.domain.VcDepths;
import org.allen.btc.future.bitvc.domain.VcDeputer;
import org.junit.Test;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class TestVcDepths {

    // @Test
    public void toJson() {
        VcDepths d = new VcDepths(0);
        List<VcDeputer> asks = new ArrayList<VcDeputer>();
        asks.add(create(1367.3f, 20.0f));
        List<VcDeputer> bids = new ArrayList<VcDeputer>();
        bids.add(create(1367.5f, 10.0f));
        d.setAsks(asks);
        d.setBids(bids);
        String json = JSON.toJSONString(d);
        System.out.println(json);
    }


    @Test
    public void toObject() {
        String str =
                "{\"asks\":[{\"amount\":20,\"price\":1367.3,\"total\":27346}],\"bids\":[{\"amount\":10,\"price\":1367.5,\"total\":13675}],\"time\":0}";
        VcDepths vd = JSON.parseObject(str, VcDepths.class);
        System.out.println(vd);
    }


    private VcDeputer create(float price, float amount) {
        VcDeputer vcDeputer = new VcDeputer();
        vcDeputer.setPrice(price);
        vcDeputer.setAmount(amount);
        vcDeputer.setTotal(price * amount);
        return vcDeputer;
    }
}
