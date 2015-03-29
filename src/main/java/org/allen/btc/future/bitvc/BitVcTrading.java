package org.allen.btc.future.bitvc;

import static org.allen.btc.Constants.BITVC_API_DOMAIN;
import static org.allen.btc.Constants.BITVC_MARKET_DOMAIN;
import static org.allen.btc.Constants.HTTPS;
import static org.allen.btc.Constants.PATH_BITVC_ORDER_SAVE;
import static org.allen.btc.Constants.PATH_BITVC_TICKET_WEEK;
import static org.allen.btc.utils.EncryptUtils.checkRequestNotNull;
import static org.allen.btc.utils.EncryptUtils.createRequestParam;
import static org.allen.btc.utils.EncryptUtils.md5LowerCase;
import static org.allen.btc.utils.EncryptUtils.signStr;
import static org.allen.btc.utils.HttpUtils.requestGet;
import static org.allen.btc.utils.HttpUtils.requestPost;

import java.net.URI;
import java.util.TreeMap;

import org.allen.btc.Trading;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 * @auther lansheng.zj
 */
public class BitVcTrading implements Trading {

    private CloseableHttpClient httpclient;


    public BitVcTrading() {
        httpclient = HttpClients.createDefault();
    }


    @Override
    public void start() throws Exception {
    }


    @Override
    public void shutdown() throws Exception {
        httpclient.close();
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcTicker getTicker(int timeout) throws Exception {
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_MARKET_DOMAIN)
                    .setPath(PATH_BITVC_TICKET_WEEK).build();

        VcTicker result = requestGet(httpclient, uri, VcTicker.class, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcOrderResponse trade(Object r, int timeout) throws Exception {
        VcOrderRequest request = (VcOrderRequest) r;
        checkRequestNotNull(request);
        TreeMap<String, String> map = createRequestParam(request);
        map.put("accessKey", request.getAccessKey());

        TreeMap<String, String> requestParam = (TreeMap<String, String>) map.clone();

        // create sign
        map.put("secretKey", request.getSecretKey());
        String signStr = signStr(map);
        String sign = md5LowerCase(signStr);

        // create request param
        requestParam.put("sign", sign);

        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_API_DOMAIN).setPath(PATH_BITVC_ORDER_SAVE)
                    .build();
        VcOrderResponse result = requestPost(httpclient, uri, requestParam, VcOrderResponse.class, timeout);
        return result;
    }

}
