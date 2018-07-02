package com.example.apkresource;

import dalvik.system.DexClassLoader;

public class PatchClassLoader extends DexClassLoader {
    public PatchClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
