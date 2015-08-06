package com.futcore.restaurant;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Context;

public class Utils
{
    public static boolean isMyServiceRunning(Context cxt,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

