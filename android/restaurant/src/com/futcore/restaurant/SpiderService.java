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

import com.futcore.restaurant.models.DatabaseHelper;
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

import java.net.URISyntaxException;
import java.net.URI;

import me.irobo.stomp.*;
import de.roderick.weberknecht.*;

import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;

import javax.security.auth.login.LoginException;

import java.util.Map;


public class SpiderService extends Service implements DataApi.DataListener, MessageApi.MessageListener, NodeApi.NodeListener, ConnectionCallbacks, OnConnectionFailedListener
{
    private Handler mHandler;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManItem, String> itemDao = null;
    
    //    public List<ManEvent> eventModel = null;
    //    public List<ManEvent> lEventModel = null;

    public List<ManEvent> eventModel = new ArrayList<ManEvent>();
    public List<ManEvent> lEventModel = new ArrayList<ManEvent>();

    public List<ManEvent> openWebEveModel = new ArrayList<ManEvent>();
    public List<ManEvent> lOpenWebEveModel = new ArrayList<ManEvent>();

    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String TAG = "SpiderSendService";

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String COUNT_PATH = "/count";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    private static final String FINISH_EVENT_RECEIVED_PATH = "/finish-event-received";
    //    private static final String IMAGE_PATH = "/image";
    private static final String COUNT_KEY = "count";

    private static final String FINISH_PATH = "/finish";
    private static final String FINISH_KEY = "finish";

    private static final String UPLOAD_FINISH_PATH = "/uploadfinish";
    private static final String UPLOAD_FINISH_KEY = "uploadfinish";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Send DataItems.
    private ScheduledExecutorService mSpiderEveExecutor;
    private ScheduledFuture<?> mSpiderEveFuture;

    private ScheduledExecutorService mServerUpdateExecutor;
    private ScheduledFuture<?> mServerUpdateFuture;



    private StompClient mClient;
    private String mChannel = "/spider/topic/getwebevents";
    
    public static final String SPIDER_HOST = "121.40.94.93";
    

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
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
        
        mSpiderEveExecutor =  new ScheduledThreadPoolExecutor(1);
        mSpiderEveFuture = mSpiderEveExecutor.scheduleWithFixedDelay(
                new SpiderGenerator(), 1, 5, TimeUnit.SECONDS);

        /*        mServerUpdateExecutor =  new ScheduledThreadPoolExecutor(1);
        mServerUpdateFuture = mServerUpdateExecutor.scheduleWithFixedDelay(
                new ServerUpdate(), 1, 10, TimeUnit.SECONDS);
        */

        //        String ip = "ws://121.40.94.93:8080/spider/hello";
        //        int port = 80;

        new TestWebSocketTask().execute();
        
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

    /*    private class ServerUpdate implements Runnable
    {
        @Override
        public void run() {
            
            String json = "";
            try {
                json = JSONReader.readUrl("http://spider.ebaystratus.com/spider/getwebevents");

                GsonBuilder gsonBuilder=  new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong()); 
                        } 
                    });
                
                Gson gson = gsonBuilder.create();

                List<ManEvent> recos = new ArrayList<ManEvent>();
                Type recoType = new TypeToken<List<ManEvent>>() {}.getType();

                openWebEveModel = gson.fromJson(json, recoType);

                if(diffEventModel(openWebEveModel, lOpenWebEveModel)){
                    for(ManEvent eve:openWebEveModel){
                        itemDao.createIfNotExists(eve.getManItem());
                        eventDao.createIfNotExists(eve);


                    }
                }

                lOpenWebEveModel = openWebEveModel;
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //            return json;
        }
    }
    */
    
    private class SpiderGenerator implements Runnable {
        //        private int count = 0;
        @Override
        public void run() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;

