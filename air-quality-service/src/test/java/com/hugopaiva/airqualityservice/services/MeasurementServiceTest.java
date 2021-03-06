package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.cache.Cache;
import com.hugopaiva.airqualityservice.exception.APINotRespondingException;
import com.hugopaiva.airqualityservice.exception.LocationNotFoundException;
import com.hugopaiva.airqualityservice.exception.ServiceUnavailableException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {
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
    private Cache cache;

    @Mock
    private AQICNMeasurementResolver aqicnMeasurementResolver;

    @Mock
    private OpenWeatherMeasurementResolver openWeatherMeasurementResolver;

    @InjectMocks
    private MeasurementService measurementService;

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
    public void testGetActualMeasurementByCoordinatesCache() throws ServiceUnavailableException {
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, null)).thenReturn(this.measurement);

        Measurement found = measurementService.getActualMeasurementByCoordinates(this.latitude, this.longitude, null);

        Mockito.verify(cache, VerificationModeFactory.times(1)).getMeasurement(anyDouble(), anyDouble(), isNull());
        assertThat(found).isEqualTo(this.measurement);
    }

    @Test
    public void testGetActualMeasurementByCoordinatesAQICN() throws ServiceUnavailableException, URISyntaxException, ParseException, IOException, APINotRespondingException {
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, null)).thenReturn(null);
        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude)).thenReturn(this.measurement);

        Measurement found = measurementService.getActualMeasurementByCoordinates(this.latitude, this.longitude, null);

        Mockito.verify(cache, VerificationModeFactory.times(1)).getMeasurement(anyDouble(), anyDouble(), isNull());
        Mockito.verify(cache, VerificationModeFactory.times(1)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1)).getActualMeasurement(anyDouble(), anyDouble());
        assertThat(found).isEqualTo(this.measurement);
    }

    @Test
    public void testGetActualMeasurementByCoordinatesOpenWeather() throws URISyntaxException, ParseException, IOException, APINotRespondingException, ServiceUnavailableException {
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, null)).thenReturn(null);
        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));
        Mockito.when(openWeatherMeasurementResolver.getActualMeasurement(this.latitude, this.longitude)).thenReturn(this.measurement);


        Measurement found = measurementService.getActualMeasurementByCoordinates(this.latitude, this.longitude, null);

        Mockito.verify(cache, VerificationModeFactory.times(1)).getMeasurement(anyDouble(),
                anyDouble(), isNull());
        Mockito.verify(cache, VerificationModeFactory.times(1)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        assertThat(found).isEqualTo(this.measurement);
    }

    @Test
    public void testGetActualMeasurementByCoordinatesServiceUnavailable() throws URISyntaxException, ParseException,
            IOException, APINotRespondingException {
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, null)).thenReturn(null);
        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));
        Mockito.when(openWeatherMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));


        assertThrows(ServiceUnavailableException.class, () -> {
            measurementService.getActualMeasurementByCoordinates(this.latitude, this.longitude, null);
        }, "All services unavailable.");

        Mockito.verify(cache, VerificationModeFactory.times(1))
                .getMeasurement(anyDouble(), anyDouble(), isNull());
        Mockito.verify(cache, VerificationModeFactory.times(0)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
    }

    @Test
    public void testGetActualMeasurementByLocationCache() throws ServiceUnavailableException, APINotRespondingException,
            ParseException, URISyntaxException, IOException, LocationNotFoundException {
        Map<String, String> locationResolverResults = new HashMap<>();
        this.latitude = 38.7167;
        this.longitude = -9.1333;
        this.measurement.setLatitude(this.latitude);
        this.measurement.setLongitude(this.longitude);
        this.measurement.setLocation("Lisbon, PT");
        locationResolverResults.put("latitude", "38.7167");
        locationResolverResults.put("longitude", "-9.1333");
        locationResolverResults.put("location", "Lisbon, PT");
        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("Lisboa")).thenReturn(locationResolverResults);

        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, "Lisbon, PT")).thenReturn(this.measurement);

        Measurement found = measurementService.getActualMeasurementByLocation("Lisboa");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getLocationCoordinates("Lisboa");

        Mockito.verify(cache, VerificationModeFactory.times(1))
                .getMeasurement(anyDouble(), anyDouble(), isNotNull());
        assertThat(found).isEqualTo(this.measurement);
    }

    @Test
    public void testGetActualMeasurementByLocationAQICN() throws ServiceUnavailableException, URISyntaxException,
            ParseException, IOException, APINotRespondingException, LocationNotFoundException {
        Map<String, String> locationResolverResults = new HashMap<>();
        this.latitude = 38.7167;
        this.longitude = -9.1333;
        this.measurement.setLatitude(this.latitude);
        this.measurement.setLongitude(this.longitude);
        this.measurement.setLocation("Lisbon, PT");
        locationResolverResults.put("latitude", "38.7167");
        locationResolverResults.put("longitude", "-9.1333");
        locationResolverResults.put("location", "Lisbon, PT");

        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("Lisboa")).thenReturn(locationResolverResults);

        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, "Lisbon, PT")).thenReturn(null);

        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude)).thenReturn(this.measurement);

        Measurement found = measurementService.getActualMeasurementByLocation("Lisboa");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1)).getLocationCoordinates("Lisboa");
        Mockito.verify(cache, VerificationModeFactory.times(1)).getMeasurement(anyDouble(), anyDouble(), isNotNull());
        Mockito.verify(cache, VerificationModeFactory.times(1)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1)).getActualMeasurement(anyDouble(), anyDouble());
        assertThat(found).isEqualTo(this.measurement);
    }


    @Test
    public void testGetActualMeasurementByLocationOpenWeather() throws URISyntaxException, ParseException, IOException, APINotRespondingException, ServiceUnavailableException, LocationNotFoundException {
        Map<String, String> locationResolverResults = new HashMap<>();
        this.latitude = 38.7167;
        this.longitude = -9.1333;
        this.measurement.setLatitude(this.latitude);
        this.measurement.setLongitude(this.longitude);
        this.measurement.setLocation("Lisbon, PT");
        locationResolverResults.put("latitude", "38.7167");
        locationResolverResults.put("longitude", "-9.1333");
        locationResolverResults.put("location", "Lisbon, PT");

        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("Lisboa")).thenReturn(locationResolverResults);
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, "Lisbon, PT")).thenReturn(null);
        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));
        Mockito.when(openWeatherMeasurementResolver.getActualMeasurement(this.latitude, this.longitude)).thenReturn(this.measurement);


        Measurement found = measurementService.getActualMeasurementByLocation("Lisboa");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getLocationCoordinates("Lisboa");
        Mockito.verify(cache, VerificationModeFactory.times(1)).getMeasurement(anyDouble(),
                anyDouble(), isNotNull());
        Mockito.verify(cache, VerificationModeFactory.times(1)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        assertThat(found).isEqualTo(this.measurement);
    }

    @Test
    public void testGetActualMeasurementByLocationServiceUnavailableMeasurements() throws URISyntaxException, ParseException,
            IOException, APINotRespondingException {
        Map<String, String> locationResolverResults = new HashMap<>();
        this.latitude = 38.7167;
        this.longitude = -9.1333;
        this.measurement.setLatitude(this.latitude);
        this.measurement.setLongitude(this.longitude);
        this.measurement.setLocation("Lisbon, PT");
        locationResolverResults.put("latitude", "38.7167");
        locationResolverResults.put("longitude", "-9.1333");
        locationResolverResults.put("location", "Lisbon, PT");

        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("Lisboa")).thenReturn(locationResolverResults);
        Mockito.when(cache.getMeasurement(this.latitude, this.longitude, "Lisbon, PT")).thenReturn(null);
        Mockito.when(aqicnMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));
        Mockito.when(openWeatherMeasurementResolver.getActualMeasurement(this.latitude, this.longitude))
                .thenThrow(new APINotRespondingException("API not available"));


        assertThrows(ServiceUnavailableException.class, () -> {
            measurementService.getActualMeasurementByLocation("Lisboa");
        }, "All services unavailable.");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getLocationCoordinates("Lisboa");
        Mockito.verify(cache, VerificationModeFactory.times(1))
                .getMeasurement(anyDouble(), anyDouble(), isNotNull());
        Mockito.verify(cache, VerificationModeFactory.times(0)).storeMeasurement(this.measurement);
        Mockito.verify(aqicnMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getActualMeasurement(anyDouble(), anyDouble());
    }

    @Test
    public void testGetActualMeasurementByLocationServiceUnavailableLocationResolver() throws URISyntaxException, ParseException,
            IOException, APINotRespondingException {

        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("Lisboa"))
                .thenThrow(new APINotRespondingException("API not available"));

        assertThrows(ServiceUnavailableException.class, () -> {
            measurementService.getActualMeasurementByLocation("Lisboa");
        }, "All services unavailable.");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getLocationCoordinates("Lisboa");
    }

    @Test
    public void testGetActualMeasurementByLocationLocationNotFound() throws URISyntaxException, ParseException,
            IOException, APINotRespondingException {

        Mockito.when(openWeatherMeasurementResolver.getLocationCoordinates("xasdasdasd"))
                .thenThrow(ParseException.class);

        assertThrows(LocationNotFoundException.class, () -> {
            measurementService.getActualMeasurementByLocation("Lisboa");
        }, "Location not found.");

        Mockito.verify(openWeatherMeasurementResolver, VerificationModeFactory.times(1))
                .getLocationCoordinates("Lisboa");
    }


}