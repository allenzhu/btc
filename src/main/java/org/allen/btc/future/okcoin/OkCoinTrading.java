package org.allen.btc.future.okcoin;

import static org.allen.btc.Constants.HTTPS;
import static org.allen.btc.Constants.OKCOIN_DOMAIN;
import static org.allen.btc.Constants.PARAM_OKCOIN_CONTRACT;
import static org.allen.btc.Constants.PARAM_OKCOIN_CONTRACT_F_WEEK;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL_F_VALUE;
import static org.allen.btc.Constants.PATH_OKCOIN_TICKET_WEEK;
import static org.allen.btc.Constants.PATH_OKCOIN_TRADE;
import static org.allen.btc.Constants.PATH_OKCOIN_USER_FUTURE;
import static org.allen.btc.utils.EncryptUtils.checkRequestNotNull;
import static org.allen.btc.utils.EncryptUtils.createRequestParam;
import static org.allen.btc.utils.EncryptUtils.md5UpperCase;
import static org.allen.btc.utils.EncryptUtils.signStr;
import static org.allen.btc.utils.HttpUtils.requestGet;
import static org.allen.btc.utils.HttpUtils.requestPost;

import java.net.URI;
import java.util.TreeMap;

import org.allen.btc.Constants;
import org.allen.btc.Credentials;
import org.allen.btc.Trading;
import org.allen.btc.future.okcoin.domain.OkDepths;
import org.allen.btc.future.okcoin.domain.OkDepthsOriginal;
import org.allen.btc.future.okcoin.domain.OkTicker;
import org.allen.btc.future.okcoin.domain.OkTradeRequest;
import org.allen.btc.future.okcoin.domain.OkTradeResponse;
import org.allen.btc.future.okcoin.domain.OkUserFutureRequest;
import org.allen.btc.future.okcoin.domain.OkUserFutureResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 * @auther lansheng.zj
 */
public class OkCoinTrading implements Trading {

    private CloseableHttpClient httpclient;


    public OkCoinTrading() {
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
    public OkTicker getTicker(int timeout) throws Exception {
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(OKCOIN_DOMAIN).setPath(PATH_OKCOIN_TICKET_WEEK)
                    .addParameter(PARAM_OKCOIN_SYMBOL, PARAM_OKCOIN_SYMBOL_F_VALUE)
                    .addParameter(PARAM_OKCOIN_CONTRACT, PARAM_OKCOIN_CONTRACT_F_WEEK).build();

        OkTicker result = requestGet(httpclient, uri, OkTicker.class, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkDepths getDepths(int timeout) throws Exception {
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(OKCOIN_DOMAIN)
                    .setPath(Constants.PATH_OKCOIN_DEPTH_WEEK)
                    .addParameter(PARAM_OKCOIN_SYMBOL, PARAM_OKCOIN_SYMBOL_F_VALUE)
                    .addParameter(PARAM_OKCOIN_CONTRACT, PARAM_OKCOIN_CONTRACT_F_WEEK).build();

        OkDepthsOriginal result = requestGet(httpclient, uri, OkDepthsOriginal.class, timeout);
        OkDepths okDepths = result.convertToOkDepths();
        return okDepths;
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkTradeResponse trade(Object r, int timeout) throws Exception {
        OkTradeRequest request = (OkTradeRequest) r;
        URI uri = new URIBuilder().setScheme(HTTPS).setHost(OKCOIN_DOMAIN).setPath(PATH_OKCOIN_TRADE).build();
        OkTradeResponse result = doPost(request, new OkTradeResponse(), httpclient, uri, timeout);
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public OkUserFutureResponse userFutureInfo(Object r, int timeout) throws Exception {
        OkUserFutureRequest request = (OkUserFutureRequest) r;
        URI uri =
                new URIBuilder().setScheme(HTTPS).setHost(OKCOIN_DOMAIN).setPath(PATH_OKCOIN_USER_FUTURE)
                    .build();
        OkUserFutureResponse result = doPost(request, new OkUserFutureResponse(), httpclient, uri, timeout);

        return result;
    }


    @SuppressWarnings("unchecked")
    private <P, R extends Credentials> P doPost(R request, P resp, CloseableHttpClient httpclient, URI uri,
            int timeout) throws Exception {

        checkRequestNotNull(request);
        TreeMap<String, String> map = createRequestParam(request);
        map.put("api_key", request.getAccessKey());

        TreeMap<String, String> requestParam = (TreeMap<String, String>) map.clone();

        // create sign
        map.put("secret_key", request.getSecretKey());
        String signStr = signStr(map);
        String sign = md5UpperCase(signStr);
        request.setSign(sign);

        // create request param
        requestParam.put("sign", sign);

        P result = (P) requestPost(httpclient, uri, requestParam, resp.getClass(), timeout);
        return result;
    }
}
