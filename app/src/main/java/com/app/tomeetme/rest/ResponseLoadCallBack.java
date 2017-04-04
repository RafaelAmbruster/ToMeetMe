package com.app.tomeetme.rest;

import java.util.ArrayList;

/**
 * Created by Ambruster on 16/08/2016.
 */

public interface ResponseLoadCallBack<T> {

    void onResponseLoadCallBack(ArrayList<T> list);

    void onError(String message, Integer code);

}
