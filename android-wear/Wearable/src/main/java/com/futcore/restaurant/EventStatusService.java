package com.futcore.restaurant;

import android.content.Intent;
import android.app.IntentService;
import android.app.Service;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import android.app.PendingIntent;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManEvent;

import 	java.lang.Exception;

import android.content.Context;

import android.os.CountDownTimer;

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.WearableExtender;


import android.graphics.BitmapFactory;

import com.futcore.restaurant.R;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;

import com.futcore.restaurant.util.TimerFormat;


//public class EventStatusService extends IntentService
public class EventStatusService extends Service
{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Boolean m1800 = false;
    private Boolean m180 = false;
    private Boolean m900 = false;

    private List<ManEvent> eventModel = new ArrayList<ManEvent>();

	private static Dao<ManEvent, String> eventDao = null;
    
    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotifyMgr = null;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

    private List<CountDownTimer> countdowns = new ArrayList<CountDownTimer>();

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            
            initNotification();

            final int startId = msg.arg1;

            for(ManEvent cevent:eventModel){
                final String eveTitle = cevent.getManItem().getItemName()+"!";
                
                long ccdate = new Date().getTime()-cevent.getCreateTime().getTime();
                ccdate = cevent.getEstDuration()*60*1000-ccdate;

                final long cccdate = ccdate;

                countdowns.add(new CountDownTimer(ccdate, 1000) {
                        public void onTick(long millisUntilFinished) {
                            long oriSec = millisUntilFinished;
                            millisUntilFinished /= 1000;

                            System.out.println("9999999999999999999999999999999999999999");

                            /*                            if((millisUntilFinished+1)%(60*15)==0){
                                sendNotification(eveTitle, oriSec, NOTI_NORMAL);
                                
                            }
                            
                            if(millisUntilFinished+1==60*10||millisUntilFinished+1==60*5){
                                sendNotification(eveTitle, oriSec, NOTI_DANGER);
                                
                            }

                            if(millisUntilFinished+1<180){
                                sendNotification(eveTitle, oriSec, NOTI_NORMAL);
                                }*/

                            if((millisUntilFinished+1)<1800&&millisUntilFinished+1>900){
                                if(!m1800){
                                    sendNotification(eveTitle, oriSec, NOTI_NORMAL);
                                    m1800 = true;
                                }
                            }
                            
                            if(millisUntilFinished+1<900&&millisUntilFinished+1>180){
                                if(!m900){
                                    sendNotification(eveTitle, oriSec, NOTI_DANGER);
                                    m900 = true;
                                }
                            }

                            if(millisUntilFinished+1<180){
                                if(!m180){
                                    sendNotification(eveTitle, oriSec, NOTI_DANGER);
                                    m180 = true;
                                }
                            }
                            
                        }

                        public void onFinish() {
                            //                eventCountDown.setText("done!");
                        }
                    }.start());
            }
            
            //            sendNotification("hahaha", "wawawawa");

            //            final String eveTitle = "test title";


            //        new CountDownTimer(ccdate, 1000) {
            /*            new CountDownTimer(920*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    millisUntilFinished /= 1000;


                    if((millisUntilFinished+1)%(60*15)==0){
                        sendNotification(eveTitle, String.format("%02d:%02d:%02d", millisUntilFinished / 3600,(millisUntilFinished % 3600) / 60, (millisUntilFinished % 60))+" left.");

                    }

                    if(millisUntilFinished+1==60*10||millisUntilFinished+1==60*5){
                        sendNotification(eveTitle, String.format("%02d:%02d:%02d", millisUntilFinished / 3600,(millisUntilFinished % 3600) / 60, (millisUntilFinished % 60))+" left.");
                    }
                    
                }

                public void onFinish() {
                    //                eventCountDown.setText("done!");
                }
            }.start();
            */
          
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            //          long endTime = System.currentTimeMillis() + 5*1000;
            //          while (System.currentTimeMillis() < endTime) {
            //              synchronized (this) {
            //                  try {
            //                      wait(endTime - System.currentTimeMillis());
            //                  } catch (Exception e) {
            //                  }
            //              }
            //          }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //          stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk1111111111111111111111111111");
        
        clearAllCountDowns();
        
