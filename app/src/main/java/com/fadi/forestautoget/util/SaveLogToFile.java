package com.fadi.forestautoget.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveLogToFile {
    String TAG = "SaveLocationMessage";
    String fileName = "test.log";
    String path = "";
    FileOutputStream fos;
    private boolean shouldReturn = false;
    private BufferedWriter bufferWrite = null;
    private final String PATH = "/sdcard/forest/";

    private String getFileName() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(new Date());
        time += ".log";
        return time;
    }

    private void CreateWriteStream() {
        String path = PATH;//
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory() + "/forest/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(path + fileName);
            try {
                bufferWrite = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
            } catch (Exception e) {
                e.printStackTrace();
                shouldReturn = true;
            }
        } else {
            Log.e(TAG, "do not have sdcard ...");
            shouldReturn = true;
            //UtilsLog.i(TAG,"do not have sdcard ...");
        }
    }


    public SaveLogToFile() {
        fileName = getFileName();
        CreateWriteStream();
    }

    public void saveMessage(String mes) {

        if (shouldReturn) {
            return;
        }
        try {
            if (fileName.equals(getFileName())) {
                bufferWrite.write(mes);
                bufferWrite.flush();
            } else {
                bufferWrite.close();
                fileName = getFileName();
                CreateWriteStream();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "an error occured while writing file...", e);
        }

    }
}
