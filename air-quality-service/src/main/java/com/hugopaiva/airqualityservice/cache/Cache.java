package com.hugopaiva.airqualityservice.cache;

import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class Cache {

    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    RequestRepository requestRepository;

    private static final Logger log = LoggerFactory.getLogger(Cache.class);
    private int timeToLive; // in seconds

    public Cache(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Cache() {
        this.timeToLive = 120;
    }

    public Measurement getMeasurement(Double lat, Double lon, String location) {
        log.info("Getting Measurement for lat {} and lon {} in the cache", lat, lon);
        Measurement m = measurementRepository.findByLatitudeAndLongitude(lat, lon).orElse(null);

        if (m != null) {
            if (hasExpired(m)) {
                log.info("Measurement expired in the cache");
                deleteMeasurementFromCache(m);
                requestRepository.saveAndFlush(new Request(CacheResponseState.MISS, lat, lon, location));
                return null;
            } else {
                log.info("Measurement retrieved from cache");
                requestRepository.saveAndFlush(new Request(CacheResponseState.HIT, lat, lon, location));
            }
        } else {
            log.info("Measurement not found in the cache");
            requestRepository.saveAndFlush(new Request(CacheResponseState.MISS, lat, lon, location));
        }

        return m;
    }

    public Measurement storeMeasurement(Measurement m) {
        Measurement measurementCheck = measurementRepository.findByLatitudeAndLongitude(m.getLatitude(), m.getLongitude()).orElse(null);
        Measurement result;
        if (measurementCheck == null) {
            result = measurementRepository.saveAndFlush(m);
            log.info("Stored Measurement {} on cache", result);
        } else {
            result = measurementCheck;
            log.info("Measurement {} was already on cache", result);
        }
        return result;
    }

    public void deleteMeasurementFromCache(Measurement m) {
        log.info("Deleting Measurement {} from cache", m);
        measurementRepository.delete(m);
    }

    public boolean hasExpired(Measurement m) {
        log.info("Checking if Measurement {} is expired", m);
        Date mostRecentExpiredDate = new Date(System.currentTimeMillis() - this.timeToLive * 1000);
        return m.getDate().before(mostRecentExpiredDate);
    }

    @Scheduled(fixedRate = 60 * 1000)
    public void cleanExpiredCachedMeasurements() {
        log.info("Running scheduled method to clean expired cached measurements");
        Date mostRecentExpiredDate = new Date(System.currentTimeMillis() - this.timeToLive * 1000);
        List<Measurement> expiredMeasurements = measurementRepository.findAllByDateIsLessThanEqual(mostRecentExpiredDate);

        for (Measurement m : expiredMeasurements) {
            log.info("Deleting expired Measurement: {}", m);
            measurementRepository.delete(m);
        }

    }

}
