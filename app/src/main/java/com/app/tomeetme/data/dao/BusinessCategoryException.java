package com.app.tomeetme.data.dao;


public class BusinessCategoryException extends RuntimeException {

    private String message;

    public BusinessCategoryException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessCategoryException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
