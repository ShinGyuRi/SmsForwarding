package com.abercompany.smsforwarding.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class JSLog {

    public static String TAG = JSLog.class.getSimpleName();

    public static final int MODE_DEBUG = 2;
    public static final int MODE_RELEASE = 0;

    public static void D(Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.d(TAG, ">>> " + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void D(String msg, Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.d(TAG, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void D(String tag, String msg, Throwable currentThrowable) {
        if (TextUtils.isEmpty(tag))		tag = TAG;
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.d(tag, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void longLog(String message) {
        int maxLogSize = 2000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d("JYLog", message.substring(start, end));
        }
    }


    public static void I( Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.i(TAG, ">>> " + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void I( String msg, Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.i(TAG, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void I(String tag, String msg, Throwable currentThrowable) {
        if (TextUtils.isEmpty(tag))		tag = TAG;
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.i(tag, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void E(Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.e(TAG, ">>> " + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void E(String msg, Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.e(TAG, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void E(String tag, String msg, Throwable currentThrowable) {
        if (TextUtils.isEmpty(tag))		tag = TAG;
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.e(tag, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void W(Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.w(TAG, ">>> " + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void W( String msg, Throwable currentThrowable) {
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.w(TAG, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    public static void W(String tag, String msg, Throwable currentThrowable) {
        if (TextUtils.isEmpty(tag))		tag = TAG;
        if (BaseApplication.getDebugMode() == MODE_DEBUG)
            Log.w(tag, ">>> " + msg + getFileNameAndLineNumber(currentThrowable));
        currentThrowable = null;
    }

    private static String getFileNameAndLineNumber(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        String fileName = throwable.getStackTrace()[0].getFileName();
        String className = throwable.getStackTrace()[0].getClassName();
        String methodName = throwable.getStackTrace()[0].getMethodName();
        int lineNumber = throwable.getStackTrace()[0].getLineNumber();
        // return "at " + className + "." + methodName + "(" + fileName + ":" + lineNumber + ")";
        throwable = null;
        return "> at " + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    private static String getDateTime() {
        Calendar cal = Calendar.getInstance();

        String dateToString, timeToString;

        dateToString = String.format(" %04d-%02d-%02d ",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));

        timeToString = String.format("%02d:%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));

        return dateToString + timeToString;
    }

	private static void writeLogFile(String logMsg, String fileName) {
		String logFilePath;
		if (fileName != null) {
			logFilePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/JYApp/Log/" + fileName + ".txt";
		} else {
			logFilePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/JYApp/Log/log.txt";
		}

		File file = new File(logFilePath);
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.d("jy", "exception : " + e.toString());
			}
		} else {
			try {
				BufferedWriter bfw = new BufferedWriter(new FileWriter(
						logFilePath, true));
				bfw.write(logMsg);
				bfw.write("\n");
				bfw.flush();
				bfw.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}
}
