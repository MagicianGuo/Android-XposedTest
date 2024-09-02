package com.magicianguo.xposedrequestsu;

import android.app.Application;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Application application = (Application) param.thisObject;
                try {
                    Runtime.getRuntime().exec("su");
                } catch (Exception e) {
                    Toast.makeText(application, "Root权限请求失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
