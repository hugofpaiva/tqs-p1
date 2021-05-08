package com.hugopaiva.airqualityservice.controller;


import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class RequestControllerTemplateIT {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

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
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests?pageNo=" + -1, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenInvalidPageSize_thenBadRequest() throws Exception {
        ResponseEntity<String> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests?pageSize=" + 0, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testWhenNoRequests_thenNoStats() throws Exception {
        requestRepository.deleteAll();

        ResponseEntity<Map> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests-stats", Map.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Map<String, Integer> found = response.getBody();
        assertThat(found.get("nRequests"), equalTo(0));
        assertThat(found.get("hits"), equalTo(0));
        assertThat(found.get("misses"), equalTo(0));
    }

    @Test
    public void testWhenRequests_thenValidStats() throws Exception {
        List<Request> hitRequests = this.savedRequests.stream()
                .filter(request -> request.getCacheResponse().equals(CacheResponseState.HIT))
                .collect(Collectors.toList());

        List<Request> missRequests = this.savedRequests.stream()
                .filter(request -> request.getCacheResponse().equals(CacheResponseState.MISS))
                .collect(Collectors.toList());

        ResponseEntity<Map> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests-stats", Map.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        Map<String, Integer> found = response.getBody();
        assertThat(found.get("nRequests"), equalTo(this.savedRequests.size()));
        assertThat(found.get("hits"), equalTo(hitRequests.size()));
        assertThat(found.get("misses"), equalTo(missRequests.size()));
    }

    @Test
    public void testGetRequests_thenStatus200() throws Exception {
        ResponseEntity<Request[]> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests", Request[].class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        List<Request> found = Arrays.asList(response.getBody());

        assertThat(found.size(), equalTo(this.savedRequests.size()));

        for (int i = 0; i < this.savedRequests.size(); i++) {
            assertThat(found.get(i).getLatitude(), equalTo(this.savedRequests.get(i).getLatitude()));
            assertThat(found.get(i).getLongitude(), equalTo(this.savedRequests.get(i).getLongitude()));
            assertThat(found.get(i).getId(), equalTo(this.savedRequests.get(i).getId()));
        }

    }

    @Test
    public void testPageNoWithoutResults_thenNoResults() throws Exception {
        ResponseEntity<Request[]> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests?pageNo=" + 1, Request[].class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        List<Request> found = Arrays.asList(response.getBody());

        assertThat(found.size(), equalTo(0));
    }

    @Test
    public void testPageNoAndLimitedPageSize_thenLimitedResults() throws Exception {
        ResponseEntity<Request[]> response = testRestTemplate.
                getForEntity(getBaseUrl() + "/requests?pageNo=" + 1 + "&pageSize=" + 2, Request[].class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

        List<Request> found = Arrays.asList(response.getBody());

        assertThat(found.size(), equalTo(1));

        assertThat(found.get(0).getLatitude(), equalTo(this.savedRequests.get(2).getLatitude()));
        assertThat(found.get(0).getLongitude(), equalTo(this.savedRequests.get(2).getLongitude()));
        assertThat(found.get(0).getId(), equalTo(this.savedRequests.get(2).getId()));
    }


    private Request createTestRequest() {
        Random rand = new Random();

        Request request = new Request(CacheResponseState.values()[rand.nextInt(CacheResponseState.values().length)],
                rand.nextDouble() * 90,
                rand.nextDouble() * 180);


        return requestRepository.saveAndFlush(request);
    }


    public String getBaseUrl() {
        return "http://localhost:" + randomServerPort + "/api";

    }
}