package com.futcore.restaurant;

import android.app.Service;

//import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.app.PendingIntent;

import android.net.Uri;

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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


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


public class LongMonitorService extends Service implements SensorEventListener
{
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private List<ManEventWear> eventWearModel = new ArrayList<ManEventWear>();
    private List<ManEventWear> lEventWearModel = new ArrayList<ManEventWear>();

    private List<ManEventWear> efeEventWearModel = new ArrayList<ManEventWear>();



    

    Notification.Builder mBuilder = null;
    NotificationManager mNotifyMgr = null;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    private int mMainIndex = 0;
    

    private ScheduledExecutorService mRefreshRecoExecutor;
    private ScheduledFuture<?> mRefreshRecoFuture;

    private ScheduledExecutorService mRefreshPlanExecutor;
    private ScheduledFuture<?> mRefreshPlanFuture;

    private ScheduledExecutorService mAlertExecutor;
    private ScheduledFuture<?> mAlertFuture;

    

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mLastTime = 0;
    private boolean mUp = false;

    private int mType;


    private static final long TIME_THRESHOLD_NS = 2000000000; // in nanoseconds (= 2sec)
    private static final float GRAVITY_THRESHOLD = 7.0f;
    

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||eventWearDao==null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
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
        getDao();
        initNotification();

