package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.AirQualityServiceApplication;
import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AirQualityServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RequestControllerMockMvcIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RequestRepository requestRepository;

    private List<Request> savedRequests = new ArrayList<>();

    @BeforeEach
    public void createTestRequests() {
        Request r1 = createTestRequest();
        requestRepository.save(r1);
        Request r2 = createTestRequest();
        requestRepository.save(r2);
        Request r3 = createTestRequest();
        requestRepository.save(r3);

        requestRepository.flush();

        savedRequests.add(r1);
        savedRequests.add(r2);
        savedRequests.add(r3);
    }

    @AfterEach
    public void resetDb() {
        requestRepository.deleteAll();
    }

    @Test
    public void testWhenInvalidPageNo_thenBadRequest() throws Exception {
        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(-1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenInvalidPageSize_thenBadRequest() throws Exception {
        mvc.perform(get("/requests")
                .param("pageSize", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testWhenNoRequests_thenNoStats() throws Exception {
        requestRepository.deleteAll();
        mvc.perform(get("/requests-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("nRequests", is(0)))
                .andExpect(jsonPath("hits", is(0)))
                .andExpect(jsonPath("misses", is(0)));
    }

    @Test
    public void testWhenRequests_thenValidStats() throws Exception {
        List<Request> hitRequests = this.savedRequests.stream()
                .filter(request -> request.getCacheResponse().equals(CacheResponseState.HIT))
                .collect(Collectors.toList());

        List<Request> missRequests = this.savedRequests.stream()
                .filter(request -> request.getCacheResponse().equals(CacheResponseState.MISS))
                .collect(Collectors.toList());

        mvc.perform(get("/requests-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("nRequests", is(this.savedRequests.size())))
                .andExpect(jsonPath("hits", is(hitRequests.size())))
                .andExpect(jsonPath("misses", is(missRequests.size())));
    }

    @Test
    public void testGetRequests_thenStatus200() throws Exception {
        List<Double> requestsLat = this.savedRequests.stream()
                .map(Request::getLatitude)
                .collect(Collectors.toList());

        List<Double> requestsLon = this.savedRequests.stream()
                .map(Request::getLongitude)
                .collect(Collectors.toList());

        List<Integer> requestsId = this.savedRequests.stream()
                .map(Request::getId)
                .map(Long::intValue)
                .collect(Collectors.toList());

        mvc.perform(get("/requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(this.savedRequests.size()))
                .andExpect(jsonPath("$[*]['latitude']", containsInRelativeOrder(requestsLat.toArray())))
                .andExpect(jsonPath("$[*]['longitude']", containsInRelativeOrder(requestsLon.toArray())))
                .andExpect(jsonPath("$[*]['id']", containsInRelativeOrder(requestsId.toArray())));
    }

    @Test
    public void testPageNoWithoutResults_thenNoResults() throws Exception {
        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    public void testPageNoAndLimitedPageSize_thenLimitedResults() throws Exception {
        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(1))
                .param("pageSize", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]['latitude']", is(savedRequests.get(2).getLatitude())))
                .andExpect(jsonPath("$[0]['longitude']", is(savedRequests.get(2).getLongitude())))
                .andExpect(jsonPath("$[0]['id']", is(savedRequests.get(2).getId().intValue())));
    }

    private Request createTestRequest() {
        Random rand = new Random();

        Request request = new Request(CacheResponseState.values()[rand.nextInt(CacheResponseState.values().length)],
                rand.nextDouble() * 90,
                rand.nextDouble() * 180);


        return requestRepository.saveAndFlush(request);
    }


}