            try{
                QueryBuilder<ManEvent, String> queryBuilder = eventDao.queryBuilder();
                queryBuilder.orderBy(ManEvent.EST_END_TIME_FIELD_NAME, false).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                eventModel = eventDao.query(preparedQuery);

                //                if(eventModel.size()>0){
                if(diffEventModel(eventModel, lEventModel)){
                //                if(false){
                    out = new ObjectOutputStream(bos);
                    out.writeObject(eventModel);
                    byte[] yourBytes = bos.toByteArray();
                    

                    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
                    //                    putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
                    putDataMapRequest.getDataMap().putByteArray(COUNT_KEY, yourBytes);
                    PutDataRequest request = putDataMapRequest.asPutDataRequest();

                    if (!mGoogleApiClient.isConnected()) {
                        return;
                    }
                    
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
                
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            finally {
                lEventModel = eventModel;
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
    

    @Override
    public void onDestroy() {
        mSpiderEveFuture.cancel(true);
        mServerUpdateFuture.cancel(true);
        
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

        LOGD(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if(!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "SpiderDataListenerService failed to connect to GoogleApiClient.");
                return;
            }
        }

        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();

            
            String path = uri.getPath();

            if(UPLOAD_FINISH_PATH.equals(path)){
                
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();


                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(UPLOAD_FINISH_KEY);


                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);

                    List<ManEvent> fEventModel = (List<ManEvent>)in.readObject();
                    //                    eventModel = (List<ManEvent>)in.readObject();


                    try{
                        for(ManEvent feve:fEventModel){
                            eventDao.createOrUpdate(feve);
                            //                        eventDao.c
                        }

                        new PushEventsTask().execute();
                        //                        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, FINISH_EVENT_RECEIVED_PATH, payload);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, FINISH_EVENT_RECEIVED_PATH, new String("ok").getBytes());
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                
                // Send the rpc
                //                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, FINISH_EVENT_RECEIVED_PATH, payload);
            }
            else if(FINISH_PATH.equals(path)){

                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();


                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(FINISH_KEY);


                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);

                    List<ManEvent> fEventModel = (List<ManEvent>)in.readObject();
                    //                    eventModel = (List<ManEvent>)in.readObject();

                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH, payload);
                
                /*                byte[] fEveIdB = DataMap.fromByteArray(event.getDataItem().getData()).get(FINISH_KEY);
                try{
                    
                    String fEveId = new String(fEveIdB,"UTF-8");
                    ManEvent eee = eventDao.queryForId(fEveId);
                    eee.setUpdateTime(new Date());
                    eventDao.createOrUpdate(eee);
                    
                } catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                */
                
            }
            
            /*            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();


                byte[] eventss = DataMap.fromByteArray(event.getDataItem().getData()).get(COUNT_KEY);


                ByteArrayInputStream bis = new ByteArrayInputStream(eventss);
                ObjectInput in = null;
                
                try {
                    in = new ObjectInputStream(bis);
                    eventModel = (List<ManEvent>)in.readObject();

                    Collections.sort(eventModel, new EventEndComparator());                    

                    ManEvent tevent = eventModel.get(mMainIndex);

                    long ttdate = tevent.getEstEndTime().getTime() - new Date().getTime();

                    sendNotification(eventModel.get(mMainIndex).getManItem().getItemName()+(eventModel.size()>1?" ("+eventModel.size()+")":""), ttdate, NOTI_NORMAL);

                    
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        bis.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // ignore close exception
                    }
                }
                
                // Send the rpc
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
                        }*/
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


    private class PushEventsTask extends AsyncTask<String, Void, String> {
        protected PushEventsTask(){
        }
        
        protected String doInBackground(String... params)
        {
            //            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpClient httpClient = new DefaultHttpClient();

            
            try{
                List<ManEvent> eves = eventDao.queryForAll();
                
                //                String postUrl="http://spider.ebaystratus.com/spider/insertevents";// put in your url
                String postUrl="http://"+SPIDER_HOST+"/spider/insertevents";// put in your url
                //                Gson gson= new Gson();
                Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                HttpPost post = new HttpPost(postUrl);
                StringEntity  postingString =new StringEntity(gson.toJson(eves)); //convert your pojo to   json
                post.setEntity(postingString);
                post.setHeader("Content-type", "application/json");
                HttpResponse response = httpClient.execute(post);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                // TODO Auto-generated catch block                
                //            } catch (Exception ex) {
                //                ex.printStackTrace();
            } catch(UnsupportedEncodingException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
            finally{
                //                httpClient.close();
            }
            
            return "ccc";
            
            //            return json;
        }
        
        protected void onPostExecute(String result) {
            //            AlertUtil.showAlert(UpdateAllActivity.this, R.string.required_fields, "Events Pushed");
            super.onPostExecute(result);
        }
    }


    private class TestWebSocketTask extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... urls)
        {
            //            String ip = "http://121.40.94.93/spider/hello";
            //            String ip = "http://121.40.94.93:8080/spider/hello";
            //            int port = 8080;
            //            String ip = "121.40.94.93/spider/hello";
            String ip = "ws://"+SPIDER_HOST+":8080/spider/hello";
            int port = 80;

            try {
                //            c = new Client( ip, port, "", "" );
                mClient = new StompClient( ip, port, "", "" );
                //            Log.i("Stomp", "Connection established");
                //            c.subscribe( channel, new Listener() {
                mClient.subscribe( mChannel, new StompListener() {
                        public void message( Map header, String message ) {

                            //                            String json = "";
                            String json = message;
                            try {
                                //                                json = JSONReader.readUrl("http://spider.ebaystratus.com/spider/getwebevents");

                                GsonBuilder gsonBuilder=  new GsonBuilder();

                                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                        return new Date(json.getAsJsonPrimitive().getAsLong()); 
                                    } 
                                });
                
                                Gson gson = gsonBuilder.create();

                                List<ManEvent> recos = new ArrayList<ManEvent>();
                                Type recoType = new TypeToken<List<ManEvent>>() {}.getType();

                                List<ManEvent> openopenModel = gson.fromJson(json, recoType);
                                //                                if(diffEventModel(openWebEveModel, lOpenWebEveModel)){
                                for(ManEvent eve:openopenModel){
                                    itemDao.createIfNotExists(eve.getManItem());
                                    eventDao.createIfNotExists(eve);
                                }
                                    //                                }
                                //                                lOpenWebEveModel = openWebEveModel;
                
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            
                            //                            System.out.println("11111111111111111!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+message);
                            //Log.i("Stomp", "Message received!!!");
                        }
                    });

            } catch (IOException ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();

            } catch (LoginException ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();
            } catch (Exception ex) {
                //            Log.e("Stomp", ex.getMessage());
                ex.printStackTrace();
            }

            return "fake";
        }

        protected void onPostExecute(String result)
        {
        }
    }
}
