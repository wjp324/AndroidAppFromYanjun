package com.futcore.restaurant;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.WindowInsets;

import java.util.Random;
import java.util.Date;

import android.content.Context;

import java.text.SimpleDateFormat;

import android.util.Log;
import java.util.List;
import java.util.ArrayList;

import android.content.Intent;

import android.view.View.OnClickListener;
import android.view.View;
import android.widget.LinearLayout;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import android.os.Handler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.futcore.restaurant.models.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import android.widget.TextView;
import android.widget.LinearLayout;

public class HistoryDetailActivity extends Activity
{
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private LinearLayout mCancel;
    private TextView mUploadIndi;

    private static final String TAG = "UploadFinish";

    private static final String FINISH_EVENT_RECEIVED_PATH = "/finish-event-received";
    
    private static final String UPLOAD_FINISH_PATH = "/uploadfinish";
    //    private static final String IMAGE_PATH = "/image";
    private static final String UPLOAD_FINISH_KEY = "uploadfinish";

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
    //    private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
    //	private static Dao<ManItem, String> itemDao = null;

    private Handler mHandler;

    private List<ManEventWear> finishEventWearModel = new ArrayList<ManEventWear>();
    private List<ManEvent> finishEventModel = new ArrayList<ManEvent>();

    private ScheduledExecutorService mFinishExecutor;
    private ScheduledFuture<?> mFinishFuture;

    private ManEventWear mEvent = null;
    private TextView mStartTime;
    private TextView mFinishTime;
    private TextView mEventTitle;
    private LinearLayout mBackEvent;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(/*eventDao == null||itemDao == null||*/eventWearDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            try {
                //                eventDao = helper.getEventDao();
                //                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_detail);

        mEventTitle = (TextView)findViewById(R.id.eventTitle);
        mStartTime = (TextView)findViewById(R.id.startTime);
        mFinishTime = (TextView)findViewById(R.id.finishTime);
        mBackEvent = (LinearLayout)findViewById(R.id.backEvent);

        mBackEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEvent.setUpdateTime(0l);
                    try{
                        //                        eventWearDao.update(mEvent);
                        eventWearDao.createOrUpdate(mEvent);

                        Intent intent = new Intent(HistoryDetailActivity.this, SendEventNotiService.class);
                        startService(intent);
                        Intent mIntent = new Intent();
                        //                        mIntent.putExtras(bundle);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    
                    //                    DoIt(v);
                }
            });

        if(getIntent().getSerializableExtra("event")!=null){
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            
            mEvent = (ManEventWear) getIntent().getSerializableExtra("event");
            mEventTitle.setText(mEvent.getManItem().getItemName());
            mStartTime.setText(formatter.format(new Date(mEvent.getStartTime())));
            mFinishTime.setText(mEvent.getUpdateTime()==0?"":formatter.format(new Date(mEvent.getUpdateTime())));
            if(mEvent.getUpdateTime()==0)
                mBackEvent.setVisibility(View.INVISIBLE);
        }
        
        mHandler = new Handler();
        getDao();
        
        
        //        mConfirm = (LinearLayout)findViewById(R.id.finishConfirm);
        //        mCancel = (LinearLayout)findViewById(R.id.cancelUpload);
        //        mUploadIndi = (TextView)findViewById(R.id.uploadIndi);

        //        mConfirm.setOnClickListener(this);
        //        mCancel.setOnClickListener(this);

        //        onNewIntent(getIntent());

        onNewIntent(getIntent());
        
    }
    
    
}

