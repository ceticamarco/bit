package com.ceticamarco.bits.exception;

import com.ceticamarco.bits.json.JsonEmitter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;
import java.util.HashMap;

@ControllerAdvice
public class CustomExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GenericErrorException.class)
    public ResponseEntity<String> genericErrorException(GenericErrorException ex) {
        var error = new JsonEmitter<>(ex.getMessage()).emitJsonKey(ex.getKey());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();

        ex.getBindingResult().getAllErrors().forEach((e) -> {
            var fieldName = ((FieldError) e).getField();
            var errMessage = e.getDefaultMessage();
            errors.put(fieldName, errMessage);
        });

        return new ResponseEntity<>(
                new JsonEmitter<>(errors).emitJsonKey(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
        return new ResponseEntity<>(
                new JsonEmitter<>("Invalid date format. Use YYYY-MM-DD").emitJsonKey("error"),
                HttpStatus.BAD_REQUEST
        );
    }
}
