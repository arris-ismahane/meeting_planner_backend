package com.sme.meeting_planner.exceptions;

import org.springframework.http.HttpStatus;

public class ElementNotFound extends ExceptionBase {
    public ElementNotFound(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST.value());
    }
}
