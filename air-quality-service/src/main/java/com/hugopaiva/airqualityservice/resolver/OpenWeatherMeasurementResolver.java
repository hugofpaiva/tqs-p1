package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.connection.HttpClient;
import com.hugopaiva.airqualityservice.exception.APINotResponding;
import com.hugopaiva.airqualityservice.model.Measurement;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OpenWeatherMeasurementResolver implements MeasurementResolver{

    @Autowired
    HttpClient httpClient;

    @Autowired
    private Environment environments;

    @Override
    public Measurement getMeasurement(Double latitude, Double longitude) throws URISyntaxException, IOException, APINotResponding, ParseException {
        URIBuilder uriBuilder = new URIBuilder("http://api.openweathermap.org/data/2.5/air_pollution");
        uriBuilder.addParameter("appid", environments.getProperty("openweathermap.api.key"));
        uriBuilder.addParameter("lat", (new Formatter()).format(Locale.US, "%.6f", latitude).toString());
        uriBuilder.addParameter("lon", (new Formatter()).format(Locale.US, "%.6f", longitude).toString());

        String response = this.httpClient.get(uriBuilder.build().toString());

        return JSONToMeasurement(response, latitude, longitude);
    }

    @Override
    public Measurement JSONToMeasurement(String data, Double latitude, Double longitude) throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(data);
        obj = (JSONObject) ((JSONArray) obj.get("list")).get(0);
        Integer aqi = ((Long) ((JSONObject) obj.get("main")).get("aqi")).intValue();
        JSONObject components = (JSONObject) obj.get("components");

        Double co =  (Double) components.get("co");
        Double no =  (Double) components.get("no");
        Double no2 =  (Double) components.get("no2");
        Double o3 =  (Double) components.get("o3");
        Double so2 =  (Double)  components.get("so2");
        Double pm2_5 =  (Double) components.get("pm2_5");
        Double pm10 =  (Double) components.get("pm10");
        Double nh3 =  (Double) components.get("nh3");

        System.out.printf("co: %f no2: %f, lat: %f", co, no2, latitude);

        return new Measurement(latitude, longitude, aqi, pm10, co, no2, o3, so2);
    }
}
