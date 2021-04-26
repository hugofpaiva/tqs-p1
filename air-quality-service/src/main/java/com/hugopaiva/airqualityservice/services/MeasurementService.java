package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.exception.APINotResponding;
import com.hugopaiva.airqualityservice.exception.ResourceNotFoundException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import net.bytebuddy.implementation.bytecode.Throw;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class MeasurementService {

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    public Measurement getMeasurementByLocation(Double lat, Double lon) throws ResourceNotFoundException {
        try {
            return openWeatherMeasurementResolver.getMeasurement(lat, lon);
        } catch (Exception e){

            System.err.println("EXCECAO");
            System.err.println(e);
            throw new ResourceNotFoundException("Invalid");
        }

    }

}
