package com.futcore.restaurant;

import android.app.Service;
import android.app.PendingIntent;

import java.sql.SQLException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManEventWear;
import com.futcore.restaurant.models.ManCertPlace;

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

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
//import android.app.Notification.BigTextStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.WearableExtender;

import android.graphics.BitmapFactory;
import android.content.Context;

import android.app.AlarmManager;

import com.futcore.restaurant.util.*;

public class FinishEventService extends Service
{
    //    static final int MAX_FIN_CNT = 10;
    //    static final long SYNC_DELAY_SEC = 3*60;

    static final int MAX_FIN_CNT = 0; //
    static final long SYNC_DELAY_SEC = 1;  //no delay
    
    private static int mFinEveCnt = 0;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

    Notification.Builder mBuilder = null;
    NotificationManager mNotifyMgr = null;

    private String mEventId;

    private int mMainIndex = 0;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private List<ManEventWear> eventWearModel = new ArrayList<ManEventWear>();

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao == null||itemDao == null||eventWearDao == null){
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
    public void onCreate() {
        super.onCreate();

        initNotification();
        getDao();

        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
        
        Bundle extras = intent.getExtras();
        if(extras !=null) {
            mEventId = extras.getString("eventid");
        }
        
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            
            try{
                ManEventWear evew = eventWearDao.queryForId(mEventId);
                evew.setUpdateTime(new Date().getTime());
                eventWearDao.createOrUpdate(evew);

                PreferenceUtil.saveBufferedEventToPreference(FinishEventService.this, evew);

                List<ManEventWear> evews = eventWearDao.queryForAll();

                List<ManEvent> ees = new ArrayList<ManEvent>();

                //                for(ManEventWear eveww:evews){
                ees.add(evew.toManEvent());
                    //                }
                
                Intent intentaa = new Intent(FinishEventService.this, AlarmHolderService.class);
                intentaa.putExtra("mop", "cancel");
                intentaa.putExtra("mlvl", "l2");
                //                intentaa.putExtra("events", (ArrayList<ManEvent>)eventModel);
                intentaa.putExtra("events", (ArrayList<ManEvent>)ees);
                //                stopService(intent);
                startService(intentaa);

                /*                Intent sintent = new Intent(FinishEventService.this, WakeAlertReceiverSim.class);
                //                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, jj++, sintent, 0);
                //                PendingIntent pendingIntent = PendingIntent.getBroadcast(FinishEventService.this, (int)(evew.getCreateTime()/1000)%1000000000, sintent, 0);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(FinishEventService.this, (int)(evew.getCreateTime()/1000)%1000000000, sintent, PendingIntent.FLAG_CANCEL_CURRENT);

                Intent sintent1 = new Intent(FinishEventService.this, WakeAlertReceiverSimp.class);
                //                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(FinishEventService.this, (int)(evew.getCreateTime()/1000)%1000000000-3*24*3600, sintent1, 0);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(FinishEventService.this, (int)(evew.getCreateTime()/1000)%1000000000-3*24*3600, sintent1, PendingIntent.FLAG_CANCEL_CURRENT);
                
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                alarmManager.cancel(pendingIntent1);
                */
                //                System.out.println("@@@@@@@@@@@@#############$$$$$$$$$$$$$$$$$$");
                //                for(ManEventWear ee:evews){
                //                    System.out.println(ee.getUpdateTime());
                //                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            mFinEveCnt++;
            
            if(mFinEveCnt>MAX_FIN_CNT){
                mFinEveCnt = 0;
                
                //delay for resetting finished events
                new android.os.Handler().postDelayed
                    (
                     new Runnable() {
                         public void run() {
                             //                     WakeAlertReceiver.completeWakefulIntent(intent);
                             //                             finish();
                             Intent intent1 = new Intent(FinishEventService.this, SyncFinishEveService.class);
                             startService(intent1);
                         }
                     }, SYNC_DELAY_SEC*1000);
            }
            //            System.out.println("pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp222");
            //            System.out.println(mEventId);
            
            /*            updateEventWearModel();
            if(eventWearModel.size()==0){
                sendFakeNotification();
            }
            else{
                ManEventWear tevent = eventWearModel.get(mMainIndex);
                long ttdate = tevent.getEstEndTime() - new Date().getTime();

                sendNotification(eventWearModel.get(mMainIndex).getManItem().getItemName()+(eventWearModel.size()>1?" ("+eventWearModel.size()+")":""), ttdate, NOTI_LESS_IMPORTANT);
            }
            */

            Intent intent = new Intent(FinishEventService.this, SendEventNotiService.class);
            //                stopService(intent);
            startService(intent);

            stopSelf();
        }
    }

    private void updateEventWearModel()
    {
        try{
            
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            //            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEvent.UPDATETIME_FIELD_NAME,0).and().eq(ManEventWear.DELETE_FIELD_NAME, (byte)0);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEvent.UPDATETIME_FIELD_NAME,0).and().ne(ManEventWear.DELETE_FIELD_NAME, (byte)1);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();

            //            lEventWearModel = eventWearModel;
            eventWearModel = eventWearDao.query(preparedQuery1);

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void onDestroy() {
        //        System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        super.onDestroy();
    }

    private void sendFakeNotification()
    {
        int mNotificationId = 001;
        //        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        
        mBuilder.setVibrate(new long[] { 1000, 300, 1000});
        
        mBuilder.setContentTitle("No Event")
            //            setContentText(TimerFormat.getTimeString(duration))
            .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
            .setWhen(System.currentTimeMillis() + 1800000);

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
        
        mBuilder.setContentTitle(title)
            //            setContentText(TimerFormat.getTimeString(duration))
            .setContentText("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk")
            .setWhen(System.currentTimeMillis() + duration);

        mNotifyMgr.notify(mNotificationId, addAllPages());        
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
    
}

