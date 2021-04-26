package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.exception.ResourceNotFoundException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MeasurementService {

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    AQICNMeasurementResolver aqicnMeasurementResolver;

    @Autowired
    OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    public Measurement getActualMeasurementByLocation(Double lat, Double lon) throws ResourceNotFoundException {
        try {
            System.out.println("Getting");
            return aqicnMeasurementResolver.getActualMeasurement(lat, lon);
        } catch (Exception e){
            System.err.println("EXCECAO");
            e.printStackTrace();
            throw new ResourceNotFoundException("Invalid");
        }

    }

}
