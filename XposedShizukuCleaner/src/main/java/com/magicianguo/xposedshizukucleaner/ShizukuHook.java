package com.magicianguo.xposedshizukucleaner;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.OutputStream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ShizukuHook {
    private static final String TAG = "ShizukuHook";
    private static final Long CLEAN_DELAY_TIME = 5000L;
    private static final HandlerThread mHandlerThread = new HandlerThread("XPosedShizuku");
    private static Handler mHandler;
    private static final Runnable mCleanRunnable = ShizukuHook::doClean;

    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        Class<?> clsMainActivity = XposedHelpers.findClass("moe.shizuku.manager.MainActivity", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clsMainActivity, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                tryClean();
            }
        });
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                tryClean();
            }
        });
    }

    private static synchronized void tryClean() {
        mHandler.removeCallbacks(mCleanRunnable);
        mHandler.postDelayed(mCleanRunnable, CLEAN_DELAY_TIME);
    }

    private static void doClean() {
        try {
            Log.d(TAG, "tryClean: start");
            Process process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write(("rm -rf /data/local/tmp/shizuku/\nrm -rf /data/local/tmp/shizuku_starter\n").getBytes());
            os.flush();
            os.close();
            Log.d(TAG, "tryClean: end");
        } catch (Exception e) {
            Log.e(TAG, "tryClean: error!");
            e.printStackTrace();
        }
    }

}
