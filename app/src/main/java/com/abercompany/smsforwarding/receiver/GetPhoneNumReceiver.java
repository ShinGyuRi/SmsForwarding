package com.abercompany.smsforwarding.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.abercompany.smsforwarding.activity.IncomingCallActivity;
import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.GetBrokerResult;
import com.abercompany.smsforwarding.model.GetResidentResult;
import com.abercompany.smsforwarding.model.Resident;
import com.abercompany.smsforwarding.util.JSLog;
import com.abercompany.smsforwarding.util.NetworkUtil;
import com.abercompany.smsforwarding.util.PrefUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetPhoneNumReceiver extends PhonecallReceiver {

    private List<Resident> residents;
    private List<Broker> brokers;
    private List<String> residentPhoneNum;
    private List<String> residentName;
    private List<String> residentRoomNum;

    private boolean flag = false;

    private final String channelId = "M_CH_ID";

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        JSLog.D("incomingNumber             :::     " + number, null);

        residents = PrefUtil.getInstance().getResidentPreference("resident");
        brokers = PrefUtil.getInstance().getBrokerPreference("broker");

        for (int i = 0; i < residents.size(); i++) {
            if ((number.equals(residents.get(i).getPhoneNum()) || number.equals(residents.get(i).getEtcNum())) && !"".equals(number)) {
                flag = true;
                pushNoti(ctx, residents.get(i).getName() + residents.get(i).getHo(), number);
            } else {
                flag = false;
            }
        }

        if (!flag) {
            for (int i = 0; i < brokers.size(); i++) {
                if (number.equals(brokers.get(i).getPhoneNum())) {
                    pushNoti(ctx, brokers.get(i).getRealtyName(), number);
                }
            }
        }
    }


    private void getResident(final Context context, final String incomingNumber)  {
        Call<GetResidentResult> getResidentResultCall = NetworkUtil.getInstace().getResident("");
        getResidentResultCall.enqueue(new Callback<GetResidentResult>() {
            @Override
            public void onResponse(Call<GetResidentResult> call, Response<GetResidentResult> response) {
                GetResidentResult getResidentResult = response.body();
                String result = getResidentResult.getResult();

                if ("success".equals(result)) {
                    residents = getResidentResult.getResidents();

                    for (int i = 0; i < residents.size(); i++) {
                        if (incomingNumber.equals(residents.get(i).getPhoneNum()) && !"".equals(incomingNumber)) {
                            flag = true;
//                            pushNoti(context, residents.get(i).getName() + residents.get(i).getHo());
                        } else {
                            flag = false;
                        }
                    }
                    if (!flag) {
                        getBroker(context, incomingNumber);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetResidentResult> call, Throwable t) {

            }
        });
    }

    private void getBroker(final Context context, final String incomingNumber) {
        Call<GetBrokerResult> getBrokerResultCall = NetworkUtil.getInstace().getBroker("");
        getBrokerResultCall.enqueue(new Callback<GetBrokerResult>() {
            @Override
            public void onResponse(Call<GetBrokerResult> call, Response<GetBrokerResult> response) {
                GetBrokerResult getBrokerResult = response.body();
                String result = getBrokerResult.getResult();

                if ("success".equals(result)) {
                    brokers = getBrokerResult.getBrokers();

                    for (int i = 0; i < brokers.size(); i++) {
                        if (incomingNumber.equals(brokers.get(i).getPhoneNum()) && !"".equals(incomingNumber)) {
//                            pushNoti(context, brokers.get(i).getRealtyName() + "(" + brokers.get(i).getName() + ")");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GetBrokerResult> call, Throwable t) {

            }
        });
    }

    private void pushNoti(Context context, String content, String number) {
        JSLog.D("push noti              !!!!        ", null);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(android.support.v4.R.drawable.notification_icon_background)
                        .setContentTitle("전화가 왔습니다")
                        .setContentText(content)
                        .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX);

        Intent intent = new Intent(context, IncomingCallActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("phoneNum", number);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pi);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, builder.build());
    }
}
