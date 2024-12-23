package com.magicianguo.xposedjumpappinterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static void callMethod(Class<?> cls, Object obj, String name) {
        try {
            Method declaredMethod = cls.getDeclaredMethod(name);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(obj);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
