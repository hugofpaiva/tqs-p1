package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.services.RequestService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RequestController.class)
class RequestControllerMockMvcMockServiceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    @Test
    public void testWhenInvalidPageNo_thenBadRequest() throws Exception {
        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(-1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, times(0)).getRequests(Mockito.any(), Mockito.any());

    }

    @Test
    public void testWhenInvalidPageSize_thenBadRequest() throws Exception {
        mvc.perform(get("/requests")
                .param("pageSize", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, times(0)).getRequests(Mockito.any(), Mockito.any());
    }

    @Test
    public void testWhenRequests_thenValidStats() throws Exception {
        Map<String, Long> response = new HashMap<>();
        response.put("nRequests", 3L);
        response.put("hits", 2L);
        response.put("misses", 1L);


        when(requestService.getRequestsStats()).thenReturn(response);


        mvc.perform(get("/requests-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("nRequests", is(3)))
                .andExpect(jsonPath("hits", is(2)))
                .andExpect(jsonPath("misses", is(1)));

        verify(requestService, times(1)).getRequestsStats();
    }

    @Test
    public void testWhenNoRequests_thenNoStats() throws Exception {
        Map<String, Long> response = new HashMap<>();
        response.put("nRequests", 0L);
        response.put("hits", 0L);
        response.put("misses", 0L);


        when(requestService.getRequestsStats()).thenReturn(response);

        mvc.perform(get("/requests-stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("nRequests", is(0)))
                .andExpect(jsonPath("hits", is(0)))
                .andExpect(jsonPath("misses", is(0)));

        verify(requestService, times(1)).getRequestsStats();
    }

    @Test
    public void testGetRequests_thenStatus200() throws Exception {
        List<Request> requests = new ArrayList<>();
        requests.add(createTestRequest());
        requests.add(createTestRequest());
        requests.add(createTestRequest());
        Collections.reverse(requests);

        List<Double> requestsLat = requests.stream()
                .map(Request::getLatitude)
                .collect(Collectors.toList());

        List<Double> requestsLon = requests.stream()
                .map(Request::getLongitude)
                .collect(Collectors.toList());

        when(requestService.getRequests(0,10)).thenReturn(requests);

        mvc.perform(get("/requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(requests.size()))
                .andExpect(jsonPath("$[*]['latitude']", Matchers.contains(requestsLat.toArray())))
                .andExpect(jsonPath("$[*]['longitude']", Matchers.contains(requestsLon.toArray())));

        verify(requestService, times(1)).getRequests(0,10);
    }

    @Test
    public void testPageNoWithoutResults_thenNoResults() throws Exception {
        when(requestService.getRequests(1,10)).thenReturn(new ArrayList<>());

        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestService, times(1)).getRequests(1,10);

    }

    @Test
    public void testPageNoAndLimitedPageSize_thenLimitedResults() throws Exception {
        Request request = createTestRequest();
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        when(requestService.getRequests(1,2)).thenReturn(requests);

        mvc.perform(get("/requests")
                .param("pageNo", String.valueOf(1))
                .param("pageSize", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]['latitude']", is(request.getLatitude())))
                .andExpect(jsonPath("$[0]['longitude']", is(request.getLongitude())));
    }


    private Request createTestRequest() {
        Random rand = new Random();

        Request request = new Request(CacheResponseState.values()[rand.nextInt(CacheResponseState.values().length)],
                rand.nextDouble() * 90,
                rand.nextDouble() * 180);


        return request;
    }

}