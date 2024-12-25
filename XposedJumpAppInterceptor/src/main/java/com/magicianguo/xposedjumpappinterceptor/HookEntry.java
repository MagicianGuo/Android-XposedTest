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
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private AlertDialog mDialog = null;
    private Activity mLastResumedActivity = null;
    private Intent mIntent = null;
    private String mPkgName = "";
    private String mTargetPkgName = "";
    private String mLabel = "";
    private String mTargetLabel = "";
    private Application.ActivityLifecycleCallbacks mActivityCallbacks = new Application.ActivityLifecycleCallbacks() {
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
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Application application = (Application) param.thisObject;
                LogUtil.init(application);
                application.registerActivityLifecycleCallbacks(mActivityCallbacks);
            }
        });

        Class<?> clsContextImpl = XposedHelpers.findClass("android.app.ContextImpl", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(clsContextImpl, "startActivity", Intent.class, Bundle.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        LogUtil.writeLog("load method: ContextImpl - startActivity");
                        resolveIntent(param);
                        return null;
                    }
                });

        XposedHelpers.findAndHookMethod(Activity.class, "startActivityForResult", Intent.class, int.class, Bundle.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        LogUtil.writeLog("load method: Activity - startActivityForResult");
                        resolveIntent(param);
                        return null;
                    }
                });
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        ResourceUtil.initZygote(startupParam);
    }

    private void resolveIntent(XC_MethodHook.MethodHookParam param) throws PackageManager.NameNotFoundException {
        Context context = (Context) param.thisObject;
        mIntent = (Intent) param.args[0];
        boolean isActivity = context instanceof Activity;
        LogUtil.writeLog("context = " + context + " , isActivity? " + isActivity + " , intent = " + mIntent);

        ComponentName component = mIntent.getComponent();
        mPkgName = context.getPackageName();
        if (component != null) {
            mTargetPkgName = component.getPackageName();
        } else {
            mTargetPkgName = mIntent.getPackage();
        }
        LogUtil.writeLog("pkgName = " + mPkgName + " , targetPkgName = " + mTargetPkgName);
        if (!TextUtils.equals(mPkgName, mTargetPkgName)) {
            PackageManager pm = context.getPackageManager();
            mLabel = pm.getApplicationLabel(pm.getApplicationInfo(mPkgName, 0)).toString();
            if (mTargetPkgName != null) {
                mTargetLabel = pm.getApplicationLabel(pm.getApplicationInfo(mTargetPkgName, 0)).toString();
            }
            if (isActivity) {
                showDialog(context, (Activity) context, param);
            } else {
                showDialog(context, mLastResumedActivity, param);
            }
        } else {
            invokeOriginalMethod(param, context);
        }
    }

    private synchronized void showDialog(Object object, Activity activity, XC_MethodHook.MethodHookParam param) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        String msg;
        if (mTargetLabel != null && !mTargetLabel.isEmpty()) {
            msg = String.format("“%s”想要跳转到“%s”，是否允许？", mLabel, mTargetLabel);
        } else {
            msg = String.format("“%s”想要跳转到其他应用，是否允许？\n" + mIntent, mLabel);
        }
        mTargetLabel = "";
        LogUtil.writeLog("showDialog: msg = " + msg);
        ResourceUtil.addModuleAssetPath(activity);
        mDialog = new AlertDialog.Builder(activity, R.style.CommonDialog)
                .setMessage(msg)
                .setPositiveButton("允许", (dialog, which) -> invokeOriginalMethod(param, object))
                .setNegativeButton("禁止", (dialog, which) -> mockActivityLifecycle(activity))
                .setOnCancelListener(dialog -> mockActivityLifecycle(activity))
                .create();
        mDialog.show();
    }

    private void mockActivityLifecycle(Activity activity) {
        ReflectUtil.callMethod(Activity.class, activity, "onPause");
        ReflectUtil.callMethod(Activity.class, activity, "onStop");
        ReflectUtil.callMethod(Activity.class, activity, "onStart");
        ReflectUtil.callMethod(Activity.class, activity, "onResume");
    }

    private void invokeOriginalMethod(XC_MethodHook.MethodHookParam param, Object obj) {
        try {
            XposedBridge.invokeOriginalMethod(param.method, obj, param.args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            LogUtil.writeLog("invokeOriginalMethod: error! e = " + e.getMessage());
        }
    }

}
