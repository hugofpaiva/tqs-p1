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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Random;


import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    public void testCoordinatesWhenInvalidLatMin_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+-182.903213+"&lat="+90.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenInvalidLatMax_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+91.903213+"&lat="+90.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenInvalidLonMin_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+-85.903213+"&lat="+-185.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenInvalidLonMax_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+-85.903213+"&lat="+185.213212, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenNoParams_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenOnlyLat_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+52.435231, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenOnlyLon_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lon="+52.435231, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesWhenString_thenBadRequest() {
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat=zsasdsd&lon=asdasd", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testCoordinatesHavingCache_thenStatus200() {
        Measurement m = createTestMeasurement();

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+m.getLatitude()+"&lon="+m.getLongitude(), Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        assertThat(response.getBody(), equalTo(m));

    }

    @Test
    public void testCoordinatesHavingNoCacheGettingFromAPI_thenStatus200() {
        Double latitude = 50.342123;
        Double longitude = 52.342123;

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-coordinates?lat="+latitude+"&lon="+longitude, Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Measurement responseMeasurement = response.getBody();

        assertThat(responseMeasurement.getLatitude(), equalTo(latitude));
        assertThat(responseMeasurement.getLongitude(), equalTo(longitude));
        assertThat(responseMeasurement.getAirQualityIndex(), is(any(Integer.class)));

    }

    @Test
    public void testLocationWhenBadLocation_thenNotFound(){
        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-location?location=localizacaonaoexistente", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void testLocationHavingCache_thenStatus200() {
        Measurement m = createTestMeasurement();
        m.setLatitude(38.7167);
        m.setLongitude(-9.1333);
        m.setLocation("Lisbon, PT");
        m = measurementRepository.saveAndFlush(m);

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-location?location=Lisboa", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        assertThat(response.getBody(), equalTo(m));
    }

    @Test
    public void testLocationHavingNoCacheGettingFromAPI_thenStatus200() {
        String location = "Lisboa";

        ResponseEntity<Measurement> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/actual-measurement-location?location=Lisboa", Measurement.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Measurement responseMeasurement = response.getBody();

        assertThat(responseMeasurement.getLatitude(), equalTo(38.7167));
        assertThat(responseMeasurement.getLongitude(), equalTo(-9.1333));
        assertThat(responseMeasurement.getLocation(), equalTo("Lisbon, PT"));
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