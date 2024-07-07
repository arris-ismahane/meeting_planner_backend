package com.sme.meeting_planner.exceptions;

import lombok.Getter;

public class ExceptionBase extends RuntimeException {

    @Getter
    private final String title;

    @Getter
    private final int httpStatus;

    public ExceptionBase(String title, int httpStatus) {
        this(title, "", null, httpStatus);
    }

    public ExceptionBase(String title, String message, int httpStatus) {
        this(title, message, null, httpStatus);
    }

    public ExceptionBase(String title, String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.title = title;
        this.httpStatus = httpStatus;
    }

}
