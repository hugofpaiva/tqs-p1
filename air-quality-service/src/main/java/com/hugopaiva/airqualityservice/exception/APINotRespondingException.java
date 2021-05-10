package com.hugopaiva.airqualityservice.exception;

public class APINotRespondingException extends Exception {
    public APINotRespondingException(String errorMessage) {
        super(errorMessage);
    }
}
