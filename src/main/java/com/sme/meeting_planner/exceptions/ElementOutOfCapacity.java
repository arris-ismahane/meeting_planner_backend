package com.sme.meeting_planner.exceptions;

import org.springframework.http.HttpStatus;

public class ElementOutOfCapacity extends ExceptionBase {
    public ElementOutOfCapacity(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST.value());
    }

}
