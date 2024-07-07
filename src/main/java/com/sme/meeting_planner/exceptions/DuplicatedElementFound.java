package com.sme.meeting_planner.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicatedElementFound extends ExceptionBase {
    public DuplicatedElementFound(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST.value());
    }
}
