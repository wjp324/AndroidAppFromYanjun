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
 
//public class WakeAlertReceiver extends BroadcastReceiver {
public class WakeAlertReceiverSimp extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, WakeAlertServiceSimp.class);

        //        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "simp wake");
        //        wl.acquire();
        
        startWakefulService(context, service);
        
        /*        try{
            Thread.sleep(90000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
        wl.release();
        */
        
        //        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^99999999999999999999999999999999999999999999");
    }
}

