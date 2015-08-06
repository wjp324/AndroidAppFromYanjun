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

import android.util.Log;
import java.util.List;
import java.util.ArrayList;

import android.content.Intent;

import java.text.SimpleDateFormat;

import android.view.View.OnClickListener;
import android.view.View;
import android.widget.LinearLayout;

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

public class FinishEventActivity extends Activity implements OnClickListener, DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks, OnConnectionFailedListener
{
    private LinearLayout mConfirm;
    private LinearLayout mCancel;

    private TextView mEventEndTime;
    private TextView mEventTitle;

    private String mEventId;

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    private Handler mHandler;

    private List<ManEventWear> finishEventWearModel = new ArrayList<ManEventWear>();
    private List<ManEvent> finishEventModel = new ArrayList<ManEvent>();

    private static final String TAG = "EventFinisher";

    private static final String FINISH_PATH = "/finish";
    //    private static final String IMAGE_PATH = "/image";
    private static final String FINISH_KEY = "finish";

    private static final String FINISH_EVENT_RECEIVED_PATH = "/finish-event-received";
    private static final String UPLOAD_FINISH_PATH = "/uploadfinish";
    //    private static final String IMAGE_PATH = "/image";
    private static final String UPLOAD_FINISH_KEY = "uploadfinish";
    
        
    private ScheduledExecutorService mFinishExecutor;
    private ScheduledFuture<?> mFinishFuture;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private static final int MAX_FIN_CNT = 3;
    private static int mFinEveCnt = 0;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_event);

        mConfirm = (LinearLayout)findViewById(R.id.finishConfirm);
        mCancel = (LinearLayout)findViewById(R.id.finishCancel);

        mEventTitle = (TextView)findViewById(R.id.eventTitle);
        mEventEndTime = (TextView)findViewById(R.id.eventEndTime);

        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        //        onNewIntent(getIntent());
        getDao();

        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
        onNewIntent(getIntent());
        /*        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
            String itemid = extras.getString("itemid");
            System.out.println(itemid);
        }
        */
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

        Bundle extras = intent.getExtras();
        if(extras !=null) {
            System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
            mEventId = extras.getString("eventid");
            System.out.println(mEventId);
            
            try{
                ManEventWear evew = eventWearDao.queryForId(mEventId);
                mEventTitle.setText(evew.getManItem().getItemName());

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                //                System.out.println(formatter.format(javaUtilDate);
                mEventEndTime.setText(formatter.format(new Date(evew.getEstEndTime())));
                                
                //                evew.setUpdateTime(new Date().getTime());
                //                eventWearDao.createOrUpdate(evew);

                /*                List<ManEventWear> evews = eventWearDao.queryForAll();
                System.out.println("@@@@@@@@@@@@#############$$$$$$$$$$$$$$$$$$");
                for(ManEventWear ee:evews){
                    System.out.println(ee.getUpdateTime());
                }
                */
                    
                    
            } catch (SQLException e) {
                e.printStackTrace();
            }
            


            //            mFinishExecutor =  new ScheduledThreadPoolExecutor(1);
            //            mFinishFuture = mFinishExecutor.scheduleWithFixedDelay(
            //                new FinishGenerator(), 1, 5, TimeUnit.SECONDS);
        }
    }

    /*    private class FinishGenerator implements Runnable {
        @Override
        public void run() {
            
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(FINISH_PATH);
            //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
            Random rand= new Random();
            putDataMapRequest.getDataMap().putString(FINISH_KEY, mEventId+(rand.nextInt(900)+100));

            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            
            if (!mGoogleApiClient.isConnected()) {
                return;
            }
                    
            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataItemResult>() {
                        @Override
                            public void onResult(DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                System.out.println(dataItemResult.getStatus().getStatusCode());
                            }
                        }
                    });
        }
    }
    */
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
        switch(id){
        case R.id.finishConfirm:
            {
                mFinEveCnt++;
                
                System.out.println("pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp"+mFinEveCnt);

                if(mFinEveCnt>MAX_FIN_CNT){
                    System.out.println("pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    
                    mFinEveCnt = 0;

                    //                    Intent intent1 = new Intent(FinishEventActivity.this, SyncFinishEveService.class);
                    //                    startService(intent1);
                    
                    /*                    try{
            
                        QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
                        //                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().isNull(ManEventWear.UPDATETIME_FIELD_NAME);
                        queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().ne(ManEvent.UPDATETIME_FIELD_NAME,0);
                        PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();

                        //            lEventWearModel = eventWearModel;
                        finishEventWearModel = eventWearDao.query(preparedQuery1);
            
                        for(ManEventWear evew:finishEventWearModel){
                            finishEventModel.add(evew.toManEvent());
                        }

                        mFinishExecutor =  new ScheduledThreadPoolExecutor(1);
                        mFinishFuture = mFinishExecutor.scheduleWithFixedDelay(
                                                                               new FinishGenerator(), 1, 5, TimeUnit.SECONDS);
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    */
                }
                
                Intent intent = new Intent(FinishEventActivity.this, FinishEventService.class);
                //                getActivity().stopService(intent);

                intent.putExtra("eventid",mEventId);
                startService(intent);
                
                finish();
            }
            break;
        case R.id.finishCancel:
            {
                finish();
            }
            break;
        }
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
                    return;
                }
                    
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataItemResult>() {
                            @Override
                                public void onResult(DataItemResult dataItemResult) {
                                if (!dataItemResult.getStatus().isSuccess()) {
                                    System.out.println(dataItemResult.getStatus().getStatusCode());
                                }
                            }
                        });
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override //MessageListener
    public void onMessageReceived(final MessageEvent messageEvent) {
        /*        LOGD(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //                mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
            }
        });
        */

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
}

