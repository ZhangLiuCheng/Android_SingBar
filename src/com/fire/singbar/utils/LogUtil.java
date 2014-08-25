package com.fire.singbar.utils;

import java.util.Locale;

import android.util.Log;

public final class LogUtil {
	
    public static String sTAG = "test";

    public static boolean sDEBUG = Log.isLoggable(sTAG, Log.INFO);

	private LogUtil() {
		
	}
	
    public static void setTag(String tag) {
        d("Changing log tag to %s", tag);
        sTAG = tag;

        sDEBUG = Log.isLoggable(sTAG, Log.VERBOSE);
    }

    public static void v(String format, Object... args) {
        if (sDEBUG) {
            Log.v(sTAG, buildMessage(format, args));
        }
    }

    public static void d(String format, Object... args) {
        Log.d(sTAG, buildMessage(format, args));
    }

    public static void e(String format, Object... args) {
        Log.e(sTAG, buildMessage(format, args));
    }

    public static void e(Throwable tr, String format, Object... args) {
        Log.e(sTAG, buildMessage(format, args), tr);
    }

    public static void wtf(String format, Object... args) {
        Log.wtf(sTAG, buildMessage(format, args));
    }

    public static void wtf(Throwable tr, String format, Object... args) {
        Log.wtf(sTAG, buildMessage(format, args), tr);
    }

    private static String buildMessage(String format, Object... args) {
        final String msg = (args == null) ? format : String.format(Locale.US, format, args);
        final StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            final Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s",
                Thread.currentThread().getId(), caller, msg);
    }
}
