package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.connection.HttpClient;
import com.hugopaiva.airqualityservice.model.Measurement;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

public class AQICNMeasurementResolver implements MeasurementResolver{

    @Autowired
    HttpClient httpClient;

    @Override
    public Measurement getMeasurement(Double latitude, Double longitude) {
        return null;
    }

    @Override
    public Measurement JSONToMeasurement(String data, Double latitude, Double longitude) throws ParseException {
        return null;
    }

}
