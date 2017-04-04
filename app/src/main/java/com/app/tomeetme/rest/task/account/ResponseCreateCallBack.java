package com.app.tomeetme.rest.task.account;

/**
 * Created by Ambruster on 16/08/2016.
 */
public interface ResponseCreateCallBack<T> {

    void onResponseCreateCallBack(Boolean correct);

    void onError(String message, Integer code);
}
