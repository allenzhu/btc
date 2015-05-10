package org.allen.btc.future.bitvc;

import static org.allen.btc.Constants.BITVC_API_DOMAIN;
import static org.allen.btc.Constants.BITVC_MARKET_DOMAIN;
import static org.allen.btc.Constants.HTTPS;
import static org.allen.btc.Constants.PATH_BITVC_DEPTH_WEEK;
import static org.allen.btc.Constants.PATH_BITVC_ORDER_CANCEL;
import static org.allen.btc.Constants.PATH_BITVC_ORDER_QUERY;
import static org.allen.btc.Constants.PATH_BITVC_ORDER_SAVE;
import static org.allen.btc.Constants.PATH_BITVC_TICKET_WEEK;
import static org.allen.btc.Constants.PATH_BITVC_USER_FUTURE;
import static org.allen.btc.utils.EncryptUtils.checkRequestNotNull;
import static org.allen.btc.utils.EncryptUtils.createRequestParam;
import static org.allen.btc.utils.EncryptUtils.md5LowerCase;
import static org.allen.btc.utils.EncryptUtils.signStr;
import static org.allen.btc.utils.HttpUtils.requestGet;
import static org.allen.btc.utils.HttpUtils.requestPost;

import java.net.URI;
import java.util.TreeMap;

import org.allen.btc.Credentials;
import org.allen.btc.Trading;
import org.allen.btc.future.bitvc.domain.VcCancelRequest;
import org.allen.btc.future.bitvc.domain.VcCancelResponse;
import org.allen.btc.future.bitvc.domain.VcDepths;
import org.allen.btc.future.bitvc.domain.VcDepthsOriginal;
import org.allen.btc.future.bitvc.domain.VcOrderQueryRequest;
import org.allen.btc.future.bitvc.domain.VcOrderRequest;
import org.allen.btc.future.bitvc.domain.VcOrderResponse;
import org.allen.btc.future.bitvc.domain.VcTicker;
import org.allen.btc.future.bitvc.domain.VcUserFutureRequest;
import org.allen.btc.future.bitvc.domain.VcUserFutureResponse;
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
    public VcDepths getDepths(int timeout) throws Exception {
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_MARKET_DOMAIN).setPath(PATH_BITVC_DEPTH_WEEK)
                    .build();

        VcDepthsOriginal result = requestGet(httpclient, uri, VcDepthsOriginal.class, timeout);
        VcDepths vcDepths = result.convertToVcDepths();
        return vcDepths;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcOrderResponse trade(Object r, int timeout) throws Exception {
        VcOrderRequest request = (VcOrderRequest) r;
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_API_DOMAIN).setPath(PATH_BITVC_ORDER_SAVE)
                    .build();
        VcOrderResponse result = doPost(request, new VcOrderResponse(), httpclient, uri, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcOrderResponse getTradeOrder(Object r, int timeout) throws Exception {
        VcOrderQueryRequest request = (VcOrderQueryRequest) r;
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_API_DOMAIN).setPath(PATH_BITVC_ORDER_QUERY)
                    .build();
        VcOrderResponse result = doPost(request, new VcOrderResponse(), httpclient, uri, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcUserFutureResponse userFutureInfo(Object r, int timeout) throws Exception {
        VcUserFutureRequest request = (VcUserFutureRequest) r;
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_API_DOMAIN).setPath(PATH_BITVC_USER_FUTURE)
                    .build();
        VcUserFutureResponse result = doPost(request, new VcUserFutureResponse(), httpclient, uri, timeout);
        return result;
    }


    @Override
    public Float exchangeRate(int timeout) throws Exception {
        return 1f;
    }


    @SuppressWarnings("unchecked")
    @Override
    public VcCancelResponse cancel(Object r, int timeout) throws Exception {
        VcCancelRequest request = (VcCancelRequest) r;
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(BITVC_API_DOMAIN).setPath(PATH_BITVC_ORDER_CANCEL)
                    .build();
        VcCancelResponse result = doPost(request, new VcCancelResponse(), httpclient, uri, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    private <P, R extends Credentials> P doPost(R request, P resp, CloseableHttpClient httpclient, URI uri,
            int timeout) throws Exception {

        checkRequestNotNull(request);
        TreeMap<String, String> map = createRequestParam(request);
        map.put("accessKey", request.getAccessKey());

        TreeMap<String, String> requestParam = (TreeMap<String, String>) map.clone();

        // create sign
        map.put("secretKey", request.getSecretKey());
        String signStr = signStr(map);
        String sign = md5LowerCase(signStr);
        request.setSign(sign);

        // create request param
        requestParam.put("sign", sign);

        P result = (P) requestPost(httpclient, uri, requestParam, resp.getClass(), timeout);
        return result;
    }
}
