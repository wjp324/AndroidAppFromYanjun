package com.futcore.restaurant;

import android.app.Service;

import android.net.Uri;

import java.io.UnsupportedEncodingException;

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

import com.google.android.gms.wearable.DataMap;

import android.util.Log;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


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

import com.futcore.restaurant.util.JSONReader;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManRecoItem;

import com.futcore.restaurant.models.DatabaseHelper;
import java.sql.SQLException;

public class SendRecoService extends Service  implements DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks, OnConnectionFailedListener
{
    private Handler mHandler;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItem, Integer> recoItemDao = null;
    
    //    public List<ManEvent> eventModel = null;
    //    public List<ManEvent> lEventModel = null;
    
    public List<ManRecoItem> recoItemModel = new ArrayList<ManRecoItem>();

    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String TAG = "SpiderSendService";

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String COUNT_PATH = "/count";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    private static final String FINISH_EVENT_RECEIVED_PATH = "/finish-event-received";
    private static final String SEND_RECO_RECEIVED_PATH = "/send-reco-received";
    //    private static final String IMAGE_PATH = "/image";
    private static final String COUNT_KEY = "count";

    private static final String FINISH_PATH = "/finish";
    private static final String FINISH_KEY = "finish";

    private static final String UPLOAD_FINISH_PATH = "/uploadfinish";
    private static final String UPLOAD_FINISH_KEY = "uploadfinish";

    private static final String SEND_RECO_PATH = "/sendreco";
    private static final String SEND_RECO_KEY = "sendreco";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private ScheduledExecutorService mSendRecoExecutor;
    private ScheduledFuture<?> mSendRecoFuture;


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||recoItemDao==null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                recoItemDao = helper.getRecoItemDao();
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

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
        //        return START_NOT_STICKY;
    }
    

    @Override
    public void onCreate() {
        super.onCreate();
        
        getDao();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        try{
            recoItemModel = recoItemDao.queryForAll();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        mSendRecoExecutor =  new ScheduledThreadPoolExecutor(1);
        mSendRecoFuture = mSendRecoExecutor.scheduleWithFixedDelay(
                new SendRecoGenerator(), 1, 5, TimeUnit.SECONDS);
    }
    
    @Override
    public void onDestroy() {
        //        mSpiderEveFuture.cancel(true);
        //        mServerUpdateFuture.cancel(true);
        System.out.println("ddddddddddddddddddrecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecoreco");
        mSendRecoFuture.cancel(true);
        super.onDestroy();
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


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println("lLLLLLLLLLLLLLLLLLLLLLLLLLLLGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        }
    }
    
    
    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    private class SendRecoGenerator implements Runnable {
        //        private int count = 0;

        @Override
        public void run() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;

            //cut the array to make sure it's not blocked --terry not good
            //            List<ManRecoItem> recoItemModel1 = new ArrayList<ManRecoItem>();
            //            recoItemModel1 = recoItemModel.subList(0, 20);
            //            List<ManRecoItem> recoItemModel11 = recoItemModel.subList(0, 300);
            
            //            for(ManRecoItem rr:recoItemModel11){
            //                recoItemModel1.add(rr);
            //            }
            
            //            recoItemModel1.add(new ManRecoItem(0, null, "fake reco config", 0, new Date()));
            
            //            recoItemModel.add(new ManRecoItem(0, null, "fake reco config", 0, new Date()));
            
            try{
                out = new ObjectOutputStream(bos);
                //                    ManEvent testEve = eventModel.get(0);
                //                    testEve.setEventScore(count++);
                    
                //                    out.writeObject(testEve);

                out.writeObject(recoItemModel);
                //                out.writeObject(recoItemModel1);
                byte[] yourBytes = bos.toByteArray();
                //                    System.out.println("cccccccccccccccccccccccckkkkkkkkkkkkkkkkkkkkkk");
                
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SEND_RECO_PATH);
                //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
                putDataMapRequest.getDataMap().putByteArray(SEND_RECO_KEY, yourBytes);
                PutDataRequest request = putDataMapRequest.asPutDataRequest();

                if (!mGoogleApiClient.isConnected()) {
                    return;
                }

                System.out.println("recorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecorecoreco");
                System.out.println(recoItemModel.size());
                //                System.out.println(recoItemModel1.size());
                
                Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataItemResult>() {
                            @Override
                                public void onResult(DataItemResult dataItemResult) {
                                if (!dataItemResult.getStatus().isSuccess()) {
                                    //                                        Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                    //                                              + dataItemResult.getStatus().getStatusCode());
                                }
                            }
                        });
            }
            catch(IOException e){
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // ignore close exception
                }
                try {
                    bos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // ignore close exception
                }
            }
        }
    }
    
}

