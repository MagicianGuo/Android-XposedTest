package com.magicianguo.xposedjumpappinterceptor;

import android.app.Application;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class LogUtil {
    private static Application mApp;
    private static final boolean mLogEnable = false;

    public static void init(Application application) {
        mApp = application;
    }

    public static void writeLog(String text) {
        if (!mLogEnable) {
            return;
        }
        try {
            String name = "log.txt";
            String path = mApp.getExternalFilesDir(null).getPath();
            File logFile = new File(path, name);
            if (logFile.exists()) {
                logFile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(new Date() + " " + text + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
