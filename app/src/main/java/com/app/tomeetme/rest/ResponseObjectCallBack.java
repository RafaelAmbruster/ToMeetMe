package com.app.tomeetme.rest;

/**
 * Created by Ambruster on 16/08/2016.
 */

public interface ResponseObjectCallBack<T> {

    void onResponseObjectCallBack(T object);

    void onError(String message, Integer code);

}
