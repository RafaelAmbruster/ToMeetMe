package com.app.tomeetme.rest.task.account;

import com.app.tomeetme.model.UserLoginResponse;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ServiceGenerator;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginTask {

    public ResponseLoginCallBack callBack;

    public LoginTask(ResponseLoginCallBack callBack) {
        this.callBack = callBack;
    }

    public void CallService(String username, String password) {

        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);

        Map<String, String> params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);

        Call<UserLoginResponse> call = apiService.Login(params);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBack.onError(response.message(), response.code());
                            break;

                        case 200:
                            UserLoginResponse resp = response.body();
                            callBack.onResponseLoginCallBack(resp);
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
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                callBack.onError(t.toString(), 500);
            }
        });
    }
}
