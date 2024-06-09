package com.ylx.ability;

import android.util.Log;

public class LogUtils {
    private static final String TAG_YLX = "ylx_";

    private static final int LOG_DATA_MAX = 1024 * 2;

    private LogUtils() {
        // 防止实例化
        throw new AssertionError("No instances for you!");
    }

    public static void i(String extraTag, String message) {
        doLogWith((tag, msg) -> Log.i(TAG_YLX + extraTag, msg), extraTag, message);
    }

    public static void w(String extraTag, String message) {
        doLogWith((tag, msg) -> Log.w(TAG_YLX + extraTag, msg), extraTag, message);
    }

    public static void d(String extraTag, String message) {
        doLogWith((tag, msg) -> Log.d(TAG_YLX + extraTag, msg), extraTag, message);
    }

    public static void e(String extraTag, String message) {
        doLogWith((tag, msg) -> Log.e(TAG_YLX + extraTag, msg), extraTag, message);
    }

    private static void doLogWith(LogMothed logMothed, String extraTag, String msg) {
        int length = msg.length();
        if (length <= LOG_DATA_MAX) {
            logMothed.doLog(extraTag, msg);
            return;
        }
        int index = 0;
        while (index < length) {
            String subMsg = msg.substring(index, Math.min(index + LOG_DATA_MAX, length));
            logMothed.doLog(extraTag, subMsg);
            index = index + LOG_DATA_MAX;
        }
    }

    interface LogMothed {
        void doLog(String tag, String msg);
    }
}
