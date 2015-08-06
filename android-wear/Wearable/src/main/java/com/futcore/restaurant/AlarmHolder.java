package com.futcore.restaurant;

import java.util.List;
import java.util.ArrayList;

import java.util.List;
import java.util.ArrayList;

import android.app.PendingIntent;

public class AlarmHolder
{
    private static AlarmHolder instance = null;
    //    private Map<String,>
    
    protected AlarmHolder() {
        // Exists only to defeat instantiation.
    }
    
    public static AlarmHolder getInstance() {
        if(instance == null) {
            instance = new AlarmHolder();
        }
        return instance;
    }
}

