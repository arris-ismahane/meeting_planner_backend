package com.sme.meeting_planner.exceptions;

import org.springframework.http.HttpStatus;

public class NoRoomAvailableException extends ExceptionBase {
    public NoRoomAvailableException(String title, String message) {
        super(title, message, HttpStatus.BAD_REQUEST.value());
    }
}