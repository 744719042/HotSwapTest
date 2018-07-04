package com.example.apkresource;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;
import dalvik.system.PathClassLoader;

public class PatchInjector {
    public static void injectPatch(Context context) throws Exception {

        String dexPath = Environment.getExternalStorageDirectory() + File.separator + "final.dex";
        File patch = new File(dexPath);
        if (!patch.exists()) {
            return;
        }
        String dexOptPath = context.getCacheDir().getAbsolutePath() + File.separator + "DEX";
        File dexOpt = new File(dexOptPath);
        if (!dexOpt.exists()) {
            dexOpt.mkdir();
        }

        ClassLoader classLoader = new PatchClassLoader(context.getClassLoader(), patch.getAbsolutePath(), dexOpt);
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Field parent = ClassLoader.class.getDeclaredField("parent");
        parent.setAccessible(true);
        parent.set(pathClassLoader, classLoader);
        Class<?> clazz = pathClassLoader.loadClass("com.example.apkresource.BugActivity$Override");
        Object obj = clazz.newInstance();
        Class clazzBug = pathClassLoader.loadClass("com.example.apkresource.BugActivity");
        Field field = clazzBug.getDeclaredField("sInvoke");
        field.setAccessible(true);
        field.set(null, obj);
    }
}
