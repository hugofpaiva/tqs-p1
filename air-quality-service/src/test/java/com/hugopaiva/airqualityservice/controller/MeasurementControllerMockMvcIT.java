package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.AirQualityServiceApplication;
import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;


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

}