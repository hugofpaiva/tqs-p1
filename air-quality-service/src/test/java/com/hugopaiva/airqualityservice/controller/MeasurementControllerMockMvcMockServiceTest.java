package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.model.ResponseSource;
import com.hugopaiva.airqualityservice.services.MeasurementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MeasurementController.class)
class MeasurementControllerMockMvcMockServiceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MeasurementService measurementService;

    @Test
    public void testWhenInvalidValidLat_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(-182.903213))
                .param("lon", String.valueOf(90.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenInvalidValidLon_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(-85.903213))
                .param("lon", String.valueOf(185.213212))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(measurementService, times(0)).getActualMeasurementByLocation(Mockito.any(), Mockito.any());
    }

    @Test
    public void testWhenNoParams_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(measurementService, times(0)).getActualMeasurementByLocation(Mockito.any(), Mockito.any());

    }

    @Test
    public void testWhenOnlyLat_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(52.435231))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(measurementService, times(0)).getActualMeasurementByLocation(Mockito.any(), Mockito.any());

    }

    @Test
    public void testWhenOnlyLon_thenBadRequest() throws Exception {
        mvc.perform(get("/actual-measurement")
                .param("lon", String.valueOf(52.435231))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(measurementService, times(0)).getActualMeasurementByLocation(Mockito.any(), Mockito.any());

    }

    @Test
    public void testGetActualMeasurement_thenStatus200() throws Exception {
        Measurement m = createTestMeasurement();

        when(measurementService.getActualMeasurementByLocation(m.getLatitude(), m.getLongitude())).thenReturn(m);

        mvc.perform(get("/actual-measurement")
                .param("lat", String.valueOf(m.getLatitude()))
                .param("lon", String.valueOf(m.getLongitude()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", is(m.getId())))
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

        verify(measurementService, times(1)).getActualMeasurementByLocation(Mockito.any(), Mockito.any());

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

        return m;
    }

}