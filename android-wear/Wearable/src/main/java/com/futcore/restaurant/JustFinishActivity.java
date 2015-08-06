package com.futcore.restaurant;

import com.futcore.restaurant.util.ItemIdUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WearableListView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Intent;

import com.futcore.restaurant.models.ManRecoItemWear;
import com.futcore.restaurant.models.ManEventWear;

import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import android.support.v4.view.GestureDetectorCompat;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.DismissOverlayView;

import android.view.WindowManager;

import android.os.Vibrator;
import android.os.SystemClock;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.os.PowerManager.WakeLock;
import android.os.PowerManager;

public class JustFinishActivity extends Activity implements DelayedConfirmationView.DelayedConfirmationListener{
    private static final int NUM_SECONDS = 20;
    
    private static final List<Integer> times = Arrays.asList(15, 30, 45, 60, 90, 120);
    //    private ManRecoItemWear mRecoWear = null;
    private ManEventWear mPlanWear = null;
    
    private TextView mEventTitle;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

    //    private boolean mIsCancel = false;
    private boolean mIsFin = false;

    private String mEveid;
    private int mHoldSec = 20;
    private ManEventWear mEveWear = null;

    DelayedConfirmationView delayedConfirmationView = null;

    //    private PowerManager.WakeLock wl;
    private Vibrator v;

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

    /**
     * Handles the button to start a DelayedConfirmationView timer.
     */
    //    public void onStartTimer(View view) {
    public void startTimer() {
        delayedConfirmationView = (DelayedConfirmationView)
            findViewById(R.id.timer);
        //        delayedConfirmationView.setTotalTimeMs(NUM_SECONDS * 1000);
        delayedConfirmationView.setTotalTimeMs(mHoldSec * 1000);
        delayedConfirmationView.setListener(this);
        delayedConfirmationView.start();
        //        scroll(View.FOCUS_DOWN);
    }

 
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onTimerFinished(View v) {
        if(!mIsFin&&mHoldSec==10){
            /*            List<ManEvent> evess = new ArrayList<ManEvent>();
                          evess.add(mEveWear.toManEvent());
            
                          Intent intentaa = new Intent(this, AlarmHolderService.class);
                          intentaa.putExtra("mop", "set");
                          intentaa.putExtra("mlvl", "l1");
                          intentaa.putExtra("events", (ArrayList<ManEvent>)evess);
                          intentaa.putExtra("cancelold", true);
            
                          startService(intentaa);
            */
            
            /*            Intent sintentAct = new Intent(this, WakeAlertReceiverActSim.class);
                          sintentAct.putExtra("eveid", mEveid);
                          sintentAct.putExtra("holdsec", 90);
                          //            PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(mEveWear.getCreateTime().getTime()/1000)%1000000000, sintentAct, 0);
                          PendingIntent pendingIntentAct = PendingIntent.getBroadcast(this, (int)(mEveWear.getCreateTime()/1000)%1000000000, sintentAct, 0);
                          //            long toEnd = mEveWear.getEstEndTime().getTime()-new Date().getTime();
                          //            long toEnd = mEveWear.getEstEndTime()-new Date().getTime();
            
                          AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                          //            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+toEnd-90*1000, pendingIntentAct);
                          alarmManager.setExact(AlarmManager.RTC_WAKEUP, mEveWear.getEstEndTime()-90*1000, pendingIntentAct);
            */
        }
        //        Log.d(TAG, "onTimerFinished is called.");
        //        scroll(View.FOCUS_UP);
        /*        if(!mIsCancel){
                  System.out.println("6666666666666666666666666666666666666666666666666666666666666666666confirm plan");
                  try{
                  mPlanWear.setIsDelete((byte)0);
                  eventWearDao.update(mPlanWear);
                
                  Intent intent = new Intent(this, SendEventNotiService.class);
                  startService(intent);
                  }
                  catch(SQLException e){
                  e.printStackTrace();
                  }
                  }
        */
        
        //        finish();
        if(!mIsFin)
            finishActivity();
    }
    
    @Override
    public void onTimerSelected(View v) {
        System.out.println("555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555 finish it"+mEveid);
        //        ManEventWear evew = eventWearDao.queryForId(mEventId);
        //        mEveWear.setUpdateTime(new Date().getTime());
        //        eventWearDao.createOrUpdate(mEveWear);
        Intent intent = new Intent(JustFinishActivity.this, FinishEventService.class);
        //                getActivity().stopService(intent);
        intent.putExtra("eventid",mEveid);
        startService(intent);
        //        Log.d(TAG, "onTimerSelected is called.");
        //        scroll(View.FOCUS_UP);
        //        mIsCancel = true;
        mIsFin = true;
        //        finish();
        System.out.println("555555 finish it"+mEveid);
        
        finishActivity();
    }

    private void finishActivity()
    {
        System.out.println("<<6666666666666666666666666666666666666666666666666666666666666666666 release lock");
        v.cancel();
        //        wl.release();
        //        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*        //wake lock
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag"+new Date().getTime());
        //        wl = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "My Tag"+new Date().getTime());
        //        wl = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK), "My Tag"+new Date().getTime());
        wl = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "My Tag"+new Date().getTime());
        
        wl.acquire();
        */
        
        //        setContentView(R.layout.confirm_force_reco);
        setContentView(R.layout.just_finish_act);
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        
        mEventTitle = (TextView)findViewById(R.id.eventTitle);

        getDao();
        
        Bundle extras = getIntent().getExtras();

        mEveid = extras.getString("eveid");
        mHoldSec = extras.getInt("holdsec");

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkpppppppppppppppppppppponnnnnnnnnnnnnnnnn!!$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+mHoldSec);
        v.vibrate(mHoldSec*1000);
        
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            queryBuilder1.where().eq(ManEventWear.ID_FIELD_NAME, mEveid);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            //            lEventWearModel = eventWearModel;
            //            finishEventWearModel = eventWearDao.query(preparedQuery1);
            List<ManEventWear> tmpL = eventWearDao.query(preparedQuery1);
            if(tmpL.size()>0){
                mEveWear = tmpL.get(0);
                mEventTitle.setText(mEveWear.getManItem().getItemName());
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        /*        if(getIntent().getSerializableExtra("planwear")!=null){
                  mPlanWear = (ManEventWear) getIntent().getSerializableExtra("planwear");
                  mEventTitle.setText(mPlanWear.getManItem().getItemName());
                  }
        */
        
        startTimer();
        
        /*        mEventTitle = (TextView)findViewById(R.id.eventTitle);

                  WearableListView listView = (WearableListView) findViewById(R.id.timeList);
                  listView.setAdapter(new Adapter(this));
                  listView.setClickListener(this);

                  getDao();
                  Bundle extras = getIntent().getExtras();
        
                  if(getIntent().getSerializableExtra("recowear")!=null){
                  mRecoWear = (ManRecoItemWear) getIntent().getSerializableExtra("recowear");
                  mEventTitle.setText(mRecoWear.getManItem().getItemName());
                  }
        */
        //            mEventId = extras.getString("eventid");
    }

}
