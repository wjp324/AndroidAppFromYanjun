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

import com.futcore.restaurant.models.*;
import com.futcore.restaurant.util.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Map;
import java.util.HashMap;

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

import android.os.PowerManager.WakeLock;
import android.os.PowerManager;

import java.lang.Thread;
import java.lang.InterruptedException;


//import com.commonsware.cwac.wakeful.WakefulIntentService;
//public class AlertService extends WakefulIntentService
public class RecoGeneService extends IntentService
{
    private static final int TIMEZONE_OFFSET = 8;

    public static final int RECO_INTERVAL = 10*60;
    public static final int RUN_RECO_INTERVAL = 20*60;

    private static final int NOTI_SCORE_THRESHOLD = 0;
    private static final int UPDATE_SCORE_THRESHOLD = 0;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

	private static Dao<ManRecoWrapper, Long> recoWrapperDao = null;

    //    public static Map<Long, List<ManItemScore>> mRecos = new HashMap<Long, List<ManItemScore>>();
    public static Map<Long, List<ManItemScore>> mRecos = null;

    public static void clearMRecos()
    {
        mRecos = null;
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||eventWearDao==null||recoWrapperDao == null){
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
                recoWrapperDao = helper.getRecoWrapperDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public RecoGeneService()
    {
        super("RecoGeneService");
    }
    
    @Override
    protected void onHandleIntent(final Intent intent) {
        Bundle extras = intent.getExtras();
        getDao();
        updateRecos();
        //        List<ManItemScore> itemRecos = getCurrentRecos();
        getCurrentRecos();

        RecoGeneReceiver.completeWakefulIntent(intent);
    }

    private static int getTimeZoneOffsetOfTime(long time)
    {
        return time%(3600*24*1000)+TIMEZONE_OFFSET*3600*1000>=24*3600*1000?TIMEZONE_OFFSET-24:TIMEZONE_OFFSET;
    }

    public static long getIntervalTimeOfDay(long cTimeStamp)
    {
        long timeofday = (cTimeStamp/1000)%(3600*24)+3600*getTimeZoneOffsetOfTime(cTimeStamp);
        return (long)(timeofday/RECO_INTERVAL)*RECO_INTERVAL;
    }

    //    private List<ManItemScore> getCurrentRecos()
    private void getCurrentRecos()
    {
        long cTimeStamp = new Date().getTime();
        
        long timeofday = (cTimeStamp/1000)%(3600*24)+3600*getTimeZoneOffsetOfTime(cTimeStamp);

        long timeofday1 = (long)(timeofday/RECO_INTERVAL)*RECO_INTERVAL;
        
        System.out.println("ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+timeofday);
        System.out.println("2ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+timeofday1);

        List<ManItemScore> recoItems = mRecos.get(timeofday1);

        int ccccccc = recoItems.size();
        System.out.println("2222zzzzttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+ccccccc);
        cleanRecoItems(recoItems);
        System.out.println("3333zzzzttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+recoItems.size());
        
        if(recoItems!=null&&recoItems.size()>0){

            final PowerManager.WakeLock wl;

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "My Tag"+new Date().getTime());
            
            wl.acquire();
            

            Intent intentT =  new Intent(this, ChooseRecoActivity.class);
            intentT.putExtra("recoscore", recoItems.get(0));
            //            intentT.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intentT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            //            intentT.putExtra("eveid", intent.getExtras().getString("eveid"));
        //        intentT.putExtra();
            startActivity(intentT);

            new android.os.Handler().postDelayed(
                                                 new Runnable() {
                                                     public void run() {
                                                         wl.release();
                                                     }
                                                 }, 
                                                 2000);
            
            System.out.println("3ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+mRecos.get(timeofday1).get(0).getManItem().getItemName());
            System.out.println("4ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+mRecos.get(timeofday1).get(0).getScore());
            
        }

        //        return mRecos.get(timeofday1);
        
    }

    private void cleanRecoItems(List<ManItemScore> recoItems)  // easy version
    {
        List<ManItemScore> accRecoItems = new ArrayList<ManItemScore>(PreferenceUtil.getAccRecoFromPreference(this).values());
        System.out.println("1zzzzttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt"+accRecoItems.size());

        /*        for(ManItemScore mis:recoItems){
            if(accRecoItems.contains(mis))
                recoItems.remove(mis);
        }
        */
        
        Iterator<ManItemScore> iter = recoItems.iterator();
        while (iter.hasNext()) {
            ManItemScore mis = iter.next();
            if (accRecoItems.contains(mis))
                iter.remove();
        }        
    }
    

    //    private void updateRecos()
    public static void updateRecos()
    {
        if(mRecos==null){
            System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiinit recosssssssssssss");
            
            mRecos = new HashMap<Long, List<ManItemScore>>();
            
            try{
                QueryBuilder<ManRecoWrapper, Long> queryBuilder1 = recoWrapperDao.queryBuilder();
                queryBuilder1.orderBy(ManRecoWrapper.ID_FIELD_NAME, false).limit(1);
                PreparedQuery<ManRecoWrapper> preparedQuery1 = queryBuilder1.prepare();
            
                List<ManRecoWrapper> recoWs = recoWrapperDao.query(preparedQuery1);

                if(recoWs.size()>0){
                    mRecos = recoWs.get(0).getSeRecos();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //        System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu recosssssssssssss"+" time: "+mRecos.size()+(new Date().getTime()/1000));

    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
}

