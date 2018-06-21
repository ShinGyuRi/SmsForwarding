package com.abercompany.smsforwarding.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;

public class BaseApplication extends Application{

    public static BaseApplication mContext;
    private static volatile BaseApplication instance = null;
    private static volatile Activity currentActivity = null;


    public BaseApplication() {
        super();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        BaseApplication.currentActivity = currentActivity;
    }

    public static int getDebugMode() {
        return mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE;
    }

    public static synchronized BaseApplication getInstance()    {   return instance;    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setHashKey();

        patchEOFException();

    }

    private void patchEOFException() {
        System.setProperty("http.keepAlive", "false");
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static BaseApplication getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }


    public void setHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                hashKey = hashKey.trim();
//                Debug.showDebug("hash key:::" + hashKey);
            }
        } catch (Exception e) {
        }
    }
}
