package org.allen.btc.future.okcoin;

import static org.allen.btc.Constants.HTTPS;
import static org.allen.btc.Constants.OKCOIN_DOMAIN;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL;
import static org.allen.btc.Constants.PARAM_OKCOIN_SYMBOL_VALUE;
import static org.allen.btc.Constants.PATH_OKCOIN_TICKET;
import static org.allen.btc.HttpUtils.requestGet;

import java.net.URI;

import org.allen.btc.Trading;
import org.allen.btc.future.okcoin.domain.OkTicker;
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
                new URIBuilder().setScheme(HTTPS).setHost(OKCOIN_DOMAIN).setPath(PATH_OKCOIN_TICKET)
                    .addParameter(PARAM_OKCOIN_SYMBOL, PARAM_OKCOIN_SYMBOL_VALUE).build();

        OkTicker result = requestGet(httpclient, uri, OkTicker.class, timeout);
        return result;
    }

}
