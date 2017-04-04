package com.app.tomeetme.rest.task.business;

import com.app.tomeetme.helper.util.CustomDateFormat;
import com.app.tomeetme.model.BusinessReview;
import com.app.tomeetme.model.CreateResponse;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessReviewTask {

    public ResponseLoadCallBack<BusinessReview> callBackList;
    public ResponseObjectCallBack<CreateResponse> callBack;

    public BusinessReviewTask(ResponseLoadCallBack callBackList) {
        this.callBackList = callBackList;
    }

    public BusinessReviewTask(ResponseObjectCallBack callBack) {
        this.callBack = callBack;
    }

    public void CallService(int operation, BusinessReview businessReview) {

        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);

        switch (operation) {
            case 1:
                Create(apiService, businessReview);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    public void Create(ApiInterface apiService, BusinessReview businessReview) {

        Map<String, String> params;
        params = new HashMap<>();

        params.put("comment", businessReview.getComment());
        params.put("stars", String.valueOf(businessReview.getStars()));
        params.put("businessId", businessReview.getBusinessId());
        params.put("email", businessReview.getEmail());
        params.put("firstName", businessReview.getFirstName());
        params.put("lastName", businessReview.getLastName());
        params.put("date", CustomDateFormat.getCurrentDateTime(new Date()));

        Call<CreateResponse> call = apiService.createBusinessReview(params);
        call.enqueue(new Callback<CreateResponse>() {
            @Override
            public void onResponse(Call<CreateResponse> call, Response<CreateResponse> response) {
                try {
                    switch (response.code()) {
                        case 401:
                            callBack.onError(response.message(), response.code());
                            break;

                        case 201:
                            CreateResponse resp = response.body();
                            callBack.onResponseObjectCallBack(resp);
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
            public void onFailure(Call<CreateResponse> call, Throwable t) {
                callBackList.onError(t.toString(), 500);
            }
        });
    }
}
