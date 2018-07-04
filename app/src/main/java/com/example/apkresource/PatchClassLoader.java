package com.example.apkresource;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

public class PatchClassLoader extends ClassLoader {
    private PatchDexClassLoader patchDexClassLoader;

   public PatchClassLoader(ClassLoader origin, String dexPath, File dexOptPath) {
       patchDexClassLoader = new PatchDexClassLoader(dexPath, dexOptPath,
               null, origin);
   }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return patchDexClassLoader.loadClass(name);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return patchDexClassLoader.findClass(name);
    }

    public static class PatchDexClassLoader extends BaseDexClassLoader {
       public PatchDexClassLoader(String dexPath, File optimizedDirectory, String librarySearchPath, ClassLoader parent) {
           super(dexPath, optimizedDirectory, librarySearchPath, parent);
       }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }
    }
}
