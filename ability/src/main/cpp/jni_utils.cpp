#include "com_ylx_ability_jni_JNIUtils.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_ylx_ability_jni_JNIUtils_getName(JNIEnv *env, jclass clazz, jstring name) {
    const char *str = env->GetStringUTFChars(name, nullptr);
    env->ReleaseStringUTFChars(name, str);
    return name;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ylx_ability_jni_JNIUtils_init(JNIEnv *env, jclass clazz, jstring name) {
    return 1;
}