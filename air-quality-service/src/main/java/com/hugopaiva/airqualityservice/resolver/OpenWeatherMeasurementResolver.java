package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.connection.HttpClient;
import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class OpenWeatherMeasurementResolver implements MeasurementResolver{

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherMeasurementResolver.class);


    @Autowired
    HttpClient httpClient;

    @Autowired
    private Environment environments;

    @Override
    public Measurement getActualMeasurement(Double latitude, Double longitude) throws URISyntaxException, IOException, APINotRespondingException, ParseException {
        URIBuilder uriBuilder = new URIBuilder("https://api.openweathermap.org/data/2.5/air_pollution");
        uriBuilder.addParameter("appid", environments.getProperty("openweathermap.api.key"));
        uriBuilder.addParameter("lat", (new Formatter()).format(Locale.US, "%.6f", latitude).toString());
        uriBuilder.addParameter("lon", (new Formatter()).format(Locale.US, "%.6f", longitude).toString());

        String response = this.httpClient.get(uriBuilder.build().toString());

        return jsonToMeasurement(response, latitude, longitude);
    }

    @Override
    public Measurement jsonToMeasurement(String data, Double latitude, Double longitude) throws ParseException {
        log.info("Parsing response from Open Weather API");

        JSONObject obj = (JSONObject) new JSONParser().parse(data);
        obj = (JSONObject) ((JSONArray) obj.get("list")).get(0);
        Integer aqi = ((Long) ((JSONObject) obj.get("main")).get("aqi")).intValue();
        JSONObject components = (JSONObject) obj.get("components");

        Double co =  Double.valueOf(String.valueOf(components.get("co")));
        Double no =  Double.valueOf(String.valueOf(components.get("no")));
        Double no2 =  Double.valueOf(String.valueOf(components.get("no2")));
        Double o3 = Double.valueOf(String.valueOf(components.get("o3")));
        Double so2 =  Double.valueOf(String.valueOf(components.get("so2")));
        Double pm25 =  Double.valueOf(String.valueOf(components.get("pm2_5")));
        Double pm10 =  Double.valueOf(String.valueOf(components.get("pm10")));
        Double nh3 =  Double.valueOf(String.valueOf(components.get("nh3")));

        Measurement result = new Measurement();
        result.setLatitude(latitude);
        result.setLongitude(longitude);
        result.setAirQualityIndex(aqi);
        result.setPm25(pm25);
        result.setNh3(nh3);
        result.setNo(no);
        result.setPm10(pm10);
        result.setCo(co);
        result.setNo2(no2);
        result.setO3(o3);
        result.setSo2(so2);

        result.setResponseSource(ResponseSource.OPENWEATHER);

        return result;
    }

    public Map<String, String> getLocationCoordinates(String location) throws ParseException, URISyntaxException,
            IOException, APINotRespondingException {
        URIBuilder uriBuilder = new URIBuilder("https://api.openweathermap.org/geo/1.0/direct");
        uriBuilder.addParameter("appid", environments.getProperty("openweathermap.api.key"));
        uriBuilder.addParameter("q", location);

        String response = this.httpClient.get(uriBuilder.build().toString());

        return jsonToGeoCoordinates(response);
    }

    public Map<String, String> jsonToGeoCoordinates(String data) throws ParseException {
        log.info("Parsing response from Open Weather API");

        HashMap<String, String> result = new HashMap<>();

        JSONArray jsonArray = (JSONArray) new  JSONParser().parse(data);
        JSONObject location = (JSONObject) jsonArray.get(0);

        result.put("latitude", String.valueOf(location.get("lat")));
        result.put("longitude", String.valueOf(location.get("lon")));
        result.put("location", location.get("name") + ", " + location.get("country"));

        return result;
    }
}
