package com.futcore.restaurant;

import android.app.Service;

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

import com.futcore.restaurant.testsensor.TestSensorActivity;

public class SendEventNotiService extends Service
{
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private List<ManEventWear> eventWearModel = new ArrayList<ManEventWear>();

    Notification.Builder mBuilder = null;
    NotificationManager mNotifyMgr = null;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    private int mMainIndex = 0;

    private boolean mIsFake = false;

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
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

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
            //            if(eventWearModel.size()>0){

            if(eventWearModel.size()==0){
                //                eventWearModel.add(new ManEventWear());
                sendNotification("No Event", 1000*60*60*24, NOTI_NORMAL);
            }
            else{
                ManEventWear tevent = eventWearModel.get(mMainIndex);
                long ttdate = tevent.getEstEndTime() - new Date().getTime();

                sendNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_NORMAL);
                
            }
                //            }
            
            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        
        super.onCreate();
        getDao();
        
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEvent.UPDATETIME_FIELD_NAME,0);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEvent.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.DELETE_FIELD_NAME, (byte)0);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEvent.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.DELETE_FIELD_NAME, (byte)1);
            
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            eventWearModel = eventWearDao.query(preparedQuery1);

            List<ManEventWear> passwdEveWears = new ArrayList<ManEventWear>();
            
            int indd = 0;
            
            for(ManEventWear manevew11:eventWearModel){
                if(manevew11.getEstEndTime()<new Date().getTime()){
                    passwdEveWears.add(manevew11);
                    indd++;
                }
                else
                    break;
            }
            
            eventWearModel.removeAll(passwdEveWears);
            eventWearModel.addAll(passwdEveWears);

            if(eventWearModel.size()==0)
                mIsFake = true;

            initNotification();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initNotification()
    {
        if(mIsFake)
        mBuilder =
            //            new NotificationCompat.Builder(this)
            new Notification.Builder(this)                
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            //            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
            .setOngoing(true)            
            ;
        else
            mBuilder =
            //            new NotificationCompat.Builder(this)
            new Notification.Builder(this)                
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            //            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
            .setUsesChronometer(true)
            .setOngoing(true)            
            .setWhen(System.currentTimeMillis())
            ;


        // Sets an ID for the notification
        // Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
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

        if(mIsFake){
            //            mBuilder.setContentTitle(title).setColor(android.graphics.Color.CYAN);
            mBuilder.setContentTitle(title);
        }
        else{
            mBuilder.setContentTitle(title)
                //            setContentText(TimerFormat.getTimeString(duration))
                .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
                .setWhen(System.currentTimeMillis() + duration);
        }
        

        mNotifyMgr.notify(mNotificationId, addAllPages());        
        
    }

    private Notification addAllPages()
    {
        Notification.WearableExtender twoPageNotification =
            new  Notification.WearableExtender();
        List<Notification> nlist = new ArrayList<Notification>();

        int i = 0;
        //        for(ManEvent eve:eventModel){
        for(ManEventWear eve:eventWearModel){
            if(i++==mMainIndex)
                continue;

            long ccdate = eve.getEstEndTime() - new Date().getTime();
            
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

        if(!mIsFake){
            
            Intent intent0 =  new Intent(this, FinishEventActivity.class);
            intent0.putExtra("eventid", eventWearModel.get(j++).getEventId());
        
            intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
        
            twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent0));
            twoPageNotification.setContentAction(0);
            twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));

            for(Notification noti:nlist){
                //            twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.event_finish), pendingIntent));
                Intent intent = new Intent(this, FinishEventActivity.class);
                intent.putExtra("eventid", eventWearModel.get(j++).getEventId());
                //            intent.putExtra("itemid", eventModel.get(0).getManItem().getItemId());
                //            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            
                twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent));
                twoPageNotification.addPage(noti);
            }
        }
        else{
            twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));
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

        //        Intent uploadFinishIntent1 =  new Intent(this, TestSensorActivity.class);

        mBuilder.extend(twoPageNotification);
        
        return mBuilder.build();
    }
    
    public void onDestroy() {
        super.onDestroy();
    }
    
}

