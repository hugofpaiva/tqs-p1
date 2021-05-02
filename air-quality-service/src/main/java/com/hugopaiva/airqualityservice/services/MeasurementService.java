package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.cache.Cache;
import com.hugopaiva.airqualityservice.exception.ServiceUnavailableException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


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

    public Measurement getActualMeasurementByLocation(Double lat, Double lon) throws ServiceUnavailableException {
        Measurement result = cache.getMeasurement(lat, lon);

        if (result == null) {
            log.info("Getting measurements for lat, lon: {} {} in the APIs", lat, lon);

            try {
                result = aqicnMeasurementResolver.getActualMeasurement(lat, lon);
                cache.storeMeasurement(result);

            } catch (Exception e) {
                log.error("There was an error while doing the AQICN request: {}", e.getMessage());

                try {
                    result = openWeatherMeasurementResolver.getActualMeasurement(lat, lon);
                    cache.storeMeasurement(result);

                } catch (Exception e1) {
                    log.error("There was an error while doing the Open Weather request: {}", e.getMessage());
                    throw new ServiceUnavailableException("All services unavailable.");

                }

            }

        }

        return result;

    }

}
