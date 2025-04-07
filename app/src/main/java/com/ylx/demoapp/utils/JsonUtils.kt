package com.ylx.demoapp.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtils {
    inline fun <reified T> parseJsonFromRaw(context: Context, resId: Int): T {
        val inputStream = context.resources.openRawResource(resId)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
    }
}