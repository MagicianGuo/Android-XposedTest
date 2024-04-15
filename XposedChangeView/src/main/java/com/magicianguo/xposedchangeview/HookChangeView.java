package com.magicianguo.xposedchangeview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookChangeView implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (TextUtils.equals(lpparam.packageName, "com.magicianguo.xposedtestapp")) {
            Class<?> clsMainActivity = XposedHelpers.findClass("com.magicianguo.xposedtestapp.MainActivity", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(clsMainActivity, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Activity activity = (Activity) param.thisObject;
                    Toast.makeText(activity, "APP已被Xposed修改！", Toast.LENGTH_SHORT).show();
                    TextView mTvTitle = (TextView) XposedHelpers.getObjectField(activity, "mTvTitle");
                    mTvTitle.setText("你好，Xposed框架");
                    // 找到mTvTitle的父布局
                    LinearLayout parent = (LinearLayout) mTvTitle.getParent();
                    SurfaceView surfaceView = null;
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View v = parent.getChildAt(i);
                        Log.d("TAG", "afterHookedMethod: v = "+v);
                        if (v instanceof SurfaceView) {
                            surfaceView = (SurfaceView) v;
                            break;
                        }
                    }
                    parent.addView(ViewTools.createView(activity, surfaceView), 0);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> System.exit(-1), 100L);
            throw new RuntimeException("请不要将此插件集成在包名“com.magicianguo.xposedtestapp”以外的应用！");
        }
    }
}
