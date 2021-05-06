package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.AirQualityServiceApplication;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AirQualityServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class MeasurementControllerMockMvcIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MeasurementRepository measurementRepository;

    @AfterEach
    public void resetDb() {
        measurementRepository.deleteAll();
    }

    @Test
    public void testWhenInvalidLatMin_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(-182.903213))
                .param("lon", String.valueOf(90.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenInvalidLatMax_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(91.903213))
                .param("lon", String.valueOf(90.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenInvalidLonMin_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(-85.903213))
                .param("lon", String.valueOf(-185.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenInvalidLonMax_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(-85.903213))
                .param("lon", String.valueOf(185.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenNoParams_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenOnlyLat_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(52.435231))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenOnlyLon_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lon", String.valueOf(52.435231))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testHavingCache_thenStatus200() throws Exception {
        Measurement m = createTestMeasurement();

        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(m.getLatitude()))
                .param("lon", String.valueOf(m.getLongitude()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(m.getId().intValue())))
                .andExpect(jsonPath("date", is(m.getDate().toInstant().toString()
                        .substring(0, m.getDate().toInstant().toString().length() - 1)+"+00:00")))
                .andExpect(jsonPath("location", is(m.getLocation())))
                .andExpect(jsonPath("responseSource", is(m.getResponseSource().toString())))
                .andExpect(jsonPath("latitude", is(m.getLatitude())))
                .andExpect(jsonPath("longitude", is(m.getLongitude())))
                .andExpect(jsonPath("airQualityIndex", is(m.getAirQualityIndex())))
                .andExpect(jsonPath("pm10", is(m.getPm10())))
                .andExpect(jsonPath("co", is(m.getCo())))
                .andExpect(jsonPath("no2", is(m.getNo2())))
                .andExpect(jsonPath("nh3", is(m.getNh3())))
                .andExpect(jsonPath("o3", is(m.getO3())))
                .andExpect(jsonPath("so2", is(m.getSo2())))
                .andExpect(jsonPath("no", is(m.getNo())))
                .andExpect(jsonPath("pm25", is(m.getPm25())))
                .andExpect(jsonPath("temperature", is(m.getTemperature())))
                .andExpect(jsonPath("wind", is(m.getWind())))
                .andExpect(jsonPath("humidity", is(m.getHumidity())))
                .andExpect(jsonPath("pressure", is(m.getPressure())));
    }

    @Test
    public void testHavingNoCacheGettingFromAPI_thenStatus200() throws Exception {
        // Assuming the first API (AQICN) will respond

        Double latitude = 50.342123;
        Double longitude = 52.342123;

        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(latitude))
                .param("lon", String.valueOf(longitude))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("responseSource", is(ResponseSource.AQICN.toString())))
                .andExpect(jsonPath("latitude", is(latitude)))
                .andExpect(jsonPath("longitude", is(longitude)))
                .andExpect(jsonPath("airQualityIndex", is(any(Integer.class))));
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



}