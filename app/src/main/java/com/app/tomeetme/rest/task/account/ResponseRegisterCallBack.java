package com.app.tomeetme.rest.task.account;

import com.app.tomeetme.model.UserRegistrationResponse;

/**
 * Created by Ambruster on 16/08/2016.
 */
public interface ResponseRegisterCallBack {
    void onResponseRegisterCallBack(UserRegistrationResponse response);

    void onError(String message, Integer code);
}
