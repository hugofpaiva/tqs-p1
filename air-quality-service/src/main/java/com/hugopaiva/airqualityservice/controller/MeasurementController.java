package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.exception.APINotResponding;
import com.hugopaiva.airqualityservice.exception.ResourceNotFoundException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.services.MeasurementService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class MeasurementController {

    @Autowired
    MeasurementService measurementService;

    @GetMapping("/measurement")
    public ResponseEntity<Measurement> getMeasurement(@RequestParam Double lat, @RequestParam Double lon) throws ResourceNotFoundException {
        Measurement response = measurementService.getMeasurementByLocation(lat, lon);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}