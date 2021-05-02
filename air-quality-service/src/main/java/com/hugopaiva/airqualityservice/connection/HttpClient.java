package com.hugopaiva.airqualityservice.connection;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hugopaiva.airqualityservice.exception.APINotResponding;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    public String get(String url) throws APINotResponding, IOException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try {
            client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);

            log.info("Requesting URL: {}", url);

            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            log.error("Error getting HttpEntity!");
            throw new APINotResponding("URL Not Responding: " + url);
        } finally {
            if (response != null)
                response.close();

            if (client != null)
                client.close();
        }
    }
}
