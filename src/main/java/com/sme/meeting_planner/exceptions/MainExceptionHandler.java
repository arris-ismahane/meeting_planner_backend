package com.sme.meeting_planner.exceptions;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@RequiredArgsConstructor
@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(ExceptionBase.class)
    public ResponseEntity<Object> handleExceptionBase(ExceptionBase e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("title", e.getTitle(), "message", e.getMessage()));
    }
}
