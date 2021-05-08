package com.hugopaiva.airqualityservice.services;

import com.hugopaiva.airqualityservice.model.CacheResponseState;
import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {

    @Autowired
    RequestRepository requestRepository;

    public List<Request> getRequests(Integer pageNo, Integer pageSize)
    {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("date").descending());

        Page<Request> pagedResult = requestRepository.findAll(paging);

        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    public Map<String, Long> getRequestsStats() {
        Map<String, Long> response = new HashMap<>();
        response.put("nRequests", requestRepository.count());
        response.put("hits", requestRepository.countByCacheResponse(CacheResponseState.HIT));
        response.put("misses", requestRepository.countByCacheResponse(CacheResponseState.MISS));

        return response;
    }
}
