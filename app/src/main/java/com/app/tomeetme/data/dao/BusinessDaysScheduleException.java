package com.app.tomeetme.data.dao;


public class BusinessDaysScheduleException extends RuntimeException {

    private String message;

    public BusinessDaysScheduleException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessDaysScheduleException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
