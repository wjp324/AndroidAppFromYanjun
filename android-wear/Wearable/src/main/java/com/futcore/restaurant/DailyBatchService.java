package com.futcore.restaurant;

import android.app.Service;
import android.app.PendingIntent;

import android.app.AlarmManager;

import android.content.Intent;
import android.content.IntentSender;
import android.app.IntentService;
import android.app.Service;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Bundle;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManEventWear;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManRecoItemWear;
import com.futcore.restaurant.models.ManRecoItem;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import com.futcore.restaurant.util.*;


public class DailyBatchService extends Service
{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    
    @Override
    public void onCreate() {
        super.onCreate();
        //        PreferenceUtil.clearAccRecoFromPreference(this);
        //        PreferenceUtil.saveDailyAlarmTouchToPreference(this, new Date().getTime());
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) { // May not have an Intent is the service was killed and restarted (See STICKY_SERVICE).
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            //            msg.obj = intent.getStringExtra("something");
            mServiceHandler.sendMessage(msg);
        }
        
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }




    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            PreferenceUtil.clearAccRecoFromPreference(DailyBatchService.this);
            PreferenceUtil.saveDailyAlarmTouchToPreference(DailyBatchService.this, new Date().getTime());
            stopSelf();
        }
    }
    
}

