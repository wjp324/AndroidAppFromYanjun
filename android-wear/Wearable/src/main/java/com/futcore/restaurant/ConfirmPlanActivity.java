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


public class ConfirmPlanActivity extends Activity implements DelayedConfirmationView.DelayedConfirmationListener{
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

    private boolean mIsCancel = false;

    DelayedConfirmationView delayedConfirmationView = null;

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
        delayedConfirmationView.setTotalTimeMs(NUM_SECONDS * 1000);
        delayedConfirmationView.setListener(this);
        delayedConfirmationView.start();
        //        scroll(View.FOCUS_DOWN);
    }

    @Override
    public void onTimerFinished(View v) {
        //        Log.d(TAG, "onTimerFinished is called.");
        //        scroll(View.FOCUS_UP);
        if(!mIsCancel){
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
        
        finish();
    }

    @Override
    public void onTimerSelected(View v) {
        //        Log.d(TAG, "onTimerSelected is called.");
        //        scroll(View.FOCUS_UP);
        mIsCancel = true;
        finish();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_force_reco);

        mEventTitle = (TextView)findViewById(R.id.eventTitle);        

        getDao();
        Bundle extras = getIntent().getExtras();
        
        if(getIntent().getSerializableExtra("planwear")!=null){
            mPlanWear = (ManEventWear) getIntent().getSerializableExtra("planwear");
            mEventTitle.setText(mPlanWear.getManItem().getItemName());
        }

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
