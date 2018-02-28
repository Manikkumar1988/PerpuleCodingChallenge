package com.perpule.util;

import android.util.Log;

/**
 * Created by mani on 19/03/17.
 */
public class Util {

    public static void Logger(String text) {
        Logger("DEV123", text);
    }

    public static void Logger(String tag, String text) {
        Log.d(tag, text);
    }

    public static void printStacktrace(Exception e) {
        Logger("ERROR Occurred - HANDLED exception");
        e.printStackTrace();
        Logger("***");
        //throw new RuntimeException();
    }
}
