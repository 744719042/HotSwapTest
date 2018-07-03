package com.example.apkresource;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
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

        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOptPath,
                null, PatchInjector.class.getClassLoader());
        PathClassLoader pathClassLoader = (PathClassLoader) PatchInjector.class.getClassLoader();
        // 反射获取pathList成员变量Field
        Field dexPathList = BaseDexClassLoader.class.getDeclaredField("pathList");
        dexPathList.setAccessible(true);
        // 现获取两个类加载器内部的pathList成员变量
        Object pathList = dexPathList.get(pathClassLoader);
        Object fixPathList = dexPathList.get(dexClassLoader);

        // 反射获取DexPathList类的dexElements成员变量Field
        Field dexElements = pathList.getClass().getDeclaredField("dexElements");
        dexElements.setAccessible(true);
        // 反射获取pathList对象内部的dexElements成员变量
        Object originDexElements = dexElements.get(pathList);
        Object fixDexElements = dexElements.get(fixPathList);

        // 使用反射获取两个dexElements的长度
        int originLength = Array.getLength(originDexElements);
        int fixLength = Array.getLength(fixDexElements);
        int totalLength = originLength + fixLength;
        // 获取dexElements数组的元素类型
        Class<?> componentClass = originDexElements.getClass().getComponentType();
        // 将修复dexElements的元素放在前面，原始dexElements放到后面，这样就保证加载类的时候优先查找修复类
        Object[] elements = (Object[]) Array.newInstance(componentClass, totalLength);
        for (int i = 0; i < totalLength; i++) {
            if (i < fixLength) {
                elements[i] = Array.get(fixDexElements, i);
            } else {
                elements[i] = Array.get(originDexElements, i - fixLength);
            }
        }
        // 将新生成的dexElements数组注入到PathClassLoader内部，
        // 这样App查找类就会先从fixdex查找，在从App安装的dex里查找
        dexElements.set(pathList, elements);

        Class<?> clazz = pathClassLoader.loadClass("com.example.apkresource.BugActivity$Override");
        Object obj = clazz.newInstance();
        Class clazzBug = pathClassLoader.loadClass("com.example.apkresource.BugActivity");
        Field field = clazzBug.getDeclaredField("sInvoke");
        field.setAccessible(true);
        field.set(null, obj);
    }
}
