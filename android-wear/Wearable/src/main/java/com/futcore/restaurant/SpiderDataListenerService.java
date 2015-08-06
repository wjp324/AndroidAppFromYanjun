package com.futcore.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

//import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Comparator;
import java.util.Collections;

import android.app.PendingIntent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.common.api.ResultCallback;

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
//import android.app.Notification.BigTextStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.WearableExtender;


import android.graphics.BitmapFactory;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import java.io.StreamCorruptedException;

import com.futcore.restaurant.models.*;

import android.os.CountDownTimer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.futcore.restaurant.util.TimerFormat;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import android.app.AlarmManager;

import com.futcore.restaurant.testsensor.TestSensorActivity;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class SpiderDataListenerService extends WearableListenerService {

    private ScheduledExecutorService eventMonitorExecutor;
    private ScheduledFuture<?> eventMonitorFuture;

    //    NotificationCompat.Builder mBuilder = null;
    Notification.Builder mBuilder = null;
    Notification.Builder mRecoBuilder = null;
    
    NotificationManager mNotifyMgr = null;
    NotificationManager mRecoNotifyMgr = null;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    private int mMainIndex = 0;
    
    private List<ManEvent> eventModel = new ArrayList<ManEvent>();
    private List<ManEventWear> eventWearModel = new ArrayList<ManEventWear>();
    private List<ManEventWear> lEventWearModel = new ArrayList<ManEventWear>();
    
    private List<ManRecoItemWear> recoItemWearModel = new ArrayList<ManRecoItemWear>();
    private List<ManRecoItemWear> curRecoItemWearModel = new ArrayList<ManRecoItemWear>();
    private List<ManRecoItem> recoItemModel = new ArrayList<ManRecoItem>();
    
    public Map<Long, List<ManItemScore>> mRecos = new HashMap<Long, List<ManItemScore>>();

    private List<ManEventWear> eventWearFinishModel = new ArrayList<ManEventWear>();
    
    private List<CountDownTimer> countdowns = new ArrayList<CountDownTimer>();
    
    private static final String TAG = "DataLayerListenerServic";

    private static final String FINISH_PATH = "/finish";
    private static final String FINISH_KEY = "finish";

    private static final String RECO_ITEM_PATH = "/recoitem";
    private static final String RECO_ITEM_KEY = "recoitem";

    private static final String SEND_RECO_PATH = "/sendreco";
    private static final String SEND_RECO_KEY = "sendreco";
    

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    public static final String COUNT_PATH = "/count";
    public static final String IMAGE_PATH = "/image";
    public static final String IMAGE_KEY = "photo";
    private static final String COUNT_KEY = "count";
    private static final int MAX_LOG_TAG_LENGTH = 23;
    GoogleApiClient mGoogleApiClient;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

	private static Dao<ManRecoWrapper, Long> recoWrapperDao = null;
    

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao == null||itemDao == null||eventWearDao == null||recoItemWearDao == null||recoWrapperDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
                recoItemWearDao = helper.getRecoItemWearDao();
                recoWrapperDao = helper.getRecoWrapperDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAllCountDowns()
    {
        for(CountDownTimer cd:countdowns){
            cd.cancel();
        }
        countdowns = new ArrayList<CountDownTimer>();
    }

    private void initNotification()
    {
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        mBuilder =
            //            new NotificationCompat.Builder(this)
            new Notification.Builder(this)                
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            //            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
            .setUsesChronometer(true)
            .setOngoing(true)            
            .setWhen(System.currentTimeMillis())
            //            .addAction(R.drawable.ic_cc_alarm, getString(R.string.event_finish), pendingIntentRestart)
            //            .addAction(R.drawable.ic_full_sad, getString(R.string.view_all), null)
            /*            .extend(new Notification.WearableExtender()
                          .addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.event_finish), pendingIntent))
                          .addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.event_finish), pendingIntent))
                          .addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.event_finish), pendingIntent))
                          )
            */
            
            //            .extend(new Notification.WearableExtender()
            //                    .setContentAction(0))
            
            //            .setContentIntent(pendingIntent)
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

    private void initRecoNotification()
    {
        
        mRecoBuilder =
            new Notification.Builder(this)                
            .setSmallIcon(R.drawable.spider_icon)
            .setVibrate(new long[] { 1000, 1000, 1000})
            //            .setUsesChronometer(true)
            .setOngoing(true)            
            //            .setWhen(System.currentTimeMillis())
            ;

        mRecoNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    

    private Notification addAllPages()
    {
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, FinishEventActivity.class), 0);

        //        WearableExtender()        
        Notification.WearableExtender twoPageNotification =
            new  Notification.WearableExtender();
        List<Notification> nlist = new ArrayList<Notification>();

        int i = 0;
        //        for(ManEvent eve:eventModel){
        for(ManEventWear eve:eventWearModel){
            if(i++==mMainIndex)
                continue;
            //            long ccdate = new Date().getTime()-eve.getCreateTime().getTime();
            //            ccdate = eve.getEstDuration()*60*1000-ccdate;

            //            long ccdate = eve.getEstEndTime().getTime() - new Date().getTime();
            long ccdate = eve.getEstEndTime() - new Date().getTime();
            
            /*            Notification secondPageNotification =
                          new NotificationCompat.Builder(this)
                          //                .setSmallIcon(R.drawable.spider_icon)
                          //                .setVibrate(new long[] { 1000, 1000, 1000})
                          //                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
                          //                .setUsesChronometer(true)
                          .setContentTitle(eve.getManItem().getItemName())
                          .setUsesChronometer(true)
                          .setOngoing(true)            
                          .setWhen(System.currentTimeMillis() + ccdate)
                          ///                .setStyle(secondPageStyle)
                          .build();
            */

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
            //            twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420)).addPage(secondPageNotification);
            //            twoPageNotification.addPage(secondPageNotification).setContentAction(0 /* action A */);
            //            twoPageNotification.addPage(secondPageNotification);
            //            mBuilder.addPage(secondPageNotification);
            //            twoPageNotification.addPage(secondPageNotification);
        }

        //        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.event_finish), pendingIntent));
        int j = 0;
        
            
        Intent intent0 =  new Intent(this, FinishEventActivity.class);
        intent0.putExtra("eventid", eventWearModel.get(j++).getEventId());
        
        intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent0));
        twoPageNotification.setContentAction(0);
        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));
        
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh777777777777777777777777777777777777777777777777777777777777");

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
        

        Intent uploadFinishIntent2 =  new Intent(this, AllRecosActivity.class);
        PendingIntent pendingUploadFinishIntent2 = PendingIntent.getActivity(this, 0, uploadFinishIntent2, 0);
        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.allreco_text), pendingUploadFinishIntent2));
        

        //        Intent uploadFinishIntent1 =  new Intent(this, TestSensorActivity.class);
        Intent uploadFinishIntent1 =  new Intent(this, HistoryEventsActivity.class);
        PendingIntent pendingUploadFinishIntent1 = PendingIntent.getActivity(this, 0, uploadFinishIntent1, 0);

        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.history_text), pendingUploadFinishIntent1));
        

        Intent uploadFinishIntent =  new Intent(this, UploadFinishActivity.class);
        PendingIntent pendingUploadFinishIntent = PendingIntent.getActivity(this, 0, uploadFinishIntent, 0);

        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.upload_finish), pendingUploadFinishIntent));

        mBuilder.extend(twoPageNotification);
        
        //        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spider_bgb));
        //        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));
        //        mBuilder.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderred));

        //        Intent actionIntent = new Intent(this, MainActivity.class);
        //        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the action
        //        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_action, getString(R.string.label, actionPendingIntent)).build();
        //        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_cc_alarm, getString(R.string.event_finish, actionPendingIntent)).build();

        /*        NotificationCompat.Action action =
                  new NotificationCompat.Action.Builder(R.drawable.ic_full_sad,
                  getString(R.string.event_finish), actionPendingIntent)
                  .build();
        */

        
        //        return twoPageNotification.extend(mBuilder).addAction(action).build();
        //        return twoPageNotification.extend(mBuilder).build();
        return mBuilder.build();
        
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



    private Notification addAllRecoPages()
    {
        Notification.WearableExtender twoPageNotification =
            new  Notification.WearableExtender();
        List<Notification> nlist = new ArrayList<Notification>();

        int i = 0;
        //        for(ManEventWear eve:eventWearModel){
        for(ManRecoItemWear riw:curRecoItemWearModel){
            if(i++==mMainIndex)
                continue;
            //            long ccdate = eve.getEstEndTime() - new Date().getTime();

            Notification secondPageNotification =
                //                new NotificationCompat.Builder(this)
                new Notification.Builder(this)                
                .setContentTitle(riw.getManItem().getItemName())
                .setContentText("Test")
                //                .setUsesChronometer(true)
                .setOngoing(true)            
                //                .setWhen(System.currentTimeMillis() + ccdate)
                .extend(new Notification.WearableExtender()
                        .setContentAction(i-1))
                //                        .setContentAction(i-1))
                .build();
            
            nlist.add(secondPageNotification);
        }

        int j = 0;
        
            
        //        Intent intent0 =  new Intent(this, FinishEventActivity.class);
        Intent intent0 =  new Intent(this, ConfirmRecoActivity.class);
        intent0.putExtra("recowear", curRecoItemWearModel.get(j++));
        
        intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent0));
        twoPageNotification.setContentAction(0);
        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spiderbb2));

        for(Notification noti:nlist){
            //            Intent intent = new Intent(this, FinishEventActivity.class);
            Intent intent = new Intent(this, ConfirmRecoActivity.class);
            intent.putExtra("recowear", curRecoItemWearModel.get(j++));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            
            twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent));
            twoPageNotification.addPage(noti);
        }


        Intent intentss = new Intent(this, SendRecoNotiService.class);
        PendingIntent pendingIntentss = PendingIntent.getService(this, 0, intentss, 0);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.update_reco), pendingIntentss));

        mRecoBuilder.extend(twoPageNotification);
        
        return mRecoBuilder.build();
    }
    
    private void sendNotification(String title, long duration, int type)
    {
        int mNotificationId = 001;
        //        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        
        switch(type){
        case NOTI_DANGER:
            //            mBuilder.setVibrate(new long[] { 1000, 2000, 1000});
            mBuilder.setVibrate(new long[] { 0, 2000, 1000});
            break;
        case NOTI_ERGENT:
            mBuilder.setVibrate(new long[] { 0, 1000, 1000});
            break;
        case NOTI_NORMAL:
            mBuilder.setVibrate(new long[] { 0, 500, 1000});
            break;
        case NOTI_LESS_IMPORTANT:
            mBuilder.setVibrate(new long[] { 0, 300, 1000});
            break;
        default:
            ;
        }
        
        //    public static final int NOTI_DANGER = 1;
        //    public static final int NOTI_ERGENT = 2;
        //    public static final int NOTI_NORMAL = 3;
        //    public static final int NOTI_LESS_IMPORTANT = 4;
        
        mBuilder.setContentTitle(title)
            //            setContentText(TimerFormat.getTimeString(duration))
            .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
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

    private void sendRecoNotification(String title, int type)
    {
        int mNotificationId = 002;
        
        switch(type){
        case NOTI_DANGER:
            mRecoBuilder.setVibrate(new long[] { 1000, 2000, 1000});
            break;
        case NOTI_ERGENT:
            mRecoBuilder.setVibrate(new long[] { 1000, 1000, 1000});
            break;
        case NOTI_NORMAL:
            mRecoBuilder.setVibrate(new long[] { 1000, 500, 1000});
            break;
        case NOTI_LESS_IMPORTANT:
            mRecoBuilder.setVibrate(new long[] { 1000, 300, 1000});
            break;
        default:
            ;
        }
        
        mRecoBuilder.setContentTitle(title)
            .setContentText("Test");
        //            .setWhen(System.currentTimeMillis() + duration);

        mRecoNotifyMgr.notify(mNotificationId, addAllRecoPages());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        initNotification();
        initRecoNotification();
        
        getDao();
        
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .build();
        mGoogleApiClient.connect();

        eventMonitorExecutor =  new ScheduledThreadPoolExecutor(1);

        
        //        eventMonitorFuture = eventMonitorExecutor.scheduleWithFixedDelay(
        //                new MonitorEvent(), 1, 10, TimeUnit.SECONDS);
    }
    
    @Override
    public void onDestroy() {
        //        eventMonitorFuture.cancel(true);
        super.onDestroy();
    }
    

    /*    private class MonitorEvent implements Runnable
          {
          @Override
          public void run() {
          updateEventWearModel();

          System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");

          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          ObjectOutput out = null;

          try{
                
          out = new ObjectOutputStream(bos);
          //                    ManEvent testEve = eventModel.get(0);
          //                    testEve.setEventScore(count++);
                
          //                    out.writeObject(testEve);
          List<ManEvent> eventWearFinishModelConvert = new ArrayList<ManEvent>();
          for(ManEventWear manwear:eventWearFinishModel){
          eventWearFinishModelConvert.add(manwear.toManEvent());
          }

          System.out.println("fffffffffffffffffffffffffffffffffiiiiiiiiiiiiiiiinnnnnnnnnnnnnn");
          System.out.println(eventWearModel.size());
          System.out.println(eventWearFinishModelConvert.size());
                
          out.writeObject(eventWearFinishModelConvert);
          byte[] yourBytes = bos.toByteArray();

          //                    System.out.println("cccccccccccccccccccccccckkkkkkkkkkkkkkkkkkkkkk");

          PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(FINISH_PATH);
          //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
          putDataMapRequest.getDataMap().putByteArray(FINISH_KEY, yourBytes);
                
          PutDataRequest request = putDataMapRequest.asPutDataRequest();
                    
          if (!mGoogleApiClient.isConnected()) {
          return;
          }

                
          System.out.println("fffffffffffffffffffffffffffffffffiiiiiiiiiiiiiiiinnnnnnnnnnnnnn11111111111111111111111111111");
                
          Wearable.DataApi.putDataItem(mGoogleApiClient, request)
          .setResultCallback(new ResultCallback<DataItemResult>() {
          @Override
          public void onResult(DataItemResult dataItemResult) {
          if (!dataItemResult.getStatus().isSuccess()) {
          //                                        Log.e(TAG, "ERROR: failed to putDataItem, status code: "
          //                                              + dataItemResult.getStatus().getStatusCode());
          }
          }
          });
                
                
          }
          catch(IOException e){
          e.printStackTrace();
          }
          finally{
                
          }
            
            
          if(simpleCheckEveFinish()){

          updateEventWearFinishModel();
                
          ManEventWear tevent = eventWearModel.get(mMainIndex);
          long ttdate = tevent.getEstEndTime() - new Date().getTime();

          sendNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_NORMAL);





          String mEventId = simpleGetFinishEventId();

          PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(FINISH_PATH);
          //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
          //            Random rand= new Random();
          putDataMapRequest.getDataMap().putString(FINISH_KEY, mEventId);

          PutDataRequest request = putDataMapRequest.asPutDataRequest();
            
          if (!mGoogleApiClient.isConnected()) {
          System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm5555555555555");
          return;
          }
          System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm11111188888");
          System.out.println(mEventId);
            
                    
          Wearable.DataApi.putDataItem(mGoogleApiClient, request)
          .setResultCallback(new ResultCallback<DataItemResult>() {
          @Override
          public void onResult(DataItemResult dataItemResult) {
          if (!dataItemResult.getStatus().isSuccess()) {
          System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm22222");
          System.out.println(dataItemResult.getStatus().getStatusCode());
          }
          }
          });


                    
          }
          }
        
          }
    */
    
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        
        LOGD(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "SpiderDataListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        // Loop through the events and send a message back to the node that created the data item.
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            
            String path = uri.getPath();

            if(SEND_RECO_PATH.equals(path)){

                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();

                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(SEND_RECO_KEY);

                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);
                    //                    eventModel = (List<ManEvent>)in.readObject();
                    //                    recoItemModel = (List<ManRecoItem>)in.readObject();
                    mRecos = (Map<Long, List<ManItemScore>>)in.readObject();

                    System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{"+mRecos.size());

                    try {
                        recoWrapperDao.create(new ManRecoWrapper(new Date().getTime(), (HashMap<Long, List<ManItemScore>>)mRecos));
                        //                        eventDao.create(cceve);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    RecoGeneService.clearMRecos();

                    /*                    try {
                        List<ManRecoWrapper> recoos = recoWrapperDao.queryForAll();
                        for(ManRecoWrapper rr:recoos){
                            System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}");
                            System.out.println(rr.getCTime().toString());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    */
                    

                    
                    //                    for(ManRecoItem ri:recoItemModel){
                    //                        recoItemWearModel.add(ManRecoItemWear.fromManRecoItem(ri));
                    //                    }

                    /*                    try {
                        helper.clearAllRecoItemWear();
                        //                        helper.clearAllEventWear();
                        //                        for(ManRecoItemWear riw:recoItemWearModel){
                        //                            eventDao.createOrUpdate(cceve);
                        for(ManRecoItem ri:recoItemModel){
                            ManItem ccitem = ri.getManItem();

                            //                            System.out.println(ccitem.getItemName());
                            itemDao.createIfNotExists(ccitem);
                            //                            System.out.println(cceve.getCreateTime().toString());
                            //                            System.out.println(cceve.getCreateTime().getTime());
                            //                            eventDao.create(cceve);
                            //                            eventWearDao.create(ManEventWear.fromManEvent(cceve));
                            recoItemWearDao.create(ManRecoItemWear.fromManRecoItem(ri));
                        }

                        QueryBuilder<ManRecoItemWear, Integer> queryBuilder = recoItemWearDao.queryBuilder();
                        queryBuilder.limit(6);
                        PreparedQuery<ManRecoItemWear> preparedQuery = queryBuilder.prepare();
                        curRecoItemWearModel = recoItemWearDao.query(preparedQuery);
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    */
                    
                    //                    ManEventWear tevent = eventWearModel.get(mMainIndex);
                    //                    long ttdate = tevent.getEstEndTime() - new Date().getTime();
                    //                    sendRecoNotification(curRecoItemWearModel.get(mMainIndex).getManItem().getItemName(), NOTI_LESS_IMPORTANT);

                /*                    Intent intentreco = new Intent(this, SendRecoNotiService.class);
                    startService(intentreco);
                */
                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                //                catch(StreamCorruptedException e){
                //                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                // Send the rpc
                //                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH, payload);
            }
            else if(RECO_ITEM_PATH.equals(path)){
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();


                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(RECO_ITEM_KEY);


                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);
                    //                    Object o = in.readObject();
                    //                    ManEvent o = (ManEvent)in.readObject();
                    //                    List<ManEvent> o = (List<ManEvent>)in.readObject();
                    //                    eventModel = (List<ManEvent>)in.readObject();
                    recoItemWearModel = (List<ManRecoItemWear>)in.readObject();
                    //                    Collections.sort(eventModel, new EventEndComparator());                    

                    //                    helper.clearAllItem();

                    try {
                        helper.clearAllRecoItemWear();
                        //                        helper.clearAllEventWear();
                        for(ManEvent cceve:eventModel){
                            //                            eventDao.createOrUpdate(cceve);
                            ManItem ccitem = cceve.getManItem();

                            itemDao.createIfNotExists(ccitem);
                            //                            System.out.println(cceve.getCreateTime().toString());
                            eventDao.create(cceve);
                            //                            eventWearDao.create(ManEventWear.fromManEvent(cceve));
                            eventWearDao.createIfNotExists(ManEventWear.fromManEvent(cceve));
                        }


                        /*                        QueryBuilder<ManEvent, String> queryBuilder = eventDao.queryBuilder();
                                                  queryBuilder.orderBy(ManEvent.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                                                  PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                                                  List<ManEvent> eventModel1 = eventDao.query(preparedQuery);
                        */

                        updateEventWearModel();
                        

                        /*                        for(ManEvent ccceve:eventModel1){
                        //                            eventDao.createOrUpdate(cceve);

                        System.out.println("((((((((((((((((((((((((((222222222222222222222222222222222222222222");
                            
                        ManItem cccitem = ccceve.getManItem();

                        System.out.println(cccitem.getItemName());
                        //                            System.out.println(ccceve.getCreateTime().toString());
                        System.out.println(ccceve.getCreateTime().getTime());
                        }
                        */
                        
                        
                        //                        eventModel = eventDao.query(preparedQuery);
                        
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    ManEventWear tevent = eventWearModel.get(mMainIndex);
                    
                    long ttdate = tevent.getEstEndTime() - new Date().getTime();
                    
                    //                    sendRecoNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_LESS_IMPORTANT);
                    
                    sendNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_LESS_IMPORTANT);


                }
                catch(IOException e){
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                                                payload);
                
            }
            else if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();
                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(COUNT_KEY);


                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);
                    //                    Object o = in.readObject();
                    //                    ManEvent o = (ManEvent)in.readObject();
                    //                    List<ManEvent> o = (List<ManEvent>)in.readObject();
                    eventModel = (List<ManEvent>)in.readObject();

                    System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee----------------------------------------"+eventModel.size());
                    //                    Collections.sort(eventModel, new EventEndComparator());
                    //                    helper.clearAllItem();
                    
                    Intent intentaa = new Intent(this, AlarmHolderService.class);
                    intentaa.putExtra("mop", "set");
                    //                    intentaa.putExtra("mlvl", "l2");
                    intentaa.putExtra("mlvl", "l0");
                    intentaa.putExtra("events", (ArrayList<ManEvent>)eventModel);
                    intentaa.putExtra("cancelold", true);
                    //                stopService(intent);
                    startService(intentaa);
                    /*                    Intent intentbb = new Intent(this, AlarmHolderService.class);
                                          intentbb.putExtra("mop", "set");
                                          intentbb.putExtra("mlvl", "l1");
                                          intentbb.putExtra("events", (ArrayList<ManEvent>)eventModel);
                                          intentbb.putExtra("cancelold", false);
                                          //                stopService(intent);
                                          startService(intentbb);
                    */
                    
                    try {
                        helper.clearAllEvent();
                        //                        helper.clearAllEventWear();
                        int jj = 0;
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        
                        for(ManEvent cceve:eventModel){
                            //                            eventDao.createOrUpdate(cceve);
                            ManItem ccitem = cceve.getManItem();
                            //                            System.out.println(ccitem.getItemName());
                            itemDao.createIfNotExists(ccitem);
                            //                            System.out.println(cceve.getCreateTime().toString());
                            //                            System.out.println(cceve.getCreateTime().getTime());
                            //                            eventDao.create(cceve);
                            //                            eventWearDao.create(ManEventWear.fromManEvent(cceve));
                            eventWearDao.createIfNotExists(ManEventWear.fromManEvent(cceve));

                            //                            Intent sintent = new Intent(this, WakeAlertReceiverSim.class);
                            //                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, jj++, sintent, 0);
                            //                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000, sintent, 0);
                            /*                            Intent sintentAct = new Intent(this, WakeAlertReceiverActSim.class);
                                                          sintentAct.putExtra("eveid", cceve.getEventId());
                                                          sintentAct.putExtra("holdsec", 10);
                                                          PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000, sintentAct, 0);
                            
                                                          //                            Intent sintent1 = new Intent(this, WakeAlertReceiverSimp.class);
                                                          //                            PendingIntent pendingIntent1 = PendingIntent.getBroadca)(cceve.getCreateTime().getTime()/1000)%1000000000-3*24*3600, sintent1, 0);
                            
                                                          long toEnd = cceve.getEstEndTime().getTime()-new Date().getTime();
                                                          //                            System.out.println((toEnd-5*60*1000)/1000);
                                                          //                            long estEnd = cceve.getEstEndTime().getTime();
                            
                                                          //                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10*1000, 10*1000, pendingIntent);
                                                          //                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5*1000, pendingIntent);
                                                          if(toEnd>5*60*1000)
                                                          //                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+toEnd-5*60*1000, pendingIntentAct);
                                                          alarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-5*60*1000, pendingIntentAct);
                            */
                            //                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+toEnd-5*60*1000, pendingIntent);
                            
                            //                            if(toEnd>60*1000)
                            //                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+toEnd-90*1000, pendingIntent1);
                            //                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, estEnd-5*60*1000, pendingIntent);
                        }


                        /*                        QueryBuilder<ManEvent, String> queryBuilder = eventDao.queryBuilder();
                                                  queryBuilder.orderBy(ManEvent.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                                                  PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                                                  List<ManEvent> eventModel1 = eventDao.query(preparedQuery);
                        */
                        updateEventWearModel();
                        /*                        for(ManEvent ccceve:eventModel1){
                        //                            eventDao.createOrUpdate(cceve);

                        System.out.println("((((((((((((((((((((((((((222222222222222222222222222222222222222222");
                            
                        ManItem cccitem = ccceve.getManItem();
                        m
                        System.out.println(cccitem.getItemName());
                        //                            System.out.println(ccceve.getCreateTime().toString());
                        System.out.println(ccceve.getCreateTime().getTime());
                        }
                        */
                        //                        eventModel = eventDao.query(preparedQuery);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    //                    System.out.println("0000000000000000000000000000000000000000");
                    //                    System.out.println(o.get(0).getManItem().getItemName());

                    //                    ManEvent tevent = eventModel.get(mMainIndex);

                    
                    if(eventWearModel.size()>0){
                        
                        ManEventWear tevent = eventWearModel.get(mMainIndex);
                        //                    long ttdate = new Date().getTime()-tevent.getCreateTime().getTime();
                        //                    ttdate = tevent.getEstDuration()*60*1000-ttdate;
                        //                    long ttdate = tevent.getEstEndTime().getTime() - new Date().getTime();
                        long ttdate = tevent.getEstEndTime() - new Date().getTime();
                        //                    sendRecoNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_LESS_IMPORTANT);
                        sendNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_LESS_IMPORTANT);
                    }
                    
                    //                    System.out.println("iii"+countdowns.size());
                    //                    clearAllCountDowns();
                    /*                    System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");

                                          Intent intent = new Intent(this, EventStatusService.class);
                                          intent.putExtra("events", (ArrayList<ManEvent>)eventModel);

                                          stopService(intent);
                                          startService(intent);
                    */
                    
                    /*                    for(ManEvent cevent:eventModel){
                                          final String eveTitle = cevent.getManItem().getItemName()+"!";
                                          long ccdate = new Date().getTime()-cevent.getCreateTime().getTime();
                                          ccdate = cevent.getEstDuration()*60*1000-ccdate;

                                          final long cccdate = ccdate;

                                          countdowns.add(new CountDownTimer(ccdate, 1000) {
                                          public void onTick(long millisUntilFinished) {
                                          long oriSec = millisUntilFinished;
                                          millisUntilFinished /= 1000;

                                          System.out.println("9999999999999999999999999");

                                          if((millisUntilFinished+1)%(60*15)==0){
                                          System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                                          sendNotification(eveTitle, oriSec, NOTI_NORMAL);
                                          }
                            
                                          if(millisUntilFinished+1==60*10||millisUntilFinished+1==60*5){
                                          sendNotification(eveTitle, oriSec, NOTI_DANGER);
                                
                                          }

                                          if(millisUntilFinished+1<30){
                                          sendNotification(eveTitle, oriSec, NOTI_NORMAL);
                                
                                          }
                                          }

                                          public void onFinish() {
                                          //                eventCountDown.setText("done!");
                                          }
                                          }.start());

                                          System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$5555555555555588888"+countdowns.size());
                        
                                          }
                    */
                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                //                catch(StreamCorruptedException e){
                //                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                
                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                                                payload);
            }
        }
    }

    private boolean simpleCheckEveFinish()
    {
        return (lEventWearModel.size()!=eventWearModel.size());
    }

    private String simpleGetFinishEventId()
    {
        for(ManEventWear eve:lEventWearModel){
            if(!eventWearModel.contains(eve))
                return eve.getEventId();
        }
        return null;
    }

    private void updateEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.CREATETIME_FIELD_NAME, ManEventWear.STARTTIME_FIELD_NAME);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.DELETE_FIELD_NAME, (byte)0);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.DELETE_FIELD_NAME, (byte)1);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();

            lEventWearModel = eventWearModel;
            eventWearModel = eventWearDao.query(preparedQuery1);

            List<ManEventWear> passwdEveWears = new ArrayList<ManEventWear>();
            //            List<Integer> indexes = new ArrayList<Integer>();
            
            /*            int indd = 0;
                          for(ManEventWear manevew11:eventWearModel){
                          if(manevew11.getEstEndTime()>new Date().getTime()){
                          if(indd>0){
                          eventWearModel.remove(indd);
                          eventWearModel.add(0, manevew11);
                          break;
                          }
                          }
                          indd++;
                          }
            */
            
            int indd = 0;
            
            //            List<ManEventWear> eventWearModeltmp =
            
            for(ManEventWear manevew11:eventWearModel){
                if(manevew11.getEstEndTime()<new Date().getTime()){
                    //                    if(indd>0){
                    passwdEveWears.add(manevew11);
                    //                    indexes.add(indd);
                    //                    eventWearModel.remove(indd);
                    indd++;
                    //                        eventWearModel.add(0, manevew11);
                    //                        break;
                    //                    }
                }
                else
                    break;
            }
            
            //            eventWearModel = ArrayUtils.addAll(eventWearModel, passwdEveWears);
            //            for(Integer ind1:indexes){
            //                eventWearModel.remove(ind1);
            //            }
            
            eventWearModel.removeAll(passwdEveWears);
            eventWearModel.addAll(passwdEveWears);
            /*            for(ManEventWear manevew11:eventWearModel){
                          if(manevew11.getEstEndTime()<new Date().getTime()){
                          mMainIndex++;
                          }
                          else
                          break;
                          }
            */
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateEventWearFinishModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().ne(ManEvent.UPDATETIME_FIELD_NAME,0);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            
            eventWearFinishModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    private void sortEventsByEnd(List<ManEvent> eves)
    {
    }
    
    public class EventEndComparator implements Comparator<ManEvent> {
        @Override
        public int compare(ManEvent eve1, ManEvent eve2) {
            return eve1.getEstEndTime().compareTo(eve2.getEstEndTime());
        }
    }    

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        /*        LOGD(TAG, "onMessageReceived: " + messageEvent);
        // Check to see if the message is to start an activity
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
        }
        */
    }

    @Override
    public void onPeerConnected(Node peer) {
        LOGD(TAG, "onPeerConnected: " + peer);
    }
    
    @Override
    public void onPeerDisconnected(Node peer) {
        LOGD(TAG, "onPeerDisconnected: " + peer);
    }

    public static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }
}