        if(intent!=null){
            if(intent.getSerializableExtra("events")!=null)
                eventModel = (ArrayList<ManEvent>)intent.getSerializableExtra("events");
        }
        else{
            getDao();

            try{
                QueryBuilder<ManEvent, String> queryBuilder = eventDao.queryBuilder();
                queryBuilder.orderBy(ManEvent.ID_FIELD_NAME, false).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                eventModel = eventDao.query(preparedQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        //        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk2222222222");
        //        System.out.println(eventModel.get(0).getManItem().getItemName());
        //        System.out.println(eventModel.get(0).getManItem().getItemName());
        
        
        //        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
        //        return START_NOT_STICKY;
    }
    


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        //    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&sssssssssstop");
        
        super.onDestroy();

        //        for(CountDownTimer cd:countdowns){
        //            cd.cancel();
        //        }
        clearAllCountDowns();
        
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        
    }

    private void clearAllCountDowns()
    {
        for(CountDownTimer cd:countdowns){
            cd.cancel();
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }
        countdowns = new ArrayList<CountDownTimer>();        
    }
    
    
    /*    public EventStatusService()
          {
          super("EventStatusService");
          }
    */
    
    /*    @Override
          protected void onHandleIntent(Intent intent) {
          //        System.out.println("$$$$$$$$$$###########@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
          initNotification();
          sendNotification("hahaha", "wawawawa");

          final String eveTitle = "test title";

          System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE111111111111111111111");

          //        new CountDownTimer(ccdate, 1000) {
          new CountDownTimer(904*1000, 1000) {
          public void onTick(long millisUntilFinished) {
          millisUntilFinished /= 1000;

          System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

          if((millisUntilFinished+1)%(60*15)==0){
          sendNotification(eveTitle, String.format("%02d:%02d:%02d", millisUntilFinished / 3600,(millisUntilFinished % 3600) / 60, (millisUntilFinished % 60))+" left.");

          }

          if(millisUntilFinished+1==60*10||millisUntilFinished+1==60*5){
          sendNotification(eveTitle, String.format("%02d:%02d:%02d", millisUntilFinished / 3600,(millisUntilFinished % 3600) / 60, (millisUntilFinished % 60))+" left.");
          }
                    
          }

          public void onFinish() {
          //                eventCountDown.setText("done!");
          }
          }.start();
          }
    */
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void initNotification()
    {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        
        mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
            .setUsesChronometer(true)
            .setOngoing(true)            
            .setWhen(System.currentTimeMillis())
            //            .addAction(R.drawable.ic_cc_alarm, getString(R.string.event_finish), pendingIntentRestart)
            //            .addAction(R.drawable.ic_cc_alarm, getString(R.string.view_all), null)
            //            .addAction(R.drawable.ic_cc_alarm, getString(R.string.event_finish), null)
            .addAction(R.drawable.ic_cc_alarm, getString(R.string.event_finish), pendingIntent)
            //            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spiderspider))
            //            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.shanghai11))
            ;

        //LED
        //        builder.setLights(Color.RED, 3000, 3000);

        //Ton
        //        builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));        

        // Sets an ID for the notification
        // Gets an instance of the NotificationManager service
        mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
    }

    private void sendNotification(String title, String conStr, int type)
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
        
        //    public static final int NOTI_DANGER = 1;
        //    public static final int NOTI_ERGENT = 2;
        //    public static final int NOTI_NORMAL = 3;
        //    public static final int NOTI_LESS_IMPORTANT = 4;
        
        mBuilder.setContentTitle(title).
            setContentText(conStr);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());        
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
        
        //    public static final int NOTI_DANGER = 1;
        //    public static final int NOTI_ERGENT = 2;
        //    public static final int NOTI_NORMAL = 3;
        //    public static final int NOTI_LESS_IMPORTANT = 4;
        
        mBuilder.setContentTitle(title).
            //            setContentText(TimerFormat.getTimeString(duration))
            setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
            .setWhen(System.currentTimeMillis() + duration);

        // Create a big text style for the second page
        /*        BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
            .bigText("A lot of text...");

        // Create second page notification
        Notification secondPageNotification =
            new NotificationCompat.Builder(this)
            .setStyle(secondPageStyle)
            .build();

        // Add second page with wearable extender and extend the main notification
        Notification twoPageNotification =
            new WearableExtender()
            .addPage(secondPageNotification)
            .addPage(secondPageNotification)
            .extend(mBuilder)
            .build();
        */

        // Issue the notification
        //        notificationManager = NotificationManagerCompat.from(this);
        //        notificationManager.notify(notificationId, twoPageNotification);
        
        //        mNotifyMgr.notify(mNotificationId, twoPageNotification);
        //        Notification twoPageNotification = addAllPages().build();
        mNotifyMgr.notify(mNotificationId, addAllPages());        
        
        //        mNotifyMgr.notify(mNotificationId, mBuilder.build());        
    }
    
    

    private Notification addAllPages()
    {
        WearableExtender twoPageNotification =
            new WearableExtender();
        
        for(ManEvent eve:eventModel){

            long ccdate = new Date().getTime()-eve.getCreateTime().getTime();
            ccdate = eve.getEstDuration()*60*1000-ccdate;
                        
            Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                //                .setSmallIcon(R.drawable.spider_icon)
                //                .setVibrate(new long[] { 1000, 1000, 1000})
                //                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
                //                .setUsesChronometer(true)
                .setContentTitle(eve.getManItem().getItemName())
                .setUsesChronometer(true)
                .setOngoing(true)            
                .setWhen(System.currentTimeMillis() + ccdate)
                .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
                
                ///                .setStyle(secondPageStyle)
                .build();

            twoPageNotification.addPage(secondPageNotification);
        }
        
        return twoPageNotification.extend(mBuilder).build();
        
        //        return twoPageNotification.build();

        // Add second page with wearable extender and extend the main notification
        /*        Notification twoPageNotification =
            new WearableExtender()
            .addPage(secondPageNotification)
            .addPage(secondPageNotification)
            .extend(mBuilder)
            .build();
        */
        
    }
    
}

