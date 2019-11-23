package com.fadi.forestautoget.util;

import com.fadi.forestautoget.MainActivity;

public class UtilsLog {
    public static boolean isTest = true;
    public static boolean saveToFile = true;
    private static SaveLogToFile mSaveLocMessage = null;

    public static void saveLogToFile(String key, String value) {

        if(!MainActivity.SDCARD_PERSIMITION){
            return;
        }
        if (saveToFile) {
            if (mSaveLocMessage == null) {
                mSaveLocMessage = new SaveLogToFile();
            }

            String time = TimeUtils.changeSnapToString(
                    "" + System.currentTimeMillis() / 1000,
                    "yyyy-MM-dd HH:mm:ss");

            mSaveLocMessage
                    .saveMessage(time + ": " + key + ": " + value + "\n");
        }
    }

    public static void d(String key, String value) {
        if (isTest) {
            android.util.Log.d(key, value);
            saveLogToFile(key, value);
        }
    }

    public static void i(String key, String value) {
        if (isTest) {
            android.util.Log.i(key, value);
            saveLogToFile(key, value);
        }
    }

    public static void e(String key, String value) {
        if (isTest) {
            android.util.Log.e(key, value);
            saveLogToFile(key, value);
        }
    }
}
