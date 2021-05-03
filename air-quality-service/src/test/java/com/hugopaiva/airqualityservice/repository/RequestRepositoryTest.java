package com.hugopaiva.airqualityservice.repository;


import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    public void testWhenFindByRequestByExistingId_thenReturnRequest() {
        Request request = new Request(CacheResponseState.HIT, 50.232122, 55.123122);
        entityManager.persistAndFlush(request);

        Request fromDb = requestRepository.findById(request.getId()).orElse(null);
        assertThat(fromDb).isNotNull().isEqualTo(request);
    }

    @Test
    public void testWhenFindByInvalidId_thenReturnNull() {
        Request fromDb = requestRepository.findById(-111L).orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    public void testGivenSetOfRequests_whenCountByCacheResponse_thenReturnAllValidRequests() {
        Request request1 = new Request(CacheResponseState.HIT, 50.232122, 55.123122);
        Request request2 = new Request(CacheResponseState.MISS, 50.332122, 55.223122);
        Request request3 = new Request(CacheResponseState.HIT, 50.432122, 55.323122);

        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(request3);
        entityManager.flush();

        Long hitCount = requestRepository.countByCacheResponse(CacheResponseState.HIT);
        assertThat(hitCount).isEqualTo(2);

        Long missCount = requestRepository.countByCacheResponse(CacheResponseState.MISS);
        assertThat(missCount).isEqualTo(1);
    }

    @Test
    public void testGivenSetOfRequests_whenFindAll_thenReturnAllRequests() {
        Request request1 = new Request(CacheResponseState.HIT, 50.232122, 55.123122);
        Request request2 = new Request(CacheResponseState.MISS, 50.332122, 55.223122);
        Request request3 = new Request(CacheResponseState.HIT, 50.432122, 55.323122);

        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(request3);
        entityManager.flush();

        List<Request> allRequests = requestRepository.findAll();

        assertThat(allRequests).hasSize(3).extracting(Request::getLatitude).containsOnly(request1.getLatitude(),
                request2.getLatitude(), request3.getLatitude());
    }

}