        //sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff111qqq");

        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            //            if (Log.isLoggable(TAG, Log.DEBUG)) {
            //                Log.d(TAG, "Successfully registered for the sensor updates");
            //            }
        }
        
        
        // Start up the thread runing the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        //        mRefreshRecoExecutor =  new ScheduledThreadPoolExecutor(1);
        //        mRefreshRecoFuture = mRefreshRecoExecutor.scheduleWithFixedDelay(new RefreshRecoGenerator(), 1, 5, TimeUnit.MINUTES);


        
        //        WakefulIntentService.scheduleAlarms(new AlertListener(), this, false);
        
        //        Intent sintent = new Intent(this, AlertReceiver.class);
        Intent sintent = new Intent(this, WakeAlertReceiver.class);
        //        Intent sintent = new Intent("FakeAlert");
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, sintent, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, sintent, 0);
 
        //        int alarmType = AlarmManager.;
        //        final int FIFTEEN_SEC_MILLIS = 1000*60;
        final int FIFTEEN_SEC_MILLIS = 1000*120;
        //        final int FIFTEEN_SEC_MILLIS = 1000*5;
        //        final int FIFTEEN_SEC_MILLIS = 1000*10;
 
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+FIFTEEN_SEC_MILLIS, FIFTEEN_SEC_MILLIS, pendingIntent);
        

        /*        Intent sintent = new Intent(this, AlertService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, sintent, 0);
 
        int alarmType = AlarmManager.ELAPSED_REALTIME;
        final int FIFTEEN_SEC_MILLIS = 5000;
 
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS, FIFTEEN_SEC_MILLIS, pendingIntent);
        */

        //        mAlertExecutor =  new ScheduledThreadPoolExecutor(1);
        //        mAlertFuture = mAlertExecutor.scheduleWithFixedDelay(new AlertGenerator(), 1, 5, TimeUnit.SECONDS);

        //        mRefreshPlanExecutor =  new ScheduledThreadPoolExecutor(1);
        //        mRefreshPlanFuture = mRefreshPlanExecutor.scheduleWithFixedDelay(new RefreshPlanGenerator(), 1, 1, TimeUnit.MINUTES);
        //        mRefreshPlanFuture = mRefreshPlanExecutor.scheduleWithFixedDelay(new RefreshPlanGenerator(), 1, 10, TimeUnit.SECONDS);
    }
    
    public void onDestroy() {
        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddkkkkkkkkkkkkkkkkkkkkkkkk111111");
        System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddkkkkkkkkkkkkkkkkkkkkkkkk111111");
        mSensorManager.unregisterListener(this);
        super.onDestroy();
        //        mRefreshRecoFuture.cancel(true);
        //        mRefreshPlanFuture.cancel(true);
    }

    private class RefreshRecoGenerator implements Runnable {
        //        private int count = 0;

        @Override
        public void run() {
            Intent intent = new Intent(LongMonitorService.this, SendForceRecoNotiService.class);
            //            stopService(intent);
            startService(intent);
        }
    }


    private class RefreshPlanGenerator implements Runnable {
        //        private int count = 0;
        private List<ManEventWear> planEventWearModel = new ArrayList<ManEventWear>();
        private List<String> planSentList = new ArrayList<String>();

        
        @Override
        public void run() {
            updatePlanEventWearModel();
            System.out.println(".............................................................................................refresh plan:  "+planEventWearModel.size());
            for(ManEventWear pevew:planEventWearModel){
                long startt = pevew.getStartTime();
                System.out.println("!!!!........................................................................"+(startt-new Date().getTime()));
            }
            //            Intent intent = new Intent(LongMonitorService.this, SendForcePlanNotiService.class);
            //            stopService(intent);
            //            startService(intent);
        }

        private void updatePlanEventWearModel()
        {
            try{
                QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
                //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
                //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.CREATETIME_FIELD_NAME, ManEventWear.STARTTIME_FIELD_NAME);
                queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.CREATETIME_FIELD_NAME, ManEventWear.STARTTIME_FIELD_NAME);
                PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
                planEventWearModel = eventWearDao.query(preparedQuery1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        
        }
    }
    
    private class AlertGenerator implements Runnable {
        @Override
        public void run() {
            //            updateEventWearModel();
            updateEfeEventWearModel();
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.................................................");
            System.out.println(efeEventWearModel.size());

            //            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            //            v.vibrate(3000);            
            
            if(efeEventWearModel.size()==1){
                if((efeEventWearModel.get(0).getEstEndTime()-new Date().getTime())<90*1000)
                    System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr!!!!!!!!!!!!!!!");
            }
        }
    }
    

    private void initNotification()
    {
        
        mBuilder =
            new Notification.Builder(this)                
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            .setUsesChronometer(true)
            .setOngoing(true)            
            .setWhen(System.currentTimeMillis())
            ;

        mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void sendNotification(String title, long duration, int type)
    {
        int mNotificationId = 001;
        //        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        
        switch(type){
        case NOTI_DANGER:
            mBuilder.setVibrate(new long[] { 1000, 2000, 1000});
            break;
        case NOTI_ERGENT:
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000});
            break;
        case NOTI_NORMAL:
            mBuilder.setVibrate(new long[] { 1000, 500, 1000});
            break;
        case NOTI_LESS_IMPORTANT:
            mBuilder.setVibrate(new long[] { 1000, 300, 1000});
            break;
        default:
            ;
        }
        
        
        mBuilder.setContentTitle(title)
            .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
            .setWhen(System.currentTimeMillis() + duration);
        mNotifyMgr.notify(mNotificationId, addAllPages());
    }


    private Notification addAllPages()
    {
        Notification.WearableExtender twoPageNotification =
            new  Notification.WearableExtender();
        List<Notification> nlist = new ArrayList<Notification>();

        int i = 0;
        for(ManEventWear eve:eventWearModel){
            if(i++==mMainIndex)
                continue;

            long ccdate = eve.getEstEndTime() - new Date().getTime();
            
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaai"+i);

            Notification secondPageNotification =
                //                new NotificationCompat.Builder(this)
                new Notification.Builder(this)                
                .setContentTitle(eve.getManItem().getItemName())
                .setUsesChronometer(true)
                .setOngoing(true)            
                .setWhen(System.currentTimeMillis() + ccdate)
                .extend(new Notification.WearableExtender()
                        .setContentAction(i-1))
                        //                        .setContentAction(i-1))
                .build();
            
            nlist.add(secondPageNotification);
        }

        int j = 0;
        
            
        Intent intent0 =  new Intent(this, FinishEventActivity.class);
        intent0.putExtra("eventid", eventWearModel.get(j++).getEventId());
        
        intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent0));
        twoPageNotification.setContentAction(0);
        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));


        for(Notification noti:nlist){
            Intent intent = new Intent(this, FinishEventActivity.class);
            intent.putExtra("eventid", eventWearModel.get(j++).getEventId());
            
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            
            twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent));
            twoPageNotification.addPage(noti);
        }

        Intent uploadFinishIntent2 =  new Intent(this, AllRecosActivity.class);
        PendingIntent pendingUploadFinishIntent2 = PendingIntent.getActivity(this, 0, uploadFinishIntent2, 0);
        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.allreco_text), pendingUploadFinishIntent2));
        

        Intent uploadFinishIntent1 =  new Intent(this, HistoryEventsActivity.class);
        PendingIntent pendingUploadFinishIntent1 = PendingIntent.getActivity(this, 0, uploadFinishIntent1, 0);

        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.history_text), pendingUploadFinishIntent1));
        

        Intent uploadFinishIntent =  new Intent(this, UploadFinishActivity.class);
        PendingIntent pendingUploadFinishIntent = PendingIntent.getActivity(this, 0, uploadFinishIntent, 0);

        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.upload_finish), pendingUploadFinishIntent));
        

        mBuilder.extend(twoPageNotification);
        
        return mBuilder.build();
        
    }


    private void updateEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.DELETE_FIELD_NAME, (byte)0);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.DELETE_FIELD_NAME, (byte)1);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();

            lEventWearModel = eventWearModel;
            eventWearModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    private void updateEfeEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.CREATETIME_FIELD_NAME, ManEventWear.STARTTIME_FIELD_NAME);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().isNotNull(ManEventWear.STARTTIME_FIELD_NAME).and().gt(ManEventWear.EST_END_TIME_FIELD_NAME, new Date().getTime());
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            
            efeEventWearModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onSensorChanged(SensorEvent event) {
        //        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff22222222222222222222222222");
        //        detectJump(event.values[0], event.timestamp);
        detectJump(event.values[1], event.timestamp);
        //        detectJump1(event.values[1], event.timestamp);
        //        detectJump2(event.values[2], event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void detectJump(float xValue, long timestamp) {
        //        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff22222222222223333");
        if ((Math.abs(xValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (xValue > 0)) {
                onJumpDetected(!mUp);
            }
            mUp = xValue > 0;
            mLastTime = timestamp;
        }
    }

    private void onJumpDetected(boolean up) {
        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2222222225555555555");
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        //        mJumpCounter++;
        //        setCounter(mJumpCounter);
        detectRotate();
        //        renewTimer();
    }

    private void detectRotate()
    {
        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff2222666666666666666666666666");
        //confirmFinish();
        //        chooseEvent(mEstDurSec);
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz==========================");
        /*        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);         
        callIntent.setData(Uri.parse("tel:18516211115"));
        startActivity(callIntent);
        */

        //        sendNotification("Go home", 2000, NOTI_NORMAL);

        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.spider_icon)
            .setContentTitle("Go Home!")
            .setContentText("Today is Friday");

        int mNotificationId1 = 001;
        NotificationManager mNotifyMgr1 = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr1.notify(mNotificationId1, mBuilder.build());        
    }


    /*    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            //            if (Log.isLoggable(TAG, Log.DEBUG)) {
            //                Log.d(TAG, "Successfully registered for the sensor updates");
            //            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        //        if (Log.isLoggable(TAG, Log.DEBUG)) {
        //            Log.d(TAG, "Unregistered for sensor events");
        //        }
    }
    */
    
    
}

