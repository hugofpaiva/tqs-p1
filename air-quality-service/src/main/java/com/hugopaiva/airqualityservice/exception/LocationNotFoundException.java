package com.hugopaiva.airqualityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LocationNotFoundException extends Exception{

    private static final long serialVersionUID = 1L;

    public LocationNotFoundException(String message){
        super(message);
    }
}
