package com.hugopaiva.airqualityservice.resolver;

import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.model.Measurement;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface MeasurementResolver {

    public Measurement getActualMeasurement(Double latitude, Double longitude) throws URISyntaxException, IOException, APINotRespondingException, ParseException;

    public Measurement jsonToMeasurement(String data, Double latitude, Double longitude) throws ParseException;
}
