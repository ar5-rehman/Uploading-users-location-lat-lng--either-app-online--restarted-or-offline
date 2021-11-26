package com.example.offlineapp;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AppWebServices {
    String URL = "https://nsolucion.com/ntask-api/";

    Retrofit web = new Retrofit.Builder().
            baseUrl(URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build();

    @FormUrlEncoded
    @POST("public/phone-login")
    Call<LoginPojo> loginUser(
            @Field("user_name") String userName,
            @Field("password") String password,
            @Field("imei") String imei);

    @FormUrlEncoded
    @POST("private/event")
    Call<EventPojo> event(
            @Field("user_id") String id,
            @Field("description") String desc,
            @Field("longitude") String lng,
            @Field("latitude") String lat,
            @Field("created_by") String created,
            @Header("Authorization") String token);
}
