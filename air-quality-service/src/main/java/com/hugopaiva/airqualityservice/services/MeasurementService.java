package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.cache.Cache;
import com.hugopaiva.airqualityservice.exception.ResourceNotFoundException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MeasurementService {

    private static final Logger log = LoggerFactory.getLogger(MeasurementService.class);

    private Cache cache = new Cache();

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    AQICNMeasurementResolver aqicnMeasurementResolver;

    @Autowired
    OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    public Measurement getActualMeasurementByLocation(Double lat, Double lon) throws ResourceNotFoundException {
        try {
            log.info("Getting measurements for lat, lon: {} {}", lat, lon);
            return aqicnMeasurementResolver.getActualMeasurement(lat, lon);
        } catch (Exception e){
            log.error("Exceção");
            e.printStackTrace();
            throw new ResourceNotFoundException("Invalid");
        }

    }

}
