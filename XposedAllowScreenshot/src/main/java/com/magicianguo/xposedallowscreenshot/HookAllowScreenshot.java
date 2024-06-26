package com.magicianguo.xposedallowscreenshot;

import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAllowScreenshot implements IXposedHookLoadPackage {
    private final XC_MethodHook mHookDeleteWindowFlagSecure = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            Integer flags = (Integer) param.args[0];
            // 只把FLAG_SECURE移除，其他不受影响
            param.args[0] = flags & (~WindowManager.LayoutParams.FLAG_SECURE);
        }
    };

    private final XC_MethodHook mHookDeleteSurfaceSecure = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            param.args[0] = false;
        }
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Window.class, "setFlags", Integer.TYPE, Integer.TYPE, mHookDeleteWindowFlagSecure);
        XposedHelpers.findAndHookMethod(SurfaceView.class, "setSecure", Boolean.TYPE, mHookDeleteSurfaceSecure);
    }
}
