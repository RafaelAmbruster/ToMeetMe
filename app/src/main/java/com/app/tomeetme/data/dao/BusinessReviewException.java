package com.app.tomeetme.data.dao;


public class BusinessReviewException extends RuntimeException {

    private String message;

    public BusinessReviewException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessReviewException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
