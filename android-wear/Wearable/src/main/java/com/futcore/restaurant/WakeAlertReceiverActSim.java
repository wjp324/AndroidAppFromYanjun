package com.futcore.restaurant;

import android.content.BroadcastReceiver;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Vibrator;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import android.os.PowerManager.WakeLock;
import android.os.PowerManager;

import android.os.PowerManager.WakeLock;
import android.os.PowerManager;

import java.lang.Thread;
import java.lang.InterruptedException;
 
public class WakeAlertReceiverActSim extends BroadcastReceiver
                                             //public class WakeAlertReceiverActSim extends WakefulBroadcastReceiver
{
    //    private static final int HOLD_SEC = 10;
    @Override
    public void onReceive(Context context, Intent intent) {
        //        Intent service = new Intent(context, WakeAlertServiceSim.class);
        //        startWakefulService(context, service);
        //        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+intent.getExtras().getString("eveid"));
        final PowerManager.WakeLock wl;
        
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag"+new Date().getTime());
        wl = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "My Tag"+new Date().getTime());
        
        wl.acquire();

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        //        Intent intent1 = new Intent(context, SendEventNotiService.class);
        //        context.startService(intent1);
        
        Intent intentT =  new Intent(context, CfmFinishActivity.class);
        intentT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        intentT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //        Intent.FLAG_ACTIVITY_CLEAR_TOP
        
        //        intentT.putExtra("holdsec", HOLD_SEC);
        intentT.putExtra("holdsec", intent.getExtras().getInt("holdsec"));
        intentT.putExtra("eveid", intent.getExtras().getString("eveid"));
        //        intentT.putExtra();
        context.startActivity(intentT);

        new android.os.Handler().postDelayed(
                                             new Runnable() {
                                                 public void run() {
                                                     System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ffffffffff");
                                                     wl.release();
                                                     //          Log.i("tag", "This'll run 300 milliseconds later");
                                                 }
                                             }, 
                                             2000);
        /*        try {
            Thread.sleep(90000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }        

        */
    }
}

