package com.magicianguo.xposedjumpappinterceptor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {
    private AlertDialog mDialog = null;
    private Activity mLastResumedActivity = null;
    private String mLabel = "";
    private String mTargetLabel = "";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Application application = (Application) param.thisObject;
                application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        mLastResumedActivity = activity;
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                    }
                });
            }
        });

        Class<?> clsContextImpl = XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clsContextImpl, "startActivity", Intent.class, Bundle.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) param.thisObject;
                        Intent intent = (Intent) param.args[0];

                        ComponentName component = intent.getComponent();
                        String pkgName = context.getPackageName();
                        String targetPkgName;
                        if (component != null) {
                            targetPkgName = component.getPackageName();
                        } else {
                            targetPkgName = intent.getPackage();
                        }
                        if (!TextUtils.equals(pkgName, targetPkgName)) {
                            PackageManager pm = context.getPackageManager();
                            mLabel = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0)).toString();
                            mTargetLabel = pm.getApplicationLabel(pm.getApplicationInfo(targetPkgName, 0)).toString();
                            showDialog(context, mLastResumedActivity, param);
                        } else {
                            invokeOriginalMethod(param, context);
                        }
                        return null;
                    }
                });

        XposedHelpers.findAndHookMethod(Activity.class, "startActivityForResult", Intent.class, int.class, Bundle.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        Intent intent = (Intent) param.args[0];

                        ComponentName component = intent.getComponent();
                        String pkgName = activity.getPackageName();
                        String targetPkgName;
                        if (component != null) {
                            targetPkgName = component.getPackageName();
                        } else {
                            targetPkgName = intent.getPackage();
                        }
                        if (!TextUtils.equals(pkgName, targetPkgName)) {
                            PackageManager pm = activity.getPackageManager();
                            mLabel = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0)).toString();
                            mTargetLabel = pm.getApplicationLabel(pm.getApplicationInfo(targetPkgName, 0)).toString();
                            showDialog(activity, activity, param);
                        } else {
                            invokeOriginalMethod(param, activity);
                        }
                        return null;
                    }
                });
    }

    private synchronized void showDialog(Object object, Activity activity, XC_MethodHook.MethodHookParam param) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new AlertDialog.Builder(activity)
                .setMessage(String.format("“%s”想要跳转到“%s”，是否允许？", mLabel, mTargetLabel))
                .setPositiveButton("允许", (dialog, which) -> {
                    invokeOriginalMethod(param, object);
                })
                .setNegativeButton("禁止", (dialog, which) -> {})
                .create();
        mDialog.show();
    }

    private void invokeOriginalMethod(XC_MethodHook.MethodHookParam param, Object obj) {
        try {
            XposedBridge.invokeOriginalMethod(param.method, obj, param.args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
