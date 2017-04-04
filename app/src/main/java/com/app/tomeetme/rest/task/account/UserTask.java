package com.app.tomeetme.rest.task.account;

import com.app.tomeetme.model.User;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTask {

    public ResponseLoadCallBack<User> callBackList;
    public ResponseObjectCallBack<User> callBack;

    public UserTask(ResponseLoadCallBack callBackList) {
        this.callBackList = callBackList;
    }

    public UserTask(ResponseObjectCallBack callBack) {
        this.callBack = callBack;
    }

    public void CallService(int operation, String id) {

        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);

        switch (operation) {
            case 1:
                break;
            case 2:
                LoadById(apiService, id);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    public void LoadById(ApiInterface apiService, String appUserId) {

        Call<User> call = apiService.getAppUserInformation(appUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBack.onError(response.message(), response.code());
                            break;

                        case 200:
                            User addresses = response.body();
                            callBack.onResponseObjectCallBack(addresses);
                            break;

                        case 500:
                            callBack.onError(response.message(), response.code());
                            break;

                        default:
                            callBack.onError(response.message(), response.code());
                            break;
                    }
                } catch (Exception ex) {
                    callBack.onError(ex.toString(), response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callBack.onError(t.toString(), 500);
            }
        });
    }
}
