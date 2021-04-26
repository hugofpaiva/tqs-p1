package com.hugopaiva.airqualityservice.exception;

public class APINotResponding extends Exception {
    public APINotResponding(String errorMessage) {
        super(errorMessage);
    }
}
