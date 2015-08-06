package com.futcore.restaurant;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

import java.util.Random;
import java.util.Date;

import android.content.Context;

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

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManEventWear;
import com.futcore.restaurant.models.ManCertPlace;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;


import android.widget.TextView;

public class UploadFinishActivity extends Activity implements OnClickListener, DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks, OnConnectionFailedListener
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
        
        mHandler = new Handler();
        setContentView(R.layout.upload_finish);

        //        mConfirm = (LinearLayout)findViewById(R.id.finishConfirm);
        mCancel = (LinearLayout)findViewById(R.id.cancelUpload);
        mUploadIndi = (TextView)findViewById(R.id.uploadIndi);

        //        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        //        onNewIntent(getIntent());
        getDao();

        onNewIntent(getIntent());
        
        /*        if(!isMyServiceRunning(LongMonitorService.class)){
                  Intent intent = new Intent(this, LongMonitorService.class);
                  startService(intent);
                  }
        */
        
        //        if(!isMyServiceRunning(RecoLongService.class)){
        Intent intent = new Intent(this, RecoLongCancelService.class);
            startService(intent);
            //        }
    }

    @Override
    public void onNewIntent(Intent intent){
        /*        mGoogleApiClient = new GoogleApiClient.Builder(this)
                  .addApi(Wearable.API)
                  .build();
        */

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        mGoogleApiClient.connect();

        try{
            
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().ne(ManEvent.UPDATETIME_FIELD_NAME,0);
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();

            //            lEventWearModel = eventWearModel;
            finishEventWearModel = eventWearDao.query(preparedQuery1);
            
            for(ManEventWear evew:finishEventWearModel){
                finishEventModel.add(evew.toManEvent());
            }

            mUploadIndi.setText("Uploading "+finishEventWearModel.size()+" finished events...");

            mFinishExecutor =  new ScheduledThreadPoolExecutor(1);
            mFinishFuture = mFinishExecutor.scheduleWithFixedDelay(
                                                                   new FinishGenerator(), 1, 5, TimeUnit.SECONDS);
            
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mFinishFuture.cancel(true);
        super.onStop();
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        //        LOGD(TAG, "Google API Client was connected");
        mResolvingError = false;
        //        mStartActivityBtn.setEnabled(true);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override //ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        //        LOGD(TAG, "Connection to Google API client was suspended");
        //        mStartActivityBtn.setEnabled(false);
    }

    @Override //OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            /*            try {
                          mResolvingError = true;
                          result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR); //not good
                          } catch (IntentSender.SendIntentException e) {
                          // There was an error with the resolution intent. Try again.
                          mGoogleApiClient.connect();
                          }
            */
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
            //            mStartActivityBtn.setEnabled(false);
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        }
    }


    @Override //DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
        /*        LOGD(TAG, "onDataChanged: " + dataEvents);
                  final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
                  dataEvents.close();
                  runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                  for (DataEvent event : events) {
                  if (event.getType() == DataEvent.TYPE_CHANGED) {
                  //                        mDataItemListAdapter.add(
                  //                                new Event("DataItem Changed", event.getDataItem().toString()));
                  } else if (event.getType() == DataEvent.TYPE_DELETED) {
                  //                        mDataItemListAdapter.add(
                  //                                new Event("DataItem Deleted", event.getDataItem().toString()));
                  }
                  }
                  }
                  });
        */
    }

    @Override //MessageListener
    public void onMessageReceived(final MessageEvent messageEvent) {
        //        LOGD(TAG, "onMessageReceived() A message from watch was received:" + messageEvent.getRequestId() + " " + messageEvent.getPath());
        /*        mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                  //                mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
                  }
                  });
        */

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        //        System.out.println(messageEvent.getPath());
        //        System.out.println(messageEvent.toString());
        if(messageEvent.getPath().equals(FINISH_EVENT_RECEIVED_PATH)){
            if(new String(messageEvent.getData()).equals("ok")){
                //                for(ManEventWear evew:finishEventWearModel){
                try{
                    int del = eventWearDao.delete(finishEventWearModel);
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                //                }
            }
        }

        mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //                mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
                    mUploadIndi.setText("Updated!");
                }
            });
        
        //            System.out.println();
        //        mUploadIndi.setText("Updated!");
        //        mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
    }

    @Override //NodeListener
    public void onPeerConnected(final Node peer) {
        //        LOGD(TAG, "onPeerConnected: " + peer);
        mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //                mDataItemListAdapter.add(new Event("Connected", peer.toString()));
                }
            });
    }

    @Override //NodeListener
    public void onPeerDisconnected(final Node peer) {
        LOGD(TAG, "onPeerDisconnected: " + peer);
        mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //                mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
                }
            });
    }

    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
        switch(id){
        case R.id.cancelUpload:
            {
                finish();
            }
            break;
        }
    }

    private class FinishGenerator implements Runnable {
        @Override
        public void run() {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            
            try{
                
                out = new ObjectOutputStream(bos);
                out.writeObject(finishEventModel);
                byte[] yourBytes = bos.toByteArray();
            
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(UPLOAD_FINISH_PATH);
                //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
                //            Random rand= new Random();
                //            putDataMapRequest.getDataMap().putString(UPLOAD_FINISH_KEY, mEventId+(rand.nextInt(900)+100));
                //            PutDataRequest request = putDataMapRequest.asPutDataRequest();
                
                putDataMapRequest.getDataMap().putByteArray(UPLOAD_FINISH_KEY, yourBytes);
                PutDataRequest request = putDataMapRequest.asPutDataRequest();
            
                if (!mGoogleApiClient.isConnected()) {
                    System.out.println("nnmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm5555555555555");
                    return;
                }
                System.out.println("nnmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm111111");
                System.out.println(finishEventModel.size());
                    
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataItemResult>() {
                            @Override
                            public void onResult(DataItemResult dataItemResult) {
                                if (!dataItemResult.getStatus().isSuccess()) {
                                    System.out.println("nnmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm22222");
                                    System.out.println(dataItemResult.getStatus().getStatusCode());
                                }
                            }
                        });
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

