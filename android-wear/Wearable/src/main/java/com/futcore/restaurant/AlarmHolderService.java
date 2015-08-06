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

public class AlarmHolderService extends Service
{
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

    private static final int L1_ALARM_SEC = 90;
    private static final int L2_ALARM_SEC = 300;

    private static final int L1_HOLD_SEC = 90;
    private static final int L2_HOLD_SEC = 10;
    
    private static final int L1_OFFSET = 3*24*3600;

    private int mHoldSec;
    private int mAlarmSec;
    
    //    private static final int L3_ALARM_SEC = ;
    //    private static final int L4_ALARM_SEC = ;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private AlarmManager mAlarmManager;

    //    private String mEventId;
    private ArrayList<String> mEventIds;
    private List<ManEvent> mEvents;
    private String mOp;
    private String mLvl;
    private boolean mCancelOld = false;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao == null||itemDao == null||eventWearDao == null||recoItemWearDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
                recoItemWearDao = helper.getRecoItemWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //        Bundle extras = intent.getExtras();
        //        if(extras !=null) {
            //            mEventId = extras.getString("eventid");
            //            mEventIds = getStringArrayListExtra("eventids");
            mOp = intent.getStringExtra("mop");
            mLvl = intent.getStringExtra("mlvl");
            mEvents = (ArrayList<ManEvent>)intent.getSerializableExtra("events");
            //            if(intent.getBooleanExtra("cancelold", false)!=null)
            mCancelOld = intent.getBooleanExtra("cancelold", false);
            
            //            mOp = extras.getString("mop");
            //            mLvl = extras.getString("mlvl");
            
            if(mLvl.equals("l1")){
                mHoldSec = L1_HOLD_SEC;
                mAlarmSec = L1_ALARM_SEC;
            }
            else if(mLvl.equals("l2")){
                mHoldSec = L2_HOLD_SEC;
                mAlarmSec = L2_ALARM_SEC;
            }
            //        }
            //        else{
            //            stopSelf();
            //        }
        
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        
        super.onCreate();
        //        initNotification();
        getDao();
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
    
    public void setEveAlarm()
    {
        
        //not good
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //        System.out.println("===========================================================================================================");
        
        for(ManEvent cceve:mEvents){
            long toEnd = cceve.getEstEndTime().getTime()-new Date().getTime();

            if(toEnd<L1_ALARM_SEC*1000){
                continue;
            }
            else if(toEnd<L2_ALARM_SEC*1000){
        System.out.println("===========================================================================================================11111111111111111111111111");
                Intent sintentAct = new Intent(this, WakeAlertReceiverActSim.class);
                sintentAct.putExtra("eveid", cceve.getEventId());
                //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000-L1_OFFSET, sintentAct, 0);
                
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-L1_ALARM_SEC*1000, pendingIntentAct);
                
            }
            else{
                System.out.println("===========================================================================================================222222222222222222222222222222222222p");
                Intent sintentAct1 = new Intent(this, WakeAlertReceiverActSim.class);
                sintentAct1.putExtra("eveid", cceve.getEventId());
                //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                sintentAct1.putExtra("holdsec", L2_HOLD_SEC);
                PendingIntent pendingIntentAct1 = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000, sintentAct1, 0);
                
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-L2_ALARM_SEC*1000, pendingIntentAct1);
                
                System.out.println("===========================================================================================================222222222222222222222222222222222222");
                Intent sintentAct = new Intent(this, WakeAlertReceiverActSim.class);
                sintentAct.putExtra("eveid", cceve.getEventId());
                //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000-L1_OFFSET, sintentAct, 0);
                
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-L1_ALARM_SEC*1000, pendingIntentAct);
            }
            //            if(toEnd>L2_ALARM_SEC*1000)
            /*            if(toEnd>mAlarmSec*1000){
            }
            */
                //                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-L2_ALARM_SEC*1000, pendingIntentAct);
        }
    }

    public void cancelEveAlarm()
    {
        System.out.println("cccccccccccccccccccccccccccccccccccccccccccccccccccccc--------------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        //not good
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        
        for(ManEvent cceve:mEvents){
            Intent sintentAct = new Intent(this, WakeAlertReceiverActSim.class);
            sintentAct.putExtra("eveid", cceve.getEventId());
            //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
            sintentAct.putExtra("holdsec", L2_HOLD_SEC);
            PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000, sintentAct, PendingIntent.FLAG_UPDATE_CURRENT);
            //            long toEnd = cceve.getEstEndTime().getTime()-new Date().getTime();
            //            if(toEnd>L2_ALARM_SEC*1000)
            //            if(toEnd>mAlarmSec*1000)

            Intent sintentAct1 = new Intent(this, WakeAlertReceiverActSim.class);
            sintentAct1.putExtra("eveid", cceve.getEventId());
            //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
            sintentAct1.putExtra("holdsec", L1_HOLD_SEC);
            PendingIntent pendingIntentAct1 = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000-L1_OFFSET, sintentAct1, PendingIntent.FLAG_UPDATE_CURRENT);
            
            mAlarmManager.cancel(pendingIntentAct);
            mAlarmManager.cancel(pendingIntentAct1);
        }
    }
    
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(mOp.equals("set")){
                if(mCancelOld)
                    cancelEveAlarm();
                setEveAlarm();
            }
            else if(mOp.equals("cancel")){
                cancelEveAlarm();
            }
            stopSelf();
        }
    }
    
}

