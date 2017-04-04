package com.app.tomeetme.data.dao;


public class BusinessAddressException extends RuntimeException {

    private String message;

    public BusinessAddressException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessAddressException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
