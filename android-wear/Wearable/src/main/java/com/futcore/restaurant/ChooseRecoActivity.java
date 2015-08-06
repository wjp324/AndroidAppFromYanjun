package com.futcore.restaurant;

import com.futcore.restaurant.util.*;

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
import android.widget.ImageView;

import java.text.SimpleDateFormat;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Intent;

import android.os.Vibrator;
import android.os.SystemClock;

import com.futcore.restaurant.models.*;

import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

public class ChooseRecoActivity extends Activity implements WearableListView.ClickListener, SensorEventListener {
    //    private static final List<Integer> times = Arrays.asList(15, 30, 45, 60, 90, 120);
    private static final int LIVE_TIME_SEC = 30;
    //private static final int LIVE_TIME_SEC = 5;
    
    private List<Integer> times = new ArrayList<Integer>();
    private List<Date> times1 = new ArrayList<Date>();
    
    //    private ManRecoItemWear mRecoWear = null;
    private ManItemScore mRecoScore = null;
    
    private TextView mEventTitle;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

    private WearableListView listView;

    private long mEstDurSec;

    private Vibrator v;
    

    private static final long SCREEN_ON_TIMEOUT_MS = 20000; // in milliseconds
    private static final long TIME_THRESHOLD_NS = 2000000000; // in nanoseconds (= 2sec)
    private static final float GRAVITY_THRESHOLD = 7.0f;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mLastTime = 0;
    private boolean mUp = false;

    private int mType;
    
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

    private void geneTimes()
    {
        long estDurSec = mRecoScore.getEstDurSec();
        mEstDurSec = estDurSec;
        
        if(mType==RecoListFragment.INTERVAL_TYPE){
            int interval = 0;
            int spread = 6;

            if(estDurSec<=30*60){
                interval = 1;
            }
            else{
                interval = 2;
            }

            //        for(int i = -spread;i<=spread;i++){
            for(int i = 0;i>=-spread;i--){
                times.add((int)estDurSec/60+i*interval);
            }
        }
        else if(mType==RecoListFragment.FIXEND_TYPE){
            long interval = 1000*60*15;
            int spread = 6;
            long estEstEnd = new Date().getTime()+estDurSec*1000;
            long estStEnd = estEstEnd-estEstEnd%interval;
            times1.add(new Date(estEstEnd));
            times1.add(new Date(estStEnd));

            for(int i = 1;i<=spread;i++){
                times1.add(new Date(estStEnd+interval*i));
                times1.add(new Date(estStEnd-interval*i));
            }
        }
        //        listView.setInitialOffset(-spread);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        //vibrate
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //        v.vibrate(5*1000);
        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        long[] pattern = {0, 100, 1000};

        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
        v.vibrate(pattern, 0);
        
        setContentView(R.layout.confirm_reco);
        mEventTitle = (TextView)findViewById(R.id.eventTitle);
        

        listView = (WearableListView) findViewById(R.id.timeList);
        listView.setAdapter(new Adapter(this));


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            //            mRecoScore = (ManItemScore) extras.getSerializable("recoscore");
            //            mEventTitle.setText(mRecoScore.getManItem().getItemName());
            //            geneTimes();
            mType = (int)extras.getInt("type");
            //            mEventTitle.setText(String.valueOf(mType));
        }
        

        if(getIntent().getSerializableExtra("recoscore")!=null){
            mRecoScore = (ManItemScore) getIntent().getSerializableExtra("recoscore");
            mEventTitle.setText(mRecoScore.getManItem().getItemName());
            geneTimes();
        }

        /*        if(getIntent().getExtra("type")!=null){
            mType = (int)getIntent().getExtra("type");
        }
        */
        
        listView.setClickListener(this);
        
        //        listView.setInitialOffset(6);

        new android.os.Handler().postDelayed
            (
             new Runnable() {
                 public void run() {
                     //                     WakeAlertReceiver.completeWakefulIntent(intent);
                     finish();
                 }
             }, LIVE_TIME_SEC*1000);
        
        
        getDao();

        //        Bundle extras = getIntent().getExtras();
        
