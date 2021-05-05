package com.hugopaiva.airqualityservice.controller;

import com.hugopaiva.airqualityservice.exception.BadRequestException;
import com.hugopaiva.airqualityservice.exception.ServiceUnavailableException;
import com.hugopaiva.airqualityservice.model.Measurement;
import com.hugopaiva.airqualityservice.services.MeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeasurementController {

    @Autowired
    MeasurementService measurementService;

    @GetMapping("/actual-measurement")
    public ResponseEntity<Measurement> getActualMeasurement(@RequestParam Double lat, @RequestParam Double lon) throws ServiceUnavailableException, BadRequestException {
        if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0){
            throw new BadRequestException("Invalid parameters");
        }

        Measurement response = measurementService.getActualMeasurementByLocation(lat, lon);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
