package com.ylx.demoapp.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object JsonUtils {
    inline fun <reified T> parseJsonFromRaw(context: Context, resourceId: Int): T {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(reader, type)
    }
}