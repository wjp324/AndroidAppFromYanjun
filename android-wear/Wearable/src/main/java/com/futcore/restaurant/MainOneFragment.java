package com.futcore.restaurant;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.wearable.view.WearableListView;
import android.content.Context;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.app.Service;
import android.app.PendingIntent;
import android.app.AlarmManager;

import java.text.SimpleDateFormat;

import com.futcore.restaurant.util.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Intent;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;


public class MainOneFragment extends Fragment
{
    private static final int TIMEZONE_OFFSET = 8;
    
    private LinearLayout mSendNoti;
    private LinearLayout mResetDailyAlarm;
    private LinearLayout mResetDailyNow;
    private TextView mResetDailyAlarmText;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_one, container, false);

        mSendNoti = (LinearLayout)view.findViewById(R.id.sendnoti);
        mResetDailyAlarm = (LinearLayout)view.findViewById(R.id.resetdailyalarm);
        mResetDailyAlarmText = (TextView)view.findViewById(R.id.resetdailyalarmtext);
        mResetDailyNow = (LinearLayout)view.findViewById(R.id.resetdailynow);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        long alarmTouch = PreferenceUtil.getDailyAlarmTouchFromPreference(getActivity());
        
        mResetDailyAlarmText.setText("D-Batch "+ (alarmTouch==0l?0:sdf.format(new Date(alarmTouch))));

        mSendNoti.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SendEventNotiService.class);
                    getActivity().startService(intent);

                    if(!isMyServiceRunning(LongMonitorService.class)){
                        Intent intent1 = new Intent(getActivity(), LongMonitorService.class);
                        getActivity().startService(intent1);
                    }

                    getActivity().finish();
                    
                }
            });


        mResetDailyAlarm.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlarmManager mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent sintentAct = new Intent(getActivity(), DailyBatchService.class);
                    PendingIntent pendingIntentAct = PendingIntent.getService(getActivity(),0,sintentAct,0);
                    mAlarmManager.cancel(pendingIntentAct);
                    mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, getNearest1Am(), 1000*3600*24, pendingIntentAct);
                    getActivity().finish();
                }
            });



        mResetDailyNow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent sintentAct = new Intent(getActivity(), DailyBatchService.class);
                    getActivity().startService(sintentAct);
                    getActivity().finish();
                }
            });

        
        return view;
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
}

