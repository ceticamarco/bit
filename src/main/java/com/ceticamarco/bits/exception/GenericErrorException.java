package com.ceticamarco.bits.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class GenericErrorException extends RuntimeException {
    private final String key;

    public GenericErrorException(String message, String key) {
        super(message);
        this.key = key;
    }

    public GenericErrorException() {
        super("Bad Request");
        this.key = "error";
    }

    public String getKey() { return key; }
}
