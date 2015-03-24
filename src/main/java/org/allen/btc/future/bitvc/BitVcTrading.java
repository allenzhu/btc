package org.allen.btc.future.bitvc;

import static org.allen.btc.Constants.BITVC_MARKET_DOMAIN;
import static org.allen.btc.Constants.HTTPS;
import static org.allen.btc.Constants.PATH_BITVC_TICKET_WEEK;
import static org.allen.btc.HttpUtils.requestGet;

import java.net.URI;

import org.allen.btc.Trading;
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

}
