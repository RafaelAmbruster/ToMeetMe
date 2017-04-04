package com.app.tomeetme.rest;

import android.util.Log;

import com.app.tomeetme.ToMeetMeApplication;
import com.app.tomeetme.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ambruster on 16/08/2016.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "https://bigappwebapi.azurewebsites.net/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, ToMeetMeApplication.getContext().getString(R.string.api_key));
    }

    public static <S> S createService(Class<S> serviceClass, final String password) {

        final String credential = Credentials.basic("", password);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", credential)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();

                Response response = chain.proceed(request);

                int tryCount = 0;
                while (!response.isSuccessful() && tryCount < 10) {
                    Log.d("intercept", "Request is not successful - " + tryCount);
                    tryCount++;
                    response = chain.proceed(request);
                }

                //return chain.proceed(request);
                return response;
            }
        });

        httpClient.retryOnConnectionFailure(true);
        httpClient.connectTimeout(120, TimeUnit.SECONDS);
        httpClient.readTimeout(120, TimeUnit.SECONDS);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
