package com.abercompany.smsforwarding.network;

import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.GetDefaulterResult;
import com.abercompany.smsforwarding.model.GetDepositResult;
import com.abercompany.smsforwarding.model.GetResidentResult;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
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

    @FormUrlEncoded
    @POST("getDeposit")
    Call<GetDepositResult> getDeposit(@Field("phone_num") String phoneNum);

    @FormUrlEncoded
    @POST("getBroker")
    Call<GetBrokerResult> getBroker(@Field("") String empty);

    @FormUrlEncoded
    @POST("getResident")
    Call<GetResidentResult> getResident(@Field("") String empty);

    @FormUrlEncoded
    @POST("updateTrimmedData")
    Call<JsonObject> updateTrimmedData(@Field("name") String name,
                                       @Field("date") String date,
                                       @Field("object_name") String objectName,
                                       @Field("type") String type);

    @FormUrlEncoded
    @POST("getDefaulter")
    Call<GetDefaulterResult> getDefaulter(@Field("") String empty);
}
