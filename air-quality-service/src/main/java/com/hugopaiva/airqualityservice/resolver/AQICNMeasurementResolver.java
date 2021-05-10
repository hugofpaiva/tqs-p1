package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.connection.HttpClient;
import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import org.apache.http.client.utils.URIBuilder;
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
import java.util.*;

@Component
public class AQICNMeasurementResolver implements MeasurementResolver{

    private static final Logger log = LoggerFactory.getLogger(AQICNMeasurementResolver.class);

    @Autowired
    HttpClient httpClient;

    @Autowired
    private Environment environments;

    @Override
    public Measurement getActualMeasurement(Double latitude, Double longitude) throws URISyntaxException, APINotRespondingException, ParseException, IOException {
        URIBuilder uriBuilder = new URIBuilder("https://api.waqi.info/feed/geo:" +
                (new Formatter()).format(Locale.US, "%.6f", latitude) +";"+
                (new Formatter()).format(Locale.US, "%.6f", longitude)+"/");
        uriBuilder.addParameter("token", environments.getProperty("aqicn.api.key"));

        String response = this.httpClient.get(uriBuilder.build().toString());

        return jsonToMeasurement(response, latitude, longitude);
    }

    @Override
    public Measurement jsonToMeasurement(String data, Double latitude, Double longitude) throws ParseException {
        log.info("Parsing response from AQICN API");

        Measurement result = new Measurement();
        JSONObject obj = (JSONObject) new JSONParser().parse(data);
        JSONObject content = (JSONObject) obj.get("data");
        Integer aqi = ((Long) content.get("aqi")).intValue();
        result.setAirQualityIndex(aqi);
        result.setLongitude(longitude);
        result.setLatitude(latitude);

        JSONObject iaqi = (JSONObject) content.get("iaqi");

        Double co =  iaqi.containsKey("co") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("co")).get("v"))) : null;
        result.setCo(co);

        Double humidity =  iaqi.containsKey("h") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("h")).get("v"))) : null;
        result.setHumidity(humidity);

        Double no2 =  iaqi.containsKey("no2") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("no2")).get("v"))) : null;
        result.setNo2(no2);

        Double o3 =  iaqi.containsKey("o3") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("o3")).get("v"))) : null;
        result.setO3(o3);

        Double pressure =  iaqi.containsKey("p") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("p")).get("v"))) : null;
        result.setPressure(pressure);

        Double pm10 =  iaqi.containsKey("pm10") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("pm10")).get("v"))) : null;
        result.setPm10(pm10);

        Double pm25 =  iaqi.containsKey("pm25") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("pm25")).get("v"))) : null;
        result.setPm25(pm25);

        Double so2 =  iaqi.containsKey("so2") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("so2")).get("v"))) : null;
        result.setSo2(so2);

        Double temperature =  iaqi.containsKey("t") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("t")).get("v"))) : null;
        result.setTemperature(temperature);

        Double wind =  iaqi.containsKey("w") ? Double.valueOf(String.valueOf(((JSONObject) iaqi.get("w")).get("v"))) : null;
        result.setWind(wind);

        result.setResponseSource(ResponseSource.AQICN);

        return result;
    }

}