        /*        if(getIntent().getSerializableExtra("recowear")!=null){
            mRecoWear = (ManRecoItemWear) getIntent().getSerializableExtra("recowear");
            mEventTitle.setText(mRecoWear.getManItem().getItemName());
        }
        */
        //            mEventId = extras.getString("eventid");
    }

    @Override
    public void onClick(WearableListView.ViewHolder v) {
        //        updateNotification((Integer) v.itemView.getTag());


        if(mType==RecoListFragment.FIXEND_TYPE){
            Date estEnd = times1.get((Integer)v.itemView.getTag());
            chooseEvent1(estEnd);
        }
        else if(mType==RecoListFragment.INTERVAL_TYPE){
            int estMin = times.get((Integer)v.itemView.getTag());
            chooseEvent((long)estMin*60);
        }
        
        
        //        System.out.println(estMin);

        //        finish();
    }


    private void chooseEvent1(Date estEnd)
    {
        try{
            ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
            //            recoItemWearDao.create();
            long curTime = new Date().getTime();
            itemDao.createIfNotExists(mRecoScore.getManItem());
            //            eventWearDao.create(new ManEventWear(ItemIdUtil.getGenId("event"), tuser, mRecoScore.getManItem(),0,0,0,curTime, curTime+estMin*60000,estMin, (byte)0));
            ManEventWear nEveWear = new ManEventWear(ItemIdUtil.getGenId("event"), tuser, mRecoScore.getManItem(),0,0,0,curTime, estEnd.getTime(),(int)(estEnd.getTime()-curTime)/1000/60, (byte)0);
            
            eventWearDao.create(nEveWear);


            ArrayList<ManEvent> aaEvents = new ArrayList<ManEvent>();
            aaEvents.add(nEveWear.toManEvent());
            
            Intent intentaa = new Intent(this, AlarmHolderService.class);
            intentaa.putExtra("mop", "set");
            //                    intentaa.putExtra("mlvl", "l2");
            intentaa.putExtra("mlvl", "l0");
            intentaa.putExtra("events", aaEvents);
            intentaa.putExtra("cancelold", true);
            //                stopService(intent);
            startService(intentaa);


            Intent intent = new Intent(this, SendEventNotiService.class);
            //        stopService(intent);
            startService(intent);

            PreferenceUtil.saveAccRecoToPreference(this, mRecoScore, new Date().getTime());
        }
        catch(SQLException e){
            e.printStackTrace();
        }


        finish();
    }

    private void chooseEvent(long estSec)
    {
        try{
            ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
            //            recoItemWearDao.create();
            long curTime = new Date().getTime();
            itemDao.createIfNotExists(mRecoScore.getManItem());
            //            eventWearDao.create(new ManEventWear(ItemIdUtil.getGenId("event"), tuser, mRecoScore.getManItem(),0,0,0,curTime, curTime+estMin*60000,estMin, (byte)0));
            ManEventWear nEveWear = new ManEventWear(ItemIdUtil.getGenId("event"), tuser, mRecoScore.getManItem(),0,0,0,curTime, curTime+estSec*1000,(int)estSec/60, (byte)0);
            
            eventWearDao.create(nEveWear);


            ArrayList<ManEvent> aaEvents = new ArrayList<ManEvent>();
            aaEvents.add(nEveWear.toManEvent());
            
            Intent intentaa = new Intent(this, AlarmHolderService.class);
            intentaa.putExtra("mop", "set");
            //                    intentaa.putExtra("mlvl", "l2");
            intentaa.putExtra("mlvl", "l0");
            intentaa.putExtra("events", aaEvents);
            intentaa.putExtra("cancelold", true);
            //                stopService(intent);
            startService(intentaa);


            Intent intent = new Intent(this, SendEventNotiService.class);
            //        stopService(intent);
            startService(intent);

            PreferenceUtil.saveAccRecoToPreference(this, mRecoScore, new Date().getTime());
        }
        catch(SQLException e){
            e.printStackTrace();
        }


        finish();
    }
    

    @Override
    public void onTopEmptyRegionClick() {
    }

    //    private static final class Adapter extends WearableListView.Adapter {
    private final class Adapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private Adapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.reco_timepicker_list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            ImageView circlev = (ImageView) holder.itemView.findViewById(R.id.circle);
            //            if(mType==RecoListFragment.FIXEND_TYPE)
            if(mType==RecoListFragment.INTERVAL_TYPE)
                circlev.getDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.OVERLAY);
//            view.setText(mContext.getString(NotificationPresets.PRESETS[position].nameResId//));
//            view.setText(times.get(position).toString());
            //            holder.itemView.setTag(position);

            if(mType==RecoListFragment.FIXEND_TYPE){
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
                //                view.setText(times1.get(position).toString());
                view.setText(sdf.format(times1.get(position)));
            }
            else if(mType==RecoListFragment.INTERVAL_TYPE)
                view.setText(times.get(position).toString());


            if(position==0){
                circlev.getDrawable().setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);
            }
            
            
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            if(mType==RecoListFragment.FIXEND_TYPE){
                return times1.size();
            }
            else
                return times.size();
        }
    }



    @Override
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
    


    @Override
    public void onSensorChanged(SensorEvent event) {
        //        detectJump(event.values[0], event.timestamp);
        detectJump(event.values[1], event.timestamp);
        //        detectJump1(event.values[1], event.timestamp);
        //        detectJump2(event.values[2], event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void detectJump(float xValue, long timestamp) {
        if ((Math.abs(xValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (xValue > 0)) {
                onJumpDetected(!mUp);
            }
            mUp = xValue > 0;
            mLastTime = timestamp;
        }
    }

    private void onJumpDetected(boolean up) {
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
        //confirmFinish();
        chooseEvent(mEstDurSec);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        v.cancel();
    }
}
