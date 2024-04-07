package edu.sjsu.cmpe272.simpleblog.server.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SignatureMismatchException.class)
    public ResponseEntity<Error> handleRuntimeException(SignatureMismatchException ex) {
        return new ResponseEntity<>(new Error("signature didn't match"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<Error> handleRuntimeException(NoUserFoundException ex) {
        return new ResponseEntity<>(new Error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleValidationException(ConstraintViolationException ex) {
        String exception = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        return ResponseEntity.badRequest().body(new Error(exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleValidationException(Exception ex) {
        return ResponseEntity.internalServerError().body(new Error(ex.getMessage()));
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class Error {
        String error;
    }
}
