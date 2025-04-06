package com.ylx.ability.jni;

public class JNIUtils {
    static {
        System.loadLibrary("native-lib"); // 对应 CMake 中的库名
    }
    public static native String getName(String name);
    public static native int init(String name);
}
