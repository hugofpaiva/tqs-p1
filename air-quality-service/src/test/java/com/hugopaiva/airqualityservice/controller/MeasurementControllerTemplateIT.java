package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Random;


import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class MeasurementControllerTemplateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MeasurementRepository measurementRepository;

    @AfterEach
    public void resetDb() {
        measurementRepository.deleteAll();
    }


    @Test
    public void testWhenInvalidLatMin_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+-182.903213+"&lat="+90.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenInvalidLatMax_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+91.903213+"&lat="+90.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenInvalidLonMin_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+-85.903213+"&lat="+-185.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenInvalidLonMax_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+-85.903213+"&lat="+185.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenNoParams_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenOnlyLat_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+52.435231, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenOnlyLon_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lon="+52.435231, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testHavingCache_thenStatus200() {
        Measurement m = createTestMeasurement();

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+m.getLatitude()+"&lon="+m.getLongitude(), Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        assertThat(response.getBody(), equalTo(m));

    }

    @Test
    public void testHavingNoCacheGettingFromAPI_thenStatus200() {
        // Assuming the first API (AQICN) will respond

        Double latitude = 50.342123;
        Double longitude = 52.342123;

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement?lat="+latitude+"&lon="+longitude, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Measurement responseMeasurement = response.getBody();

        assertThat(responseMeasurement.getLatitude(), equalTo(latitude));
        assertThat(responseMeasurement.getLongitude(), equalTo(longitude));
        assertThat(responseMeasurement.getResponseSource(), equalTo(ResponseSource.AQICN));
        assertThat(responseMeasurement.getAirQualityIndex(), is(any(Integer.class)));

    }

    private Measurement createTestMeasurement() {
        Random rand = new Random();
        Measurement m = new Measurement();
        m.setLatitude(rand.nextDouble() * 90);
        m.setLongitude(rand.nextDouble() * 180);
        m.setId(rand.nextLong());
        m.setResponseSource(ResponseSource.AQICN);
        m.setDate(new Date(System.currentTimeMillis()));
        m.setAirQualityIndex(rand.nextInt(501));
        m.setPm25(rand.nextDouble() * 50);
        m.setPm10(rand.nextDouble() * 50);
        m.setWind(rand.nextDouble() * 50);

        return measurementRepository.saveAndFlush(m);
    }


    public String getBaseUrl() {
        return "http://localhost:"+randomServerPort+"/api";

    }
}