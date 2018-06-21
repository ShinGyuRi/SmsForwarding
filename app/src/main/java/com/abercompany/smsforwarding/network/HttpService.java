package com.abercompany.smsforwarding.network;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HttpService {

    @FormUrlEncoded
    @POST("insertSms")
    Call<JsonObject> insertSms(@Field("message") String message,
                               @Field("phone_num") String phoneNum,
                               @Field("sender_num") String senderNum,
                               @Field("timestamp") String timeStamp);

    @FormUrlEncoded
    @POST("registerNum")
    Call<JsonObject> registerNum(@Field("sender_num") String senderNum,
                                 @Field("phone_num") String phoneNum);

    @FormUrlEncoded
    @POST("getNum")
    Call<JsonObject> getNum(@Field("sender_num") String senderNum);

    @FormUrlEncoded
    @POST("deleteNum")
    Call<JsonObject> deleteNum(@Field("sender_num") String senderNum);
}
