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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        Page<Request> pageRequest = new PageImpl(allRequests, PageRequest.of(1, 15), allRequests.size());


        Mockito.when(requestRepository.findAll(any(Pageable.class))).thenReturn(pageRequest);

        List<Request> found = requestService.getRequests(1, 15);
        Mockito.verify(requestRepository, VerificationModeFactory.times(1)).findAll(any(Pageable.class));
        assertThat(found).hasSize(3).extracting(Request::getLatitude).contains(r1.getLatitude(), r2.getLatitude(),
                r3.getLatitude());
    }

}