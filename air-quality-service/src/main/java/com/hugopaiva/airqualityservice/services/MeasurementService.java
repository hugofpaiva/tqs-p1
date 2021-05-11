package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.cache.Cache;
import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.exception.LocationNotFoundException;
import com.hugopaiva.airqualityservice.exception.ServiceUnavailableException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class MeasurementService {

    private static final Logger log = LoggerFactory.getLogger(MeasurementService.class);

    @Autowired
    Cache cache;

    @Autowired
    AQICNMeasurementResolver aqicnMeasurementResolver;

    @Autowired
    OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    public Measurement getActualMeasurementByCoordinates(Double lat, Double lon, String location) throws ServiceUnavailableException {
        Measurement result = cache.getMeasurement(lat, lon, location);

        if (result == null) {
            log.info("Getting measurements for lat, lon: {} {} in the APIs", lat, lon);

            try {
                result = aqicnMeasurementResolver.getActualMeasurement(lat, lon);
                result.setLocation(location);
                cache.storeMeasurement(result);

            } catch (Exception e) {
                log.error("There was an error while doing the AQICN request: {}", e.getMessage());

                try {
                    result = openWeatherMeasurementResolver.getActualMeasurement(lat, lon);
                    result.setLocation(location);
                    cache.storeMeasurement(result);

                } catch (Exception e1) {
                    log.error("There was an error while doing the Open Weather request: {}", e.getMessage());
                    throw new ServiceUnavailableException("All services unavailable.");

                }

            }

        }

        return result;

    }

    public Measurement getActualMeasurementByLocation(String location) throws LocationNotFoundException, ServiceUnavailableException {
        log.info("Getting Coordinates for location: {} in the API", location);

        try {
            Map<String, String> result = openWeatherMeasurementResolver.getLocationCoordinates(location);

            String locationRequest = result.get("location");
            Double latitude = Double.valueOf(result.get("latitude"));
            Double longitude = Double.valueOf(result.get("longitude"));

            return getActualMeasurementByCoordinates(latitude, longitude, locationRequest);


        } catch (ServiceUnavailableException e) {
            throw new ServiceUnavailableException(e.getMessage());
        } catch (APINotRespondingException e) {
            log.error("There was an error while doing the OpenWeather Location Resolver request: {}", e.getMessage());
            throw new ServiceUnavailableException("All services unavailable.");

        } catch (Exception e) {
            log.error("There was an error while doing the OpenWeather Location Resolver request: {}", e.getMessage());
            throw new LocationNotFoundException("Location not found.");

        }

    }

}
