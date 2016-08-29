package com.zero.waterfalllib.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @author linzewu
 * @date 16-8-29
 */
public class LogUtils {
    
    private static final String TAG = "WaterFallLog";
    
    private static boolean sIsPrintLog = false;
    
    public static void setIsPrintLog(boolean isPrintLog) {
        sIsPrintLog = isPrintLog;
    }

    public static void i(String msg) {
        if (sIsPrintLog) {
            Log.i(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (sIsPrintLog) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sIsPrintLog) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (sIsPrintLog) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sIsPrintLog) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (sIsPrintLog) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (sIsPrintLog) {
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (sIsPrintLog) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (sIsPrintLog) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (sIsPrintLog) {
            Log.w(tag, tr);
        }
    }

    /**
     * error为错误日志，出现时是后台配置错误或其它必须处理的情形
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * error为错误日志，出现时是后台配置错误或其它必须处理的情形
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void showToast(Context context, CharSequence text,
                                 int duration) {
        if (sIsPrintLog) {
            Toast.makeText(context, text, duration).show();
        }
    }

    public static void showToast(Context context, int resId, int duration) {
        if (sIsPrintLog) {
            Toast.makeText(context, resId, duration).show();
        }
    }

    /**
     * 获取当前调用堆栈信息
     *
     * @return
     */
    public static String getCurrentStackTraceString() {
        return Log.getStackTraceString(new Throwable());
    }

    /**
     * 获取堆栈信息
     *
     * @param tr
     * @return
     */
    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }
    
    
}
