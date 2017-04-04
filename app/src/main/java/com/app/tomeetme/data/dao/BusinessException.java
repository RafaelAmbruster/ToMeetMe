package com.app.tomeetme.data.dao;


public class BusinessException extends RuntimeException {

    private String message;

    public BusinessException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
