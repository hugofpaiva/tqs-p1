package com.hugopaiva.airqualityservice.cache;

import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheTest {
    private Double latitude;
    private Double longitude;
    private Long id;
    private Date date;
    private Integer airQualityIndex;
    private Double pm25;
    private Double pm10;
    private Double wind;
    private Measurement measurement;

    @Mock
    RequestRepository requestRepository;

    @Mock
    MeasurementRepository measurementRepository;

    @InjectMocks
    Cache cache; // Using default 120 seconds of TTL

    @InjectMocks
    Cache cache60sec = new Cache(60);


    @BeforeEach
    void setUp() {
        Random rand = new Random();
        Measurement measurement = new Measurement();
        this.measurement = measurement;
        this.latitude = rand.nextDouble() * 90;
        measurement.setLatitude(this.latitude);
        this.longitude = rand.nextDouble() * 180;
        measurement.setLongitude(this.longitude);
        this.id = rand.nextLong();
        measurement.setId(this.id);
        this.date = new Date(System.currentTimeMillis());
        measurement.setDate(this.date);
        this.airQualityIndex = rand.nextInt(501);
        measurement.setAirQualityIndex(this.airQualityIndex);
        this.pm25 = rand.nextDouble() * 50;
        measurement.setPm25(this.pm25);
        this.pm10 = rand.nextDouble() * 50;
        measurement.setPm10(this.pm10);
        this.wind = rand.nextDouble() * 50;
        measurement.setWind(this.wind);
    }

    @Test
    void testGetValidMeasurement() {
        when(measurementRepository.findByLatitudeAndLongitude(this.latitude, this.longitude))
                .thenReturn(Optional.of(this.measurement));

        Measurement cacheMeasurement = cache.getMeasurement(this.latitude, this.longitude, null);

        assertNotNull(cacheMeasurement);

        assertEquals(this.latitude, cacheMeasurement.getLatitude());
        assertEquals(this.longitude, cacheMeasurement.getLongitude());
        assertEquals(this.id, cacheMeasurement.getId());
        assertEquals(this.date, cacheMeasurement.getDate());
        assertEquals(this.airQualityIndex, cacheMeasurement.getAirQualityIndex());
        assertEquals(this.pm25, cacheMeasurement.getPm25());
        assertEquals(this.pm10, cacheMeasurement.getPm10());
        assertEquals(this.wind, cacheMeasurement.getWind());
        assertEquals(null, cacheMeasurement.getNo2());
        assertEquals(null, cacheMeasurement.getNo());
        assertEquals(null, cacheMeasurement.getCo());
        assertEquals(null, cacheMeasurement.getNh3());
        assertEquals(null, cacheMeasurement.getO3());
        assertEquals(null, cacheMeasurement.getSo2());
        assertEquals(null, cacheMeasurement.getTemperature());
        assertEquals(null, cacheMeasurement.getHumidity());
        assertEquals(null, cacheMeasurement.getPressure());

        verify(measurementRepository, times(1)).findByLatitudeAndLongitude(anyDouble(), anyDouble());

        verify(requestRepository, times(1)).saveAndFlush(new Request(CacheResponseState.HIT,
                this.latitude, this.longitude, null));

    }

    @Test
    void testGetNonValidMeasurement() {
        when(measurementRepository.findByLatitudeAndLongitude(this.latitude, this.longitude))
                .thenReturn(Optional.empty());
        Measurement cacheMeasurement = cache.getMeasurement(this.latitude, this.longitude, "Example Location");

        assertNull(cacheMeasurement);

        verify(measurementRepository, times(1)).findByLatitudeAndLongitude(anyDouble(), anyDouble());

        verify(requestRepository, times(1)).saveAndFlush(new Request(CacheResponseState.MISS,
                this.latitude, this.longitude, "Example Location"));

    }

    @Test
    void testGetExpiredMeasurement() {
        this.date = new Date(System.currentTimeMillis() - 125 * 1000); // A date with more that 120 sec
        this.measurement.setDate(this.date);
        when(measurementRepository.findByLatitudeAndLongitude(this.latitude, this.longitude))
                .thenReturn(Optional.of(this.measurement));

        Measurement cacheMeasurement = cache.getMeasurement(this.latitude, this.longitude, null);

        assertNull(cacheMeasurement);

        verify(measurementRepository, times(1)).findByLatitudeAndLongitude(anyDouble(), anyDouble());
        verify(measurementRepository, times(1)).delete(this.measurement);


        verify(requestRepository, times(1)).saveAndFlush(new Request(CacheResponseState.MISS,
                this.latitude, this.longitude, null));

    }

    @Test
    void testGetExpiredMeasurementOnDifferentCache() {
        this.date = new Date(System.currentTimeMillis() - 65 * 1000); // A date with more that 60 sec
        this.measurement.setDate(this.date);
        when(measurementRepository.findByLatitudeAndLongitude(this.latitude, this.longitude))
                .thenReturn(Optional.of(this.measurement));

        Measurement cacheMeasurement = this.cache60sec.getMeasurement(this.latitude, this.longitude, "Example Location");

        assertNull(cacheMeasurement);

        verify(measurementRepository, times(1)).findByLatitudeAndLongitude(anyDouble(), anyDouble());
        verify(measurementRepository, times(1)).delete(this.measurement);


        verify(requestRepository, times(1)).saveAndFlush(new Request(CacheResponseState.MISS,
                this.latitude, this.longitude, "Example Location"));

    }

    @Test
    void testScheduleCleaningCache() {
        // The test of the @Schedule is on the main tests because of Spring Context
        this.date = new Date(System.currentTimeMillis() - 125 * 1000); // A date with more that 120 sec
        this.measurement.setDate(this.date);
        when(measurementRepository.findAllByDateIsLessThanEqual(any(Date.class)))
                .thenReturn(new ArrayList<>(Arrays.asList(
                        this.measurement
                )));

        cache.cleanExpiredCachedMeasurements();

        verify(measurementRepository, times(1)).findAllByDateIsLessThanEqual(any(Date.class));

        verify(measurementRepository, times(1)).delete(this.measurement);

        assertNull(cache.getMeasurement(this.latitude, this.longitude, null));

        verify(requestRepository, times(1)).saveAndFlush(new Request(CacheResponseState.MISS,
                this.latitude, this.longitude, null));

    }

    @Test
    void testHasExpiredValidMeasurement() {
        assertFalse(cache.hasExpired(this.measurement));
    }

    @Test
    void testHasExpiredInvalidMeasurement() {
        this.date = new Date(System.currentTimeMillis() - 125 * 1000); // A date with more that 120 sec
        this.measurement.setDate(this.date);
        assertTrue(cache.hasExpired(this.measurement));
    }

    @Test
    void testDeleteMeasurement() {
        cache.deleteMeasurementFromCache(this.measurement);

        verify(measurementRepository, times(1)).delete(this.measurement);

    }

    @Test
    void testStoreMeasurement() {
        when(measurementRepository.saveAndFlush(this.measurement)).thenReturn(this.measurement);

        assertEquals(cache.storeMeasurement(this.measurement), this.measurement);

        verify(measurementRepository, times(1)).saveAndFlush(this.measurement);

    }


}