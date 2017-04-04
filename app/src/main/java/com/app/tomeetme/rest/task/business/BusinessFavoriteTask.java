package com.app.tomeetme.rest.task.business;

import com.app.tomeetme.model.BusinessFavorites;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessFavoriteTask {

    public ResponseLoadCallBack<BusinessFavorites> callBackList;

    public ResponseObjectCallBack<Object> callBack;

    public BusinessFavoriteTask(ResponseLoadCallBack callBackList) {
        this.callBackList = callBackList;
    }

    public BusinessFavoriteTask(ResponseObjectCallBack callBack) {
        this.callBack = callBack;
    }

    public void CallService(int operation, String userId, String businessId) {

        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);

        switch (operation) {
            case 1:
                Load(apiService, userId);
                break;
            case 2:
                Create(apiService, userId, businessId);
                break;
            case 3:
                Delete(apiService, businessId);
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    public void Load(ApiInterface apiService, String userId) {

        Call<List<BusinessFavorites>> call = apiService.getBusinessFavorites(userId);
        call.enqueue(new Callback<List<BusinessFavorites>>() {
            @Override
            public void onResponse(Call<List<BusinessFavorites>> call, Response<List<BusinessFavorites>> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBackList.onError(response.message(), response.code());
                            break;

                        case 200:
                            List<BusinessFavorites> events = response.body();
                            callBackList.onResponseLoadCallBack((ArrayList<BusinessFavorites>) events);
                            break;

                        case 500:
                            callBackList.onError(response.message(), response.code());
                            break;

                        default:
                            callBack.onError(response.message(), response.code());
                            break;
                    }
                } catch (Exception ex) {
                    callBackList.onError(ex.toString(), response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BusinessFavorites>> call, Throwable t) {
                callBackList.onError(t.toString(), 500);
            }
        });
    }

    public void Create(ApiInterface apiService, String userId, String businessId) {

        Map<String, String> params = new HashMap<>();
        params.put("applicationUserId", userId);
        params.put("businessId", businessId);

        Call<ResponseBody> call = apiService.addBusinessFavorite(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBack.onError(response.message(), response.code());
                            break;

                        case 201:
                            callBack.onResponseObjectCallBack(response);
                            break;

                        case 500:
                            callBack.onError(response.message(), response.code());
                            break;

                        default:
                            callBack.onError(response.message(), response.code());
                            break;
                    }
                } catch (Exception ex) {
                    callBackList.onError(ex.toString(), response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBackList.onError(t.toString(), 500);
            }
        });
    }

    private void Delete(ApiInterface apiService, String Id) {
        Call<ResponseBody> call = apiService.deleteBusinessFavorites(Id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBack.onError(response.message(), response.code());
                            break;

                        case 200:
                            callBack.onResponseObjectCallBack(response);
                            break;

                        case 500:
                            callBack.onError(response.message(), response.code());
                            break;

                        default:
                            callBack.onError(response.message(), response.code());
                            break;
                    }
                } catch (Exception ex) {
                    callBackList.onError(ex.toString(), response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBackList.onError(t.toString(), 500);
            }
        });
    }

}
