package com.magicianguo.xposedshizukucleaner;

import android.text.TextUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (TextUtils.equals(lpparam.packageName, "moe.shizuku.privileged.api")) {
            ShizukuHook.init(lpparam);
        }
    }
}
