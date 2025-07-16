package com.fitech.app.users.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UnitOfMeasurerNotFoundException extends RuntimeException {

    public UnitOfMeasurerNotFoundException(String message) {
        super(message);
    }
}