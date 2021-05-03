package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.model.Request;
import com.hugopaiva.airqualityservice.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RequestController {

    @Autowired
    RequestService requestService;

    @GetMapping("/requests")
    public ResponseEntity<List<Request>> getRequests(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Request> response = requestService.getRequests(pageNo, pageSize);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/requests-stats")
    public ResponseEntity<Map<String, Long>> getRequestsStats(){
        Map<String, Long> response = requestService.getRequestsStats();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
