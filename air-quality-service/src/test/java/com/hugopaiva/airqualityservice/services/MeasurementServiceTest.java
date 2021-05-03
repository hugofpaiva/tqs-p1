package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.repository.MeasurementRepository;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private AQICNMeasurementResolver aqicnMeasurementResolver;

    @Mock
    private OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    @InjectMocks
    private MeasurementService measurementService;


}