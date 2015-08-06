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

import java.lang.Thread;
import java.lang.InterruptedException;
 
public class WakeRecoReceiver extends BroadcastReceiver
{
    //    private static final int HOLD_SEC = 10;
    @Override
    public void onReceive(Context context, Intent intent) {
        /*        final PowerManager.WakeLock wl;
        
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "My Tag"+new Date().getTime());
        
        wl.acquire();

        Intent intentT =  new Intent(this, ChooseRecoActivity.class);
        intentT.putExtra("recoscore", (ManItemScore) getIntent().getSerializableExtra("recoscore"));
        intentT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentT);

        new android.os.Handler().postDelayed(
                                             new Runnable() {
                                                 public void run() {
                                                     wl.release();
                                                 }
                                             }, 
                                             2000);
        */
    }
}

