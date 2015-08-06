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
public class WakeAlertService extends IntentService
{

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private List<ManEventWear> efeEventWearModel = new ArrayList<ManEventWear>();
    private List<ManEventWear> planEventWearModel = new ArrayList<ManEventWear>();

    private static final byte DELETE_TYPE_PLAN = 10;
    private static final byte DELETE_TYPE_RECO = 11;
    private static final byte DELETE_TYPE_PERMANENT = 12;

    private static final int ALERT_BEFORE_NMIN = 5;
    
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||eventWearDao==null){
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public WakeAlertService()
    {
        super("WakeAlertService");
    }
    
    @Override
    protected void onHandleIntent(final Intent intent) {
        Bundle extras = intent.getExtras();
        getDao();

        updateEfeEventWearModel();
        //        updatePlanEventWearModel();
        
        /*        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        System.out.println(efeEventWearModel.size());
        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))111");
        System.out.println(planEventWearModel.size());
        */
        
        //        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //        v.vibrate(10000);
        
        
        // Do the work that requires your app to keep the CPU running.
        // ...
        // Release the wake lock provided by the WakefulBroadcastReceiver.

        int wakedelay = 0;
        /*        if(planEventWearModel.size()>0){
            System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))ppp");
            //                Intent intent0 =  new Intent(this, ConfirmForceRecoActivity.class);
            ManEventWear nearevent = planEventWearModel.get(0);

            if((nearevent.getStartTime()-new Date().getTime())<1000*60*ALERT_BEFORE_NMIN){
                System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))near");
                System.out.println(nearevent.getStartTime()-new Date().getTime());
                
                
                Intent intent0 =  new Intent(this, ConfirmPlanActivity.class);
                //            Intent intent0 =  new Intent(this, MainActivity.class);
                intent0.putExtra("planwear", nearevent);
                //                intent0.putExtra("recowear", (ManRecoItemWear)curRecoItemWearScoreModel.get(j++));
                //            intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //                PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(2000);
            
                startActivity(intent0);

                if(wakedelay<25000)
                    wakedelay = 25000;
            }
        }
        */
        
        //        if(efeEventWearModel.size()==1){
        if(efeEventWearModel.size()>0){
            
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
    }

    private void updateEfeEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.DELETE_FIELD_NAME, (byte)0).and().isNotNull(ManEventWear.STARTTIME_FIELD_NAME).and().gt(ManEventWear.EST_END_TIME_FIELD_NAME, new Date().getTime());
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.DELETE_FIELD_NAME, (byte)1).and().isNotNull(ManEventWear.STARTTIME_FIELD_NAME).and().gt(ManEventWear.EST_END_TIME_FIELD_NAME, new Date().getTime());
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            
            efeEventWearModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlanEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.CREATETIME_FIELD_NAME, ManEventWear.STARTTIME_FIELD_NAME);
            queryBuilder1.orderBy(ManEventWear.STARTTIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().isNotNull(ManEventWear.STARTTIME_FIELD_NAME).and().eq(ManEventWear.DELETE_FIELD_NAME, DELETE_TYPE_PLAN).and().gt(ManEventWear.STARTTIME_FIELD_NAME, new Date().getTime());
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            planEventWearModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
}

