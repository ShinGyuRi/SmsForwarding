package com.abercompany.smsforwarding.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;

public class SmsLib {

    private static final String TAG = SmsLib.class.getSimpleName();

    private static ConnectivityManager cm;
    private static NetworkRequest.Builder req;


    private static SmsLib sharedSmsLib;
    private SharedPreferences.Editor sharedPrefEd;
    private SharedPreferences sharedPref;


    public static  SmsLib getInstance() {
        if (sharedSmsLib == null) {
            sharedSmsLib = new SmsLib();
        }

        return sharedSmsLib;
    }

    public void showSimpleDialog(Context context, String message, String buttonMessage, DialogInterface.OnClickListener btnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setNegativeButton(buttonMessage,btnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public void showSimplSelect2Dialog(Context context, String message, String yesBtn, String noBtn, DialogInterface.OnClickListener yesBtnListener, DialogInterface.OnClickListener noBtnListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(yesBtn,yesBtnListener);
        alertBuilder.setNegativeButton(noBtn,noBtnListener);
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }
}
