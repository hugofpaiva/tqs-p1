package com.hugopaiva.airqualityservice.connection;

import java.io.IOException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    public String get(String url) throws APINotRespondingException, IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;


        try {
            int timeout = 3;
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000)
                    .setSocketTimeout(timeout * 1000).build();
            client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
            HttpGet request = new HttpGet(url);

            log.info("Requesting URL: {}", url);

            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error("Error getting HttpEntity!");
            throw new APINotRespondingException("URL Not Responding: " + url);
        } finally {
            if (response != null)
                response.close();

            if (client != null)
                client.close();
        }
    }
}
