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

import org.json.JSONObject;
import org.json.JSONException;


import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManRecoItemWear;
import com.futcore.restaurant.models.ManRecoItemWearWithScore;
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
import java.util.Comparator;
import java.util.Collections;

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

public class SendForceRecoNotiService extends Service
{
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    //    private List<ManEventWear> eventWearModel = new ArrayList<ManEventWear>();
    private List<ManRecoItemWear> recoItemWearModel = new ArrayList<ManRecoItemWear>();
    private List<ManRecoItemWear> curRecoItemWearModel = new ArrayList<ManRecoItemWear>();
    private List<ManRecoItemWearWithScore> curRecoItemWearScoreModel = new ArrayList<ManRecoItemWearWithScore>();

    Notification.Builder mBuilder = null;
    Notification.Builder mRecoBuilder = null;
    NotificationManager mNotifyMgr = null;
    NotificationManager mRecoNotifyMgr = null;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    public static final long RECOTHRESHOLDSEC = 3600000;

    private static final int TIMEZONE_OFFSET = 8;
    
    private int mMainIndex = 0;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||eventWearDao==null||recoItemWearDao==null){
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
            //            System.out.println("lLLLLLLLLLLLLLLLLLLLLLLLLLLLGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
            System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            updateRecoItemWearModel();
            /*            try{
                QueryBuilder<ManRecoItemWear, Integer> queryBuilder = recoItemWearDao.queryBuilder();
                queryBuilder.limit(3);
                PreparedQuery<ManRecoItemWear> preparedQuery = queryBuilder.prepare();
                curRecoItemWearModel = recoItemWearDao.query(preparedQuery);

                
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            */
            
            stopSelf();
        }
    }
    
    


    @Override
    public void onCreate() {
        
        super.onCreate();
        getDao();
        initRecoNotification();
        
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

    private void sendRecoNotification(String title, int type)
    {
        int mNotificationId = 003;
        
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
        
        
        //        mRecoBuilder.setContentTitle(title);
            //            .setContentText("Test");
            //            .setWhen(System.currentTimeMillis() + duration);

        mRecoNotifyMgr.notify(mNotificationId, addAllRecoPages());
    }


    private Notification addAllRecoPages()
    {
        Notification.WearableExtender twoPageNotification =
            new  Notification.WearableExtender();
        List<Notification> nlist = new ArrayList<Notification>();

        int i = 0;
        //        for(ManEventWear eve:eventWearModel){
        /*        for(ManRecoItemWear riw:curRecoItemWearModel){
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
        */

        int j = 0;

        ManRecoItemWear riw = (ManRecoItemWear)curRecoItemWearScoreModel.get(j);
        int recoScore = 0;
        try{
            JSONObject conff = new JSONObject(riw.getModelConfig());
            recoScore = conff.getInt("score");
                        
        }catch(JSONException e){
            e.printStackTrace();
        }
        
        mRecoBuilder.setContentText(curRecoItemWearScoreModel.get(mMainIndex).getManItem().getItemName()).setContentTitle(Integer.toString(recoScore)+"   "+secToHhMmSs(riw.getRecoDuration()));
  
        
        
            
        //        Intent intent0 =  new Intent(this, FinishEventActivity.class);
        Intent intent0 =  new Intent(this, ConfirmForceRecoActivity.class);
        //        intent0.putExtra("recowear", curRecoItemWearModel.get(j++));
        intent0.putExtra("recowear", (ManRecoItemWear)curRecoItemWearScoreModel.get(j++));
        
        intent0.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent0 = PendingIntent.getActivity(this, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent0));
        twoPageNotification.setContentAction(0);
        twoPageNotification.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.spidergl));

        /*        for(Notification noti:nlist){
        //            Intent intent = new Intent(this, FinishEventActivity.class);
        Intent intent = new Intent(this, ConfirmRecoActivity.class);
        intent.putExtra("recowear", curRecoItemWearModel.get(j++));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            
        twoPageNotification.addAction(new Notification.Action(R.drawable.empty16, getString(R.string.event_finish), pendingIntent));
        twoPageNotification.addPage(noti);
        }
        */

        Intent intentss = new Intent(this, SendForceRecoNotiService.class);
        PendingIntent pendingIntentss = PendingIntent.getService(this, 0, intentss, 0);
        
        twoPageNotification.addAction(new Notification.Action(R.drawable.ic_full_sad, getString(R.string.update_reco), pendingIntentss));

        mRecoBuilder.extend(twoPageNotification);
        
        return mRecoBuilder.build();
    }
    
    public void onDestroy() {
        System.out.println("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddkkkkkkkkkkkkkkkkkkkkkkkk11111");
        super.onDestroy();
    }

    private void updateRecoItemWearModel()
    {
        long dateindi = (new Date().getTime()+TIMEZONE_OFFSET*1000*3600)/(24*1000*3600);
        long timeindi = (new Date().getTime()+TIMEZONE_OFFSET*1000*3600)%(24*1000*3600);

        try{
            
            List<ManRecoItemWear> recos = recoItemWearDao.queryForAll();
            for(ManRecoItemWear rec:recos){
                    
                try{
                    JSONObject conff = new JSONObject(rec.getModelConfig());
                    
                    long score = conff.getLong("score");
                        
                    if(timeindi>conff.getLong("timeindi")-conff.getLong("timefuzzy")*1000 && timeindi<conff.getLong("timeindi")+conff.getLong("timefuzzy")*1000){
                        curRecoItemWearScoreModel.add(new ManRecoItemWearWithScore(rec, score));
                        Collections.sort(curRecoItemWearScoreModel, new EventEndComparator());
                    }
                        //                        curRecoItemWearModel.add(rec);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            sendRecoNotification(curRecoItemWearScoreModel.get(mMainIndex).getManItem().getItemName(), NOTI_LESS_IMPORTANT);

            // refresh event noti also
            Intent intent = new Intent(this, SendEventNotiService.class);
            startService(intent);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public class EventEndComparator implements Comparator<ManRecoItemWearWithScore> {
        @Override
            public int compare(ManRecoItemWearWithScore eve1, ManRecoItemWearWithScore eve2) {
            return new Long(eve2.getScore()).compareTo(new Long(eve1.getScore()));
        }
    }

    private String secToHhMmSs(int seconds)
    {
        int hr = seconds/3600;
        int rem = seconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;

        if(hr==0)
            return mnStr+":"+secStr;
        return hrStr+":"+mnStr+":"+secStr;
    }
}

