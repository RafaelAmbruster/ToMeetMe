package com.app.tomeetme.rest.task.business;

import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.BusinessPictures;
import com.app.tomeetme.model.CreateResponse;
import com.app.tomeetme.rest.ApiInterface;
import com.app.tomeetme.rest.ResponseLoadCallBack;
import com.app.tomeetme.rest.ResponseObjectCallBack;
import com.app.tomeetme.rest.ServiceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BusinessPictureTask {

    public ResponseLoadCallBack<Business> callBackList;

    public ResponseObjectCallBack<Object> callBack;

    public BusinessPictureTask(ResponseLoadCallBack callBackList) {
        this.callBackList = callBackList;
    }

    public BusinessPictureTask() {
    }

    public void CallService(int operation, ArrayList<BusinessPictures> pictures) {

        switch (operation) {
            case 1:
                Create(pictures);
                break;
        }
    }

    private void Create(ArrayList<BusinessPictures> pictures) {
        ApiInterface apiService =
                ServiceGenerator.createService(ApiInterface.class);
        Map<String, String> params;
        for (BusinessPictures item : pictures) {

            params = new HashMap<>();
            params.put("businessId", item.getBusinessId());
            params.put("imageBase64", item.getImageBase64());
            params.put("imageFileName", item.getImageFileName());

            apiService.createBusinessPicture(params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CreateResponse>() {
                        @Override
                        public final void onCompleted() {
                            LogManager.getInstance().info("Completed", "true");
                        }

                        @Override
                        public final void onError(Throwable e) {
                            LogManager.getInstance().error("Error", "true");
                        }

                        @Override
                        public final void onNext(CreateResponse response) {
                        }
                    });
        }
        LogManager.getInstance().info("All work Completed", "true");
    }
}
