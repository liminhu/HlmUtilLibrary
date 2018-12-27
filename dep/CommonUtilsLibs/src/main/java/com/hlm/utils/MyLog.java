package com.hlm.utils;

import android.util.Log;

import com.hlm.config.CommonConfig;

/**
 * Created by caoyanglong on 2017/7/27.
 */

public class MyLog {
    private static String a = "iplay";


    public static void v(String var0, Object... var1) {
        if (CommonConfig.isDebug()){
                Log.v(a, String.format(var0, var1));
        }
    }

    public static void i(String var0, Object... var1) {
        if (CommonConfig.isDebug()) {
                Log.i(a, String.format(var0, var1));
        }
    }

    public static void w(String var0, Object... var1) {
        if (CommonConfig.isDebug()) {
                Log.w(a, String.format(var0, var1));
        }
    }

    public static void d(String var0, Object... var1) {
        if (CommonConfig.isDebug()) {
                Log.d(a, String.format(var0, var1));
        }
    }

    public static void e(String var0, Object... var1) {
        if (CommonConfig.isDebug()) {
                Log.e(a, String.format(var0, var1));
        }
    }

    public static void e(Throwable t, String var0, Object... var1) {
        if (CommonConfig.isDebug()) {
                Log.e(a, String.format(var0, var1));
                t.printStackTrace();
        }
    }
}
