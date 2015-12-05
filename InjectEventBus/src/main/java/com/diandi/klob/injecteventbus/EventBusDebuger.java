package com.diandi.klob.injecteventbus;

import android.util.Log;

/**
 * *******************************************************************************
 * *********    Author : klob(kloblic@gmail.com) .
 * *********    Date : 2015-12-04  .
 * *********    Time : 12:48 .
 * *********    Version : 1.0
 * *********    Copyright © 2015, klob, All Rights Reserved
 * *******************************************************************************
 */
public class EventBusDebuger {
    public static boolean loggable = true;

    public static boolean isLoggable() {
        return loggable;
    }

    public static void setLoggable(boolean loggable) {
        EventBusDebuger.loggable = loggable;
    }

    static void v(String tag, String msg) {
        if (isLoggable()) {
            Log.v(tag, msg);
        }
    }

    static void d(String tag, String msg) {
        if (isLoggable()) {
            Log.d(tag, msg);
        }
    }

    static void w(String tag, String msg) {
        if (isLoggable()) {
            Log.w(tag, msg);
        }
    }

    static void e(String tag, String msg) {
        if (isLoggable()) {
            Log.e(tag, msg);
        }
    }


    public static void e(String tag, String s, Throwable cause) {
        if (isLoggable()) {
            Log.e(tag, s, cause);
        }
    }

    public static void w(String tag, String s, Exception e) {
        if (isLoggable()) {
            Log.e(tag, s, e);
        }

    }
}
