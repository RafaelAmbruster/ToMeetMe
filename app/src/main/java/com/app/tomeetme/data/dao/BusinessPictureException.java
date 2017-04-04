package com.app.tomeetme.data.dao;


public class BusinessPictureException extends RuntimeException {

    private String message;

    public BusinessPictureException(String detailMessage) {
        super(detailMessage);
        this.message = detailMessage;
    }

    public BusinessPictureException(Exception exception) {
        super(exception);
        this.setStackTrace(exception.getStackTrace());
        this.message = exception.getMessage();
    }

    @Override
    public String getMessage() {
        return getMessage();
    }
}
