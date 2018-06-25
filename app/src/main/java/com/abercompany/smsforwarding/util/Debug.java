package com.abercompany.smsforwarding.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Debug {
    public static boolean IS_DEBUG_LOG = true;
    public static boolean IS_TOAST_LOG = true;
    public static String DEBUG_TAG = "DEBUG";

    public static void showDebug(String msg) {
        if (TextUtil.isNull(msg))
            return;
        if (IS_DEBUG_LOG) {
            Log.d(DEBUG_TAG, msg);
        }
        return;
    }

    public static void showToast(Context context, int msg) {
        if (IS_TOAST_LOG) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }
    public static void showToast(Context context, String msg) {
        if (IS_TOAST_LOG) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static Toast toast;
    /**
     *
     * @param mCon
     * @param mMsg
     */
//	public static void customPushToast(Context context, String pushMsg,String pushMsg2) {
//
//		if (toast == null) toast = new Toast(context);
//
//		View view = View.inflate(context, R.layout.push_toast, null);
//
//		ImageView image = (ImageView) view.findViewById(R.id.image);
//		image.setImageResource(R.drawable.ic_launcher);
//		TextView text = (TextView) view.findViewById(R.id.text);
//		text.setText(pushMsg);
//		text.setGravity(Gravity.CENTER|Gravity.LEFT);
//
//		TextView text2 = (TextView) view.findViewById(R.id.text2);
//		text2.setText(pushMsg2+":");
//		text2.setGravity(Gravity.CENTER|Gravity.LEFT);
//
//
//		toast.setGravity(Gravity.TOP, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
//		toast.setView(view);
//		toast.show();
//	}
}
