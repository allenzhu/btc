package org.allen.btc.utils;

import static org.apache.http.client.config.RequestConfig.custom;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.allen.btc.exception.RemoteException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * @auther lansheng.zj
 */
public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);


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
                // for test
                // System.out.println(json);
                result = (T) JSON.parseObject(json, clazz);
            }
            else {
                String errorMsg = EntityUtils.toString(response.getEntity());
                throw new RemoteException("requestGet remote error, code=" + statusCode + ", error msg="
                        + errorMsg + ", url=" + uri.toString());
            }
        }
        finally {
            response.close();
        }

        return result;
    }


    public static <T> T requestPost(CloseableHttpClient httpclient, URI uri, SortedMap<String, String> map,
            Class<T> clazz, int timeout) throws Exception {
        T result = null;

        HttpPost httppost = new HttpPost(uri);
        httppost.setHeader("contentType", "application/x-www-form-urlencoded");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : map.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        httppost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

        RequestConfig config =
                RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                    .setSocketTimeout(timeout).setStaleConnectionCheckEnabled(true).build();
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(config);
        CloseableHttpResponse response = httpclient.execute(httppost, context);

        try {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                String json = EntityUtils.toString(response.getEntity());
                result = (T) JSON.parseObject(json, clazz);
                if (null != json && (json.contains("code") && json.contains("message"))) {
                    log.error("result fail, result=" + json + ", uri=" + uri + ", map=" + map);
                }
                if (null != json && (json.contains("error_code") && json.contains("result"))) {
                    log.error("result fail, result=" + json + ", uri=" + uri + ", map=" + map);
                }
            }
            else {
                String errorMsg = EntityUtils.toString(response.getEntity());
                throw new RemoteException("requestPost remote error, code=" + statusCode + ", error msg="
                        + errorMsg + ", url=" + uri.toString());
            }
        }
        finally {
            response.close();
        }

        return result;
    }
}
