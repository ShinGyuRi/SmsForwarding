package com.abercompany.smsforwarding.network;

import com.abercompany.smsforwarding.model.Deposit;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.GetContractResult;
import com.abercompany.smsforwarding.model.GetDefaulterResult;
import com.abercompany.smsforwarding.model.GetDepositResult;
import com.abercompany.smsforwarding.model.GetRealtyResult;
import com.abercompany.smsforwarding.model.GetResidentResult;
import com.abercompany.smsforwarding.model.GetRoomResult;
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
                                       @Field("type") String type,
                                       @Field("phone_num") String phoneNum);

    @FormUrlEncoded
    @POST("getDefaulter")
    Call<GetDefaulterResult> getDefaulter(@Field("") String empty);

    @FormUrlEncoded
    @POST("getRoom")
    Call<GetRoomResult> getRoom(@Field("") String empty);

    @FormUrlEncoded
    @POST("getContract")
    Call<GetContractResult> getContract(@Field("") String empty);

    @FormUrlEncoded
    @POST("insertContract")
    Call<JsonObject> insertContract(@Field("room_num") String roomNum,
                                    @Field("name") String name,
                                    @Field("phone_num") String phoneNum,
                                    @Field("id_num") String idNum,
                                    @Field("etc_num") String etcNum,
                                    @Field("address") String address,
                                    @Field("emer_num") String emerNum,
                                    @Field("emer_name") String emerName,
                                    @Field("down_payment") String downPayment,
                                    @Field("deposit") String deposit,
                                    @Field("rent") String rent,
                                    @Field("manage_fee") String manageFee,
                                    @Field("start_date") String startDate,
                                    @Field("end_date") String endDate,
                                    @Field("elec_num") String elecNum,
                                    @Field("gas_num") String gasNum,
                                    @Field("active") String active,
                                    @Field("realty_name") String realtyName,
                                    @Field("realty_account") String realtyAccount,
                                    @Field("realty_broker_name") String realtyBrokerName,
                                    @Field("realty_broker_phone_num") String realtyBrokerPhoneNum);

    @FormUrlEncoded
    @POST("getRealty")
    Call<GetRealtyResult> getRealty(@Field("") String empty);
}
