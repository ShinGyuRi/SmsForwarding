package com.abercompany.smsforwarding.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.abercompany.smsforwarding.util.BaseApplication;
import com.abercompany.smsforwarding.util.DeviceUtil;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.network.QuestionsSpreadsheetWebService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsReceiver extends BroadcastReceiver {
    static final String logTag = "SmsReceiver";
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private QuestionsSpreadsheetWebService spreadsheetWebService;

    private List<String> nums = new ArrayList<String>();


    @Override

    public void onReceive(Context context, Intent intent) {
        Log.d(logTag, "onReceive            !!!     ");
        if (intent.getAction().equals(ACTION)) {
            //Bundel 널 체크
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            StringBuilder sms = new StringBuilder();
            StringBuilder address = new StringBuilder();
            StringBuilder time = new StringBuilder();

            //pdu 객체 널 체크
            Object[] pdusObj = (Object[]) bundle.get("pdus");
            if (pdusObj == null) {
                return;
            }

            //message 처리
            SmsMessage[] smsMessages = new SmsMessage[pdusObj.length];
            for (int i = 0; i < pdusObj.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                Log.e(logTag, "NEW SMS " + i + "th");
                Log.e(logTag, "DisplayOriginatingAddress : "
                        + smsMessages[i].getDisplayOriginatingAddress());
                Log.e(logTag, "DisplayMessageBody : "
                        + smsMessages[i].getDisplayMessageBody());
                Log.e(logTag, "EmailBody : "
                        + smsMessages[i].getEmailBody());
                Log.e(logTag, "EmailFrom : "
                        + smsMessages[i].getEmailFrom());
                Log.e(logTag, "OriginatingAddress : "
                        + smsMessages[i].getOriginatingAddress());
                Log.e(logTag, "MessageBody : "
                        + smsMessages[i].getMessageBody());
                Log.e(logTag, "ServiceCenterAddress : "
                        + smsMessages[i].getServiceCenterAddress());
                Log.e(logTag, "TimestampMillis : "
                        + smsMessages[i].getTimestampMillis());

                Toast.makeText(context, smsMessages[i].getMessageBody(), Toast.LENGTH_SHORT).show();
            }

            for (SmsMessage smsMessage : smsMessages) {
                address.setLength(0);
                time.setLength(0);
                sms.append(smsMessage.getMessageBody());
                address.append(smsMessage.getOriginatingAddress());
                time.append(smsMessage.getTimestampMillis());
            }

            nums.clear();
            getNum(sms.toString(), address.toString(), time.toString());


        }
    }

    private void uploadData(String message, String senderNum, String timeStamp) {

        String phoneNum = DeviceUtil.getDevicePhoneNumber(BaseApplication.getContext());

        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().insertSms(message, phoneNum, senderNum, timeStamp);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();

                String result = null;
                result = jsonObject.get("result").getAsString();
                if ("success".equals(result)) {
                    boolean success = jsonObject.get("message").getAsBoolean();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getNum(final String messageBody, final String senderNum, final String timeStamp) {
        Call<JsonObject> jsonObjectCall = NetworkUtil.getInstace().getNum(senderNum);
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                String result = jsonObject.get("result").getAsString();

                if ("success".equals(result)) {
                    Log.d(logTag, "message      ::      " + jsonObject.get("message").getAsJsonArray().toString());

                    JsonArray message = jsonObject.get("message").getAsJsonArray();
                    JsonObject messageObj;
                    for (int i=0; i<message.size(); i++) {
                        messageObj = message.get(i).getAsJsonObject();
                        if (DeviceUtil.getDevicePhoneNumber(BaseApplication.getContext()).equals(messageObj.get("phone_num").getAsString())) {
                            nums.add(messageObj.get("sender_num").getAsString());
                        }
                    }


                    for (int i = 0; i < nums.size(); i++) {
                        Log.d(logTag, "address      ::      " + senderNum);
                        Log.d(logTag, "num.get      ::      " + nums.get(i));

                        if (senderNum.equals(nums.get(i))) {
                            uploadData(messageBody, senderNum, timeStamp);

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
