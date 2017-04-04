package com.app.tomeetme.rest.task.account;

import com.app.tomeetme.model.UserLoginResponse;

/**
 * Created by Ambruster on 16/08/2016.
 */
public interface ResponseLoginCallBack {
    void onResponseLoginCallBack(UserLoginResponse response);

    void onError(String message, Integer code);
}
