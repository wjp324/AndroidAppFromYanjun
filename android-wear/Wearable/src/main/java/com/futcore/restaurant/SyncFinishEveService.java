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
import android.os.AsyncTask;

import com.google.android.gms.wearable.DataMap;

import android.util.Log;

/*import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
*/

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

//import com.futcore.restaurant.util.JSONReader;

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
import com.futcore.restaurant.models.ManEventWear;

//import com.futcore.restaurant.models.DatabaseHelper;

import java.sql.SQLException;

import org.apache.http.HttpEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


public class SyncFinishEveService extends Service implements DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks, OnConnectionFailedListener
{
    private Handler mHandler;

    private List<ManEventWear> finishEventWearModel = new ArrayList<ManEventWear>();
    private List<ManEvent> finishEventModel = new ArrayList<ManEvent>();
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;

    private static final String FINISH_EVENT_RECEIVED_PATH = "/finish-event-received";
    
    //    public List<ManEvent> eventModel = null;
    //    public List<ManEvent> lEventModel = null;

    public List<ManEvent> eventModel = new ArrayList<ManEvent>();
    public List<ManEvent> lEventModel = new ArrayList<ManEvent>();

    public List<ManEvent> openWebEveModel = new ArrayList<ManEvent>();
    public List<ManEvent> lOpenWebEveModel = new ArrayList<ManEvent>();

    private static final String TAG = "SyncFinishEveService";

    private static final String UPLOAD_FINISH_PATH = "/uploadfinish";
    private static final String UPLOAD_FINISH_KEY = "uploadfinish";
    
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

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
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }
    

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
        
        //        clearAllCountDowns();
        
        /*        if(intent!=null){
                  if(intent.getSerializableExtra("events")!=null)
                  eventModel = (ArrayList<ManEvent>)intent.getSerializableExtra("events");
                  }
                  else{
                  getDao();

                  try{
                  QueryBuilder<ManEvent, Integer> queryBuilder = eventDao.queryBuilder();
                  queryBuilder.orderBy(ManEvent.ID_FIELD_NAME, false).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                  PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                  eventModel = eventDao.query(preparedQuery);
                  } catch (SQLException e) {
                  e.printStackTrace();
                  }
                  }
        */

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
        getDao();

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
mFinishExecutor =  new ScheduledThreadPoolExecutor(1);
            mFinishFuture = mFinishExecutor.scheduleWithFixedDelay(
                                                                   new FinishGenerator(), 1, 5, TimeUnit.SECONDS);
                        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        

        

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



        
    }
    
    private Boolean diffEventModel(List<ManEvent> mod1, List<ManEvent> mod2)
    {
        //        return true;
        if(mod1.size()!=mod2.size()){
            return true;
        }

        int i = 0;
        for(ManEvent man1:mod1){
            //            if(man1.getEventId()!=mod2.get(i).getEventId()){
            if(!man1.getEventId().equals(mod2.get(i).getEventId())){
                return true;
            }
            
            i++;
        }
        //        if(mod1.toString()!=mod2.toString())
        //            return true;
        return false;
    }

    @Override
    public void onDestroy() {
        //        mSpiderEveFuture.cancel(true);
        mFinishFuture.cancel(true);
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
        if(messageEvent.getPath().equals(FINISH_EVENT_RECEIVED_PATH)){
            if(new String(messageEvent.getData()).equals("ok")){
                System.out.println("5555555555555555555555555555555555555555555555555555555555555555555555555555555555555555");
                for(ManEventWear evew:finishEventWearModel){
                    System.out.println(evew.getManItem().getItemName());
                    
                    try{
                        int del = eventWearDao.delete(finishEventWearModel);
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("5555555555555555555555555555555555555555555555555555555555555555555555555555555555555555cccccccccccccccccccccccccccccccccccccccccc");
            }
        }

        System.out.println("tttttttttttttttttttttttttttttttttttttttttttttttttttt00000000000000000000000000000000000000000000000");

        stopSelf();
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
        }
    }
    
    
    private static void LOGD(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }


    private class FinishGenerator implements Runnable {
        @Override
        public void run() {

            System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");

            for(ManEvent eve:finishEventModel){
                System.out.println(eve.getManItem().getItemName());
            }
            
            System.out.println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffccccccccccccccc");
            
            
            
            
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
    

}

