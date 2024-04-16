package com.magicianguo.xposedlogwebviewurl;

import android.app.Application;
import android.webkit.WebView;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookLogWebViewUrl implements IXposedHookLoadPackage {
    public Application application;
    private final XC_MethodHook mHookWebViewLoadUrl = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            String url = (String) param.args[0];
            String logPath = application.getExternalFilesDir(null).getParent();
            File logFile = new File(logPath, "url_list.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(new Date() + " - url: " + url + "\n");
            fw.close();
        }
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                // 此时应用已创建
                application = (Application) param.thisObject;
                XposedHelpers.findAndHookMethod(WebView.class, "loadUrl", String.class, mHookWebViewLoadUrl);
            }
        });
    }
}
