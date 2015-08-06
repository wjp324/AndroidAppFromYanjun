package com.futcore.restaurant;

import static com.futcore.restaurant.SpiderDataListenerService.LOGD;


import android.app.Service;
import android.app.PendingIntent;
import android.app.AlarmManager;

import java.text.SimpleDateFormat;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

import android.view.View.OnClickListener;


import android.util.Log;
import java.util.List;
import java.util.Date;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.futcore.restaurant.SpiderDataListenerService;

import android.content.Intent;

import com.futcore.restaurant.util.*;

import android.support.v4.view.ViewPager;

public class CMainActivity extends Activity{

    private static final String TAG = "CMainActivity";

    private static final int TIMEZONE_OFFSET = 8;
    
    /*    private LinearLayout mSendNoti;
    private LinearLayout mResetDailyAlarm;
    private LinearLayout mResetDailyNow;
    private TextView mResetDailyAlarmText;
    */

    private ViewPager mPager;

    private MainOneFragment mCounterPage;
    //    private MainOneFragment mCounterPage1;
    private MainTwoFragment mCounterPage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmain);

        mPager = (ViewPager) findViewById(R.id.pager);

        final MainPagerAdapter adapter = new MainPagerAdapter(getFragmentManager());

        mCounterPage = new MainOneFragment();
        mCounterPage1 = new MainTwoFragment();
        
        adapter.addFragment(mCounterPage1);
        adapter.addFragment(mCounterPage);

        mPager.setAdapter(adapter);
        
        /*        mSendNoti = (LinearLayout)findViewById(R.id.sendnoti);
        mResetDailyAlarm = (LinearLayout)findViewById(R.id.resetdailyalarm);
        mResetDailyAlarmText = (TextView)findViewById(R.id.resetdailyalarmtext);
        mResetDailyNow = (LinearLayout)findViewById(R.id.resetdailynow);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        long alarmTouch = PreferenceUtil.getDailyAlarmTouchFromPreference(CMainActivity.this);

        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"+alarmTouch);
        
        mResetDailyAlarmText.setText("D-Batch "+ (alarmTouch==0l?0:sdf.format(new Date(alarmTouch))));

        mSendNoti.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CMainActivity.this, SendEventNotiService.class);
                    startService(intent);
                    finish();
                }
            });

        mResetDailyAlarm.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent sintentAct = new Intent(CMainActivity.this, DailyBatchService.class);
                    //                    sintentAct.putExtra("eveid", cceve.getEventId());
                    //            sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                    //                    sintentAct.putExtra("holdsec", L1_HOLD_SEC);
                    //                    PendingIntent pendingIntentAct = PendingIntent.getBroadcast(CMainActivity.this, (int)(cceve.getCreateTime().getTime()/1000)%1000000000-L1_OFFSET, sintentAct, 0);
                    PendingIntent pendingIntentAct = PendingIntent.getService(CMainActivity.this,0,sintentAct,0);
                    mAlarmManager.cancel(pendingIntentAct);
                    //                    mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, cceve.getEstEndTime().getTime()-L1_ALARM_SEC*1000, pendingIntentAct);
                    //                    mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*3600*24, pendingIntentAct);
                    mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, getNearest1Am(), 1000*3600*24, pendingIntentAct);
                    //                    Intent intent = new Intent(CMainActivity.this, ResetDailyAlarmService.class);
                    //                    startService(intent);
                    finish();
                }
            });



        mResetDailyNow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent sintentAct = new Intent(CMainActivity.this, DailyBatchService.class);
                    startService(sintentAct);
                    finish();
                }
            });
*/
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private long getNearest1Am()
    {
        long currentTime = new Date().getTime();
        long timestampOfDay = CommonUtil.getTimeStampOfDay(currentTime);
        long timeplus = timestampOfDay>3600?(25*3600*1000-timestampOfDay*1000):(3600*1000-timestampOfDay*1000);
        return currentTime+timeplus;
        //        return new Date().getTime()/
        //        return new Date().getTime()
    }


    
}
