package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import com.hugopaiva.airqualityservice.resolver.AQICNMeasurementResolver;
import com.hugopaiva.airqualityservice.resolver.OpenWeatherMeasurementResolver;
import org.bouncycastle.cert.ocsp.Req;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @Test
    public void testGiven3Requests_whenGetRequests_thenReturn3Records() {
        Request r1 = new Request(CacheResponseState.MISS, 43.213212, 50.123321);
        Request r2 = new Request(CacheResponseState.HIT, 43.223212, 51.123321);
        Request r3 = new Request(CacheResponseState.HIT, 41.223212, 49.123321);

        List<Request> allRequests = Arrays.asList(r1, r2, r3);
        Page<Request> pageRequest = new PageImpl(allRequests, PageRequest.of(0, 10), allRequests.size());

        Mockito.when(requestRepository.findAll(any(Pageable.class))).thenReturn(pageRequest);

        List<Request> found = requestService.getRequests(0, 10);
        Mockito.verify(requestRepository, VerificationModeFactory.times(1))
                .findAll(any(Pageable.class));
        assertThat(found).hasSize(3).extracting(Request::getLatitude).contains(r1.getLatitude(), r2.getLatitude(),
                r3.getLatitude());
    }

    @Test
    public void testGivenNoRequests_whenGetRequests_thenReturn0Records() {
        Page<Request> pageRequest = new PageImpl(new ArrayList<>(), PageRequest.of(0, 10), new ArrayList<>().size());

        Mockito.when(requestRepository.findAll(any(Pageable.class))).thenReturn(pageRequest);

        List<Request> found = requestService.getRequests(0, 10);
        Mockito.verify(requestRepository, VerificationModeFactory.times(1))
                .findAll(any(Pageable.class));
        assertThat(found).hasSize(0);
    }

    @Test
    public void testHaving3Requests_whenGetRequestsStats_thenReturnValidRequestsStats() {
        Map<String, Long> requestsStats = new HashMap<>();
        requestsStats.put("nRequests", 3L);
        requestsStats.put("hits", 2L);
        requestsStats.put("misses", 1L);

        Mockito.when(requestRepository.count()).thenReturn(3L);
        Mockito.when(requestRepository.countByCacheResponse(CacheResponseState.HIT)).thenReturn(2L);
        Mockito.when(requestRepository.countByCacheResponse(CacheResponseState.MISS)).thenReturn(1L);

        Map<String, Long> found = requestService.getRequestsStats();
        Mockito.verify(requestRepository, VerificationModeFactory.times(1)).count();
        Mockito.verify(requestRepository, VerificationModeFactory.times(2))
                .countByCacheResponse(any(CacheResponseState.class));
        assertThat(found).isEqualTo(requestsStats);
    }

    @Test
    public void testHaving0Requests_whenGetRequestsStats_thenReturnValidRequestsStats() {
        Map<String, Long> requestsStats = new HashMap<>();
        requestsStats.put("nRequests", 0L);
        requestsStats.put("hits", 0L);
        requestsStats.put("misses", 0L);

        Mockito.when(requestRepository.count()).thenReturn(0L);
        Mockito.when(requestRepository.countByCacheResponse(CacheResponseState.HIT)).thenReturn(0L);
        Mockito.when(requestRepository.countByCacheResponse(CacheResponseState.MISS)).thenReturn(0L);

        Map<String, Long> found = requestService.getRequestsStats();
        Mockito.verify(requestRepository, VerificationModeFactory.times(1)).count();
        Mockito.verify(requestRepository, VerificationModeFactory.times(2))
                .countByCacheResponse(any(CacheResponseState.class));
        assertThat(found).isEqualTo(requestsStats);
    }

}