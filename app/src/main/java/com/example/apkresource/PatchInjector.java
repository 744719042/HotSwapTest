package com.example.apkresource;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Field;

public class PatchInjector {
    public static void injectPatch(Context context) throws Exception {
        File patch = new File(context.getCacheDir(), "patch.jar");
        if (!patch.exists()) {
            return;
        }
        String dexPath = patch.getAbsolutePath();
        String dexOptPath = context.getCacheDir().getAbsolutePath() + File.separator + "DEX";
        File dexOpt = new File(dexOptPath);
        if (!dexOpt.exists()) {
            dexOpt.mkdir();
        }

        ClassLoader pathClassLoader = context.getClassLoader();
        ClassLoader pathParent = pathClassLoader.getParent();
        PatchClassLoader patchClassLoader = new PatchClassLoader(dexPath, dexOptPath, null, pathParent);
        Field parent = ClassLoader.class.getDeclaredField("parent");
        parent.setAccessible(true);
        parent.set(pathClassLoader, patchClassLoader);

        Class<?> clazz = patchClassLoader.loadClass("com.example.apkresource.BugActivity$Override");
        Object obj = clazz.newInstance();
        Class clazzBug = pathClassLoader.loadClass("com.example.apkresource.BugActivity");
        Field field = clazzBug.getDeclaredField("sInvoke");
        field.set(null, obj);
    }
}
