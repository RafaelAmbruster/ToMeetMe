package com.app.tomeetme.rest;

import com.app.tomeetme.model.Business;
import com.app.tomeetme.model.CreateResponse;
import com.app.tomeetme.model.UserLoginResponse;
import com.app.tomeetme.model.UserRegistrationResponse;
import com.app.tomeetme.model.User;
import com.app.tomeetme.model.BusinessFilter;
import com.app.tomeetme.model.Response;
import com.app.tomeetme.model.Country;
import com.app.tomeetme.model.BusinessFavorites;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {

    /**
     * Login
     * @return
     */
    @FormUrlEncoded
    @POST("api/CustomLogin")
    Call<UserLoginResponse> Login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("tables/ApplicationUser/register")
    Call<UserRegistrationResponse> Register(@FieldMap Map<String, String> params);

    @GET("tables/ApplicationUser/{id}?$expand=Businesses,LocalEvents,Notifications")
    Call<User> getAppUserInformation(@Path("id") String userId);

    /**
     * Favorites
     * @return
     */
    @GET("tables/UserFavoriteBusiness")
    Call<List<BusinessFavorites>> getBusinessFavorites(@Query("userId") String userId);

    @FormUrlEncoded
    @POST("tables/UserFavoriteBusiness")
    Call<ResponseBody> addBusinessFavorite(@FieldMap Map<String, String> params);

    @DELETE("tables/UserFavoriteBusiness/{id}")
    Call<ResponseBody> deleteBusinessFavorites(@Path("id") String id);

    /**
     * Business
     * @return
     */
    /*1*/
    @POST("tables/Business/filter?$expand=BusinessCategory,businessPictures")
    Call<List<Business>> getBusiness(@Body BusinessFilter filter);

    /*2*/
    @GET("tables/Business/search?criteria={criteria}")
    Call<List<Business>> getBusinessCriteria(@Path("criteria") String criteria);

    /*3*/
    @GET("tables/Business/{id}?$expand=businessPictures,Reviews,Videos,Coupons,BusinessCategory")
    Call<Business> getBusinessDetail(@Path("id") String id);

    /*4*/
    @GET("tables/Business/search?")
    Observable<List<Business>> autoSuggestBusiness(@Query("criteria") String criteria);

    /*5*/
    @FormUrlEncoded
    @POST("tables/Business")
    Call<Response> createBusiness(@FieldMap Map<String, String> params);

    /*6*/
    @FormUrlEncoded
    @POST("tables/BusinessPicture")
    Observable<CreateResponse> createBusinessPicture(@FieldMap Map<String, String> params);

    /*3*/
    @GET("tables/Business/{id}?$expand=businessPictures,Reviews,Videos,Coupons,BusinessCategory")
    Observable<Business> getBusinessFavorite(@Path("id") String id);

    @FormUrlEncoded
    @POST("tables/Review")
    Call<CreateResponse> createBusinessReview(@FieldMap Map<String, String> params);

    /**
     * Country
     * @return
     */
    /*1*/
    @GET("tables/Country")
    Call<List<Country>> getCountries();

    /*2*/
    @GET("tables/Country/{id}?$expand=State")
    Call<Country> getCountryDetail(@Path("id") String id);

}
