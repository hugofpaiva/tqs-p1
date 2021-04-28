package com.hugopaiva.airqualityservice.cache;

import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;

public class Cache {

    @Autowired
    MeasurementRepository measurementRepository;

    private static final Logger log = LoggerFactory.getLogger(Cache.class);
    private int timeToLive; // in seconds

    public Cache(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Cache() {
        this.timeToLive = 120;
    }

    public Measurement getMeasurement(Double lat, Double lon){
        return measurementRepository.findByLatitudeAndLongitude(lat, lon).orElse(null);
    }

    public Measurement storeMeasurement(Measurement m){
        return measurementRepository.saveAndFlush(m);
    }

    @Scheduled(fixedRate = 60*1000)
    public void cleanExpiredCachedMeasurements(){
        log.info("Running scheduled method to clean expired cached measurements");
        Date mostRecentExpiredDate = new Date(System.currentTimeMillis()-this.timeToLive*1000);
        List<Measurement> expiredMeasurements = measurementRepository.findAllByDateIsLessThanEqual(mostRecentExpiredDate);

        for(Measurement m: expiredMeasurements){
            measurementRepository.delete(m);
            log.info("Deleting expired Measurement: {}", m);
        }

    }

}
