package com.futcore.restaurant;

import android.app.Service;
import android.app.IntentService;

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


//import com.commonsware.cwac.wakeful.WakefulIntentService;


//public class AlertService extends WakefulIntentService
public class WakeAlertServiceSim extends IntentService
{

    public WakeAlertServiceSim()
    {
        super("WakeAlertServiceSim");
    }
    
    @Override
    protected void onHandleIntent(final Intent intent) {
        
        Intent intentT =  new Intent(this, CfmFinishActivity.class);
        intentT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentT);
        
        Bundle extras = intent.getExtras();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(10000);
        System.out.println("111cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc111");

        try{
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        

        System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc111");
        WakeAlertReceiverSim.completeWakefulIntent(intent);
        
        /*        new android.os.Handler().postDelayed
            (
                new Runnable() {
                    public void run() {
                        System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc111");
                        WakeAlertReceiverSim.completeWakefulIntent(intent);
                    }
                }, 11000);
        */
        
        
        //        if(efeEventWearModel.size()==1){
        /*        if(efeEventWearModel.size()>0){
            
            if((efeEventWearModel.get(0).getEstEndTime()-new Date().getTime())<1000*60*ALERT_BEFORE_NMIN){
                
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(10000);

                if(wakedelay<11000)
                    wakedelay = 11000;
            }
        }

        if(wakedelay>0){
            new android.os.Handler().postDelayed
                (
                    new Runnable() {
                        public void run() {
                            WakeAlertReceiver.completeWakefulIntent(intent);
                        }
                    }, wakedelay);    
        }
        else{
            WakeAlertReceiver.completeWakefulIntent(intent);
        }
        */
    }

    public void onDestroy() {
        super.onDestroy();
    }
    
}

