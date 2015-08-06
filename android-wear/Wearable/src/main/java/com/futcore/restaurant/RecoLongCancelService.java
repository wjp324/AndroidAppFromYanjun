package com.futcore.restaurant;

import android.app.Service;

//import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.app.PendingIntent;

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
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManEventWear;
import com.futcore.restaurant.models.ManRecoItem;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
//import android.app.Notification.BigTextStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.WearableExtender;


//import com.futcore.restaurant.models.DatabaseHelper;
import java.sql.SQLException;

import android.graphics.BitmapFactory;
import android.content.Context;

import android.os.Vibrator;
import android.os.SystemClock;

import android.app.AlarmManager;


public class RecoLongCancelService extends Service
{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        //        Message msg = mServiceHandler.obtainMessage();
        //        msg.arg1 = startId;
        //        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
        //        return START_NOT_STICKY;
    }
    

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        //        Intent sintent = new Intent(this, WakeAlertReceiver.class);
        Intent sintent = new Intent(this, RecoGeneReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, sintent, 0);
        
        //        final int FIFTEEN_SEC_MILLIS = 1000*120;
        //        final int FIFTEEN_SEC_MILLIS = 1000*10;
        final int FIFTEEN_SEC_MILLIS = 1000*60*20;
 
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //keep only one repeating alarm
        alarmManager.cancel(pendingIntent);
        //        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+FIFTEEN_SEC_MILLIS, FIFTEEN_SEC_MILLIS, pendingIntent);
        //        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*1000, FIFTEEN_SEC_MILLIS, pendingIntent);
        
        stopSelf();
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
}

