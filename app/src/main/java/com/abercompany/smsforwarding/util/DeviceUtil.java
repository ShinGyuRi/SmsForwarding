package com.abercompany.smsforwarding.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtil {

    public static String TAG = DeviceUtil.class.getSimpleName();

    public static String getDevicePhoneNumber(Context context) {
        TelephonyManager telManager = null;
        String mPhoneNum = null;
        try {
            telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNum = telManager.getLine1Number();
//			mPhoneNum = PhoneNumberUtils.formatNumber(mPhoneNum);
            mPhoneNum = mPhoneNum.replace("+820", "0");
            mPhoneNum = mPhoneNum.replace("+82", "0");
            if (mPhoneNum.subSequence(0, 2).equals("01") == false) {
                mPhoneNum = "";
            }

        } catch (Exception err) {
            mPhoneNum = null;
            Log.e(TAG, err.toString());
        } finally {
            if (telManager != null)
                telManager = null;
        }

        return mPhoneNum;
    }
}
