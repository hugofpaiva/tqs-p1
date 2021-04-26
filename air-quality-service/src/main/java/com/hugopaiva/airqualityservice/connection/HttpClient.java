package com.hugopaiva.airqualityservice.connection;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hugopaiva.airqualityservice.exception.APINotResponding;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpClient {

    public String get(String url) throws IOException, APINotResponding {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        Logger.getLogger(OpenWeatherMeasurementResolver.class.getName()).log(Level.INFO, "Requesting url: " + url);

        CloseableHttpResponse response = client.execute(request);
        try {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch(Exception e) {
            System.err.println("Error getting Entity");
            throw new APINotResponding("URL Not Responding: " + url );
        }
        finally {
            if( response != null)
                response.close();
        }
    }
}
