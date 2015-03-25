package org.allen.btc.utils;

import static org.apache.http.client.config.RequestConfig.custom;

import java.net.URI;

import org.allen.btc.exception.RemoteException;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class HttpUtils {

    public static <T> T requestGet(CloseableHttpClient httpclient, URI uri, Class<T> clazz, int timeout)
            throws Exception {
        T result = null;
        RequestConfig config =
                custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout).setStaleConnectionCheckEnabled(true).build();
        HttpGet httpGet = new HttpGet(uri);
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(config);

        CloseableHttpResponse response = httpclient.execute(httpGet, context);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String json = EntityUtils.toString(response.getEntity());
                result = (T) JSON.parseObject(json, clazz);
            }
            else {
                String errorMsg = EntityUtils.toString(response.getEntity());
                throw new RemoteException("request remote error, code=" + statusCode + ", error msg="
                        + errorMsg + ", url=" + uri.toString());
            }
        }
        finally {
            response.close();
        }

        return result;
    }
}
