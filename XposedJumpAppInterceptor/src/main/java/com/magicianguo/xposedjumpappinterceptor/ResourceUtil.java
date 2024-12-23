package com.magicianguo.xposedjumpappinterceptor;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookZygoteInit;

public class ResourceUtil {
    private static String modulePath;

    public static void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        modulePath = startupParam.modulePath;
    }

    public static void addModuleAssetPath(Context context) {
        addModuleAssetPath(context.getResources());
    }

    private static void addModuleAssetPath(Resources resources) {
        try {
            AssetManager assets = resources.getAssets();
            Method addAssetPathMethod = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assets, modulePath);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LogUtil.writeLog("addModuleAssetPath: error! e = "+e.getMessage());
            e.printStackTrace();
        }
    }
}
