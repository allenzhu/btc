package trading;

import static org.allen.btc.utils.EncryptUtils.md5UpperCase;
import static org.allen.btc.utils.EncryptUtils.signStr;

import java.util.TreeMap;

import org.junit.Test;


/**
 * @auther lansheng.zj
 */
public class TestSign {

    @Test
    public void testSign() {
        TreeMap<String, String> map = new TreeMap();
        map.put("api_key", "abc");
        map.put("symbol", "btc_usd");
        map.put("contract_type", "this_week");
        map.put("price", "10.134");
        map.put("amount", "1");
        map.put("type", "1");
        map.put("match_price", "0");
        String signStr = signStr(map, "secret_key", "123");
        String sign = md5UpperCase(signStr);
        System.out.println(sign);
    }
}
