package com.futcore.restaurant;

import android.support.v4.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

import java.text.SimpleDateFormat;


import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.futcore.restaurant.util.*;

import java.sql.SQLException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.futcore.restaurant.R;
import com.futcore.restaurant.util.JSONReader;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.view.LayoutInflater;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManRecoItem;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.futcore.restaurant.models.ManCertPlace;

import com.futcore.restaurant.models.DatabaseHelper;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import com.j256.ormlite.dao.Dao;

import com.futcore.restaurant.models.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import com.j256.ormlite.table.TableUtils;

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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.Calendar;

import android.widget.TimePicker;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

import android.graphics.PorterDuffColorFilter;

import android.graphics.drawable.Drawable;


//public class NewEventItemActivity extends SherlockFragmentActivity implements OnClickListener
public class NewEventItemActivity extends SherlockFragmentActivity implements DataApi.DataListener,
                                                                              MessageApi.MessageListener, NodeApi.NodeListener, OnClickListener, ConnectionCallbacks, OnConnectionFailedListener
                                                                              //public class NewEventItemActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener
{
    private static final String TAG = "NewEventItemActivity";

    private static final int ICRE_MODE = 1;
    private static final int FIX_MODE = 2;

    private static final String COUNT_PATH = "/count";
    //    private static final String IMAGE_PATH = "/image";
    private static final String COUNT_KEY = "count";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private ScheduledExecutorService mGeneratorExecutor;
    private ScheduledFuture<?> mDataItemGeneratorFuture;
    
    
    
	private static Dao<ManUser, Integer> userDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManCertPlace, Integer> placeDao = null;
	private static Dao<ManEvent, String> eventDao = null;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
    
    private EditText itemeventnameEdit;
    private EditText itemeventcritEdit;
    private EditText itemnminsEdit;
    private Button newitemeventsubBut;
    private Button min30But;
    private Button min60But;
    private Button min120But;

    private Button after30But;
    private Button after60But;
    private Button after90But;
    private Button after120But;

    private Button resetTimepickerBut;
    

	private TimePicker timePicker1;

    private int mHour;
    private int mMinute;
    

    //    private ManItem titem = null;
    //    private ManItem titem = null;
    //    private ManEvent tevent = null;
    private ManRecoItem mRecoItem = null;

    private Date m30After = null;
    private Date m60After = null;
    private Date m90After = null;
    private Date m120After = null;

    private long mSecOfDay;

    private int mModeFlag = ICRE_MODE;
    private Date mFixDate = null;
    private long mCurrentTime;
    
    //    private TextView mTestJson;
    //    private LinearLayout mItemEvents;
    //    private EventItemsListFragment itemList;


    //	private void doSampleDatabaseStuff(String action, TextView tv) {
    /*	private void doSampleDatabaseStuff(){
        try{
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
        List<ManUser> list = userDao.queryForAll();

        ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
        helper.clearAllUser();
        userDao.create(tuser);
        System.out.println(String.valueOf(list.size()));
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk11111");
        }
        catch(SQLException e){
        e.printStackTrace();
        }
        }
    */

    @Override
    //    protected void onDestroy() {
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
    
    //    private DBHelper getHelper() {
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(NewEventItemActivity.this, DatabaseHelper.class);
        }
        return databaseHelper;
    }    
    

    public void getDao() {
        if(userDao == null||placeDao == null||eventDao == null||itemDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(NewEventItemActivity.this);
            //            helper = getHelper();
            helper = getHelper();
            
            try {
                userDao = helper.getUserDao();
                placeDao = helper.getPlaceDao();
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSecOfDay = CommonUtil.getTimeStampOfDay(new Date().getTime());

        mCurrentTime = new Date().getTime();

        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

        
        //        Bundle extras = getIntent().getExtras();
        //        setContentView(R.layout.new_event);
        setContentView(R.layout.new_item_event);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        getDao();
        
        itemeventnameEdit = (EditText) findViewById(R.id.itemeventname);
        itemeventcritEdit = (EditText) findViewById(R.id.itemeventcrit);
        itemnminsEdit = (EditText) findViewById(R.id.itemnmins);
        newitemeventsubBut = (Button) findViewById(R.id.newitemeventsub);
        min30But = (Button) findViewById(R.id.min30but);
        min60But = (Button) findViewById(R.id.min60but);
        min120But = (Button) findViewById(R.id.min120but);

        after30But = (Button) findViewById(R.id.after30but);
        after60But = (Button) findViewById(R.id.after60but);
        after90But = (Button) findViewById(R.id.after90but);
        after120But = (Button) findViewById(R.id.after120but);

        resetTimepickerBut = (Button) findViewById(R.id.resettimepicker);
        
		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);

        newitemeventsubBut.setOnClickListener(this);
        min30But.setOnClickListener(this);
        min60But.setOnClickListener(this);
        min120But.setOnClickListener(this);

        after30But.setOnClickListener(this);
        after60But.setOnClickListener(this);
        after90But.setOnClickListener(this);
        after120But.setOnClickListener(this);

        resetTimepickerBut.setOnClickListener(this);

        fillAfterBut();


        /*		final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

        timePicker1.setCurrentHour(mHour);
		timePicker1.setCurrentMinute(mMinute);
        */
        resetTimePicker();

        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                public void onTimeChanged(TimePicker view, int selectedHour,int selectedMinute) {
                    mHour = selectedHour;
                    mMinute = selectedMinute;
 
                    // set current time into textview
                    //                    tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                    //                                          .append(":").append(pad(minute)));
 
                    // set current time into timepicker
                    //                    timePicker1.setCurrentHour(hour);
                    //                    timePicker1.setCurrentMinute(minute);
                    itemnminsEdit.setText(String.valueOf(getFastMinFromPicker(mHour, mMinute)));
                    mModeFlag = FIX_MODE;
                    resetOtherUI();

                    //                    mFixDate = m30After;
                    
                }
            });
        

        itemeventcritEdit.setVisibility(View.GONE);

        Intent intent = getIntent();
        if(intent.getSerializableExtra("recoitem")!=null){
            //            titem = (ManItem)intent.getSerializableExtra("item");
            //            tevent = (ManEvent)intent.getSerializableExtra("event");
            mRecoItem = (ManRecoItem)intent.getSerializableExtra("recoitem");
            itemeventnameEdit.setText(mRecoItem.getManItem().getItemName());
            itemnminsEdit.setText(String.valueOf(mRecoItem.getRecoDuration()/60));
            itemeventnameEdit.setKeyListener(null);
            
        }
        
    }

    private int getFastMinFromPicker(int hour, int min)  //set mFixDate also
    {
        //        long mSecOfDay = CommonUtil.getTimeStampOfDay(new Date().getTime());
        //        System.out.println("fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff+" + mSecOfDay);
        int dayOffset = 0;

        long ccSecOfDay = CommonUtil.getTimeStampOfDay(new Date().getTime());

        System.out.println("ccmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm+"+ccSecOfDay);
        System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm+"+mSecOfDay);
        System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm+"+hour+"++"+min);
        System.out.println("111mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm+"+(3600*hour+60*min));
        
        

        
        
        if(ccSecOfDay>(3600*hour+60*min)){
            ccSecOfDay -= 24*3600;
            dayOffset = 1;

            System.out.println("55555fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        }
        mFixDate = new Date(CommonUtil.getTimeFromHourMin(hour, min, dayOffset));
        
        //        return java.lang.Math.floor((ccSecOfDay-3600*hour+60*min)/60);
        return (int)(3600*hour+60*min-ccSecOfDay)/60;
    }

    /*    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
          inflater.inflate(R.menu.new_event_item, menu);
          }
    */
    @Override
    public void onResume() {
        super.onResume();
        mDataItemGeneratorFuture = mGeneratorExecutor.scheduleWithFixedDelay(
                                                                             new DataItemGenerator(), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDataItemGeneratorFuture.cancel(true /* mayInterruptIfRunning */);
    }
    

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.new_event_item, menu);
        return true;
    }
    

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onClick(View v)
    {
        //        super.onClick(v);

        //        PorterDuffColorFilter cyanFilter = new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.OVERLAY);
        PorterDuffColorFilter cyanFilter = new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
        
        int id = v.getId();
        switch(id){
        case R.id.min30but:
            {
                itemnminsEdit.setText("30");
                //                Drawable d = findViewById(id).getBackground();
                //                min30But.getBackground().setColorFilter(cyanFilter);
                //                min30But.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
                //                min30But.setBackgroundColor(Color.CYAN);
                resetOtherUI();
                min30But.setBackgroundColor(R.color.g_g);

                mModeFlag = ICRE_MODE;
            }
            break;
        case R.id.min60but:
            {
                itemnminsEdit.setText("60");
                resetOtherUI();
                min60But.setBackgroundColor(R.color.g_g);
                mModeFlag = ICRE_MODE;
            }
            break;
        case R.id.min120but:
            {
                itemnminsEdit.setText("120");
                resetOtherUI();
                min120But.setBackgroundColor(R.color.g_g);
                mModeFlag = ICRE_MODE;
            }
            break;
        case R.id.after30but:
            {
                //                itemnminsEdit.setText(String.valueOf((m30After.getTime()/1000-mSecOfDay)/60));
                itemnminsEdit.setText(String.valueOf((m30After.getTime()-mCurrentTime)/1000/60));
                resetOtherUI();
                after30But.setBackgroundColor(R.color.g_g);
                mModeFlag = FIX_MODE;
                mFixDate = m30After;
            }
            break;
        case R.id.after60but:
            {
                itemnminsEdit.setText(String.valueOf((m60After.getTime()-mCurrentTime)/1000/60));
                resetOtherUI();
                after60But.setBackgroundColor(R.color.g_g);
                mModeFlag = FIX_MODE;
                mFixDate = m60After;
            }
            break;
        case R.id.after90but:
            {
                itemnminsEdit.setText(String.valueOf((m90After.getTime()-mCurrentTime)/1000/60));
                resetOtherUI();
                after90But.setBackgroundColor(R.color.g_g);
                mModeFlag = FIX_MODE;
                mFixDate = m90After;
            }
            break;
        case R.id.after120but:
            {
                itemnminsEdit.setText(String.valueOf((m120After.getTime()-mCurrentTime)/1000/60));
                resetOtherUI();
                after120But.setBackgroundColor(R.color.g_g);
                mModeFlag = FIX_MODE;
                mFixDate = m120After;
            }
            break;
        case R.id.resettimepicker:
            {
                resetTimePicker();
            }
            break;
        case R.id.newitemeventsub:
            {
                saveEvent();
            }
            break;
        }
    }

    private void resetTimePicker()
    {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        timePicker1.setCurrentMinute(mMinute);

        timePicker1.setCurrentHour(mHour);

        timePicker1.setCurrentHour(mHour);
        timePicker1.setCurrentMinute(mMinute);

        resetOtherUI();
        mModeFlag = FIX_MODE;
        mFixDate = null;

        itemnminsEdit.setText("");
    }

    private void fillAfterBut()
    {
        int offsetSec = (int)mSecOfDay%(30*60);

        //        m30After = new Date((mSecOfDay+30*60-offsetSec)*1000);
        //        m60After = new Date((mSecOfDay+30*60*2-offsetSec)*1000);
        //        m90After = new Date((mSecOfDay+30*60*3-offsetSec)*1000);
        //        m120After = new Date((mSecOfDay+30*60*4-offsetSec)*1000);

        m30After = new Date(mCurrentTime+(30*60-offsetSec)*1000);
        m60After = new Date(mCurrentTime+(30*60*2-offsetSec)*1000);
        m90After = new Date(mCurrentTime+(30*60*3-offsetSec)*1000);
        m120After = new Date(mCurrentTime+(30*60*4-offsetSec)*1000);
        
        
        //        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        
        after30But.setText(formatter.format(m30After));
        after60But.setText(formatter.format(m60After));
        after90But.setText(formatter.format(m90After));
        after120But.setText(formatter.format(m120After));
        
    }

    private void saveEvent()
    {
        final String itemeventname = itemeventnameEdit.getText().toString().trim();
        final String itemeventcrit = itemeventcritEdit.getText().toString().trim();

        //                if(StringUtils.isEmpty(itemeventname)||StringUtils.isEmpty(itemeventcrit)||StringUtils.isEmpty(itemnminsEdit.getText().toString().trim())){

        if(mModeFlag == ICRE_MODE){
            if(StringUtils.isEmpty(itemeventname)||StringUtils.isEmpty(itemnminsEdit.getText().toString().trim())){
                AlertUtil.showAlert(this, R.string.required_fields, "Fields cannot be empty!");
                return;
            }
                
            final int eventmins = Integer.parseInt(itemnminsEdit.getText().toString().trim());

            try {
                ManUser cUser = userDao.queryForId(new Integer(1));
                ManEvent nEvent = null;
            
                byte isSpecial = itemeventname.equals(itemeventname.toLowerCase())?(byte)0:(byte)1;
            
                if(mRecoItem==null){
                    ManItem nItem = new ManItem(ItemIdUtil.getGenId("item"), cUser, itemeventname, itemeventcrit, 0, new Date(), eventmins, (byte)0, isSpecial);

                    itemDao.createIfNotExists(nItem); //not good

                    nEvent = new ManEvent(ItemIdUtil.getGenId("event"), cUser, nItem, 0, 0, 0, new Date(), new Date(new Date().getTime()+eventmins*60000), eventmins, (byte) 0);
                }
                else{
                    //                titem.setEstDuration(eventmins);
                    ManItem iitem = mRecoItem.getManItem();
                    iitem.setEstDuration(eventmins);
                    nEvent = new ManEvent(ItemIdUtil.getGenId("event"), cUser, iitem, 0, 0, 0, new Date(), new Date(new Date().getTime()+eventmins*60000), eventmins, (byte) 0);
                }
                
                //                    helper.clearAllEvent();
                    
                nEvent = eventDao.createIfNotExists(nEvent);

                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(mModeFlag == FIX_MODE){
            if(mFixDate==null){
                AlertUtil.showAlert(this, R.string.required_fields, "Fix Date is null");
                return;
            }
                
            //            final int eventmins = Integer.parseInt(itemnminsEdit.getText().toString().trim());
            int eventmins = (int)(mFixDate.getTime()-new Date().getTime())/1000/60;

            System.out.println("100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"+"++"+eventmins);

            try {
                ManUser cUser = userDao.queryForId(new Integer(1));
                ManEvent nEvent = null;
            
                byte isSpecial = itemeventname.equals(itemeventname.toLowerCase())?(byte)0:(byte)1;
            
                //                if(mRecoItem==null){   // terry no recos here
                ManItem nItem = new ManItem(ItemIdUtil.getGenId("item"), cUser, itemeventname, itemeventcrit, 0, new Date(), eventmins, (byte)0, isSpecial);

                itemDao.createIfNotExists(nItem); //not good
                    
                //                nEvent = new ManEvent(ItemIdUtil.getGenId("event"), cUser, nItem, 0, 0, 0, new Date(), new Date(new Date().getTime()+eventmins*60000), eventmins, (byte) 0);
                nEvent = new ManEvent(ItemIdUtil.getGenId("event"), cUser, nItem, 0, 0, 0, new Date(), mFixDate, eventmins, (byte) 0);

                System.out.println("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"+"+"+eventmins);
                System.out.println("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"+"+"+mFixDate.getTime());
                
                
                    //                }
                    //                else{
                    //                titem.setEstDuration(eventmins);
                    //                    ManItem iitem = mRecoItem.getManItem();
                    //                    iitem.setEstDuration(eventmins);
                    //                    nEvent = new ManEvent(ItemIdUtil.getGenId("event"), cUser, iitem, 0, 0, 0, new Date(), new Date(new Date().getTime()+eventmins*60000), eventmins, (byte) 0);
                    //                }
            

                //                    helper.clearAllEvent();
                    
                nEvent = eventDao.createIfNotExists(nEvent);

                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        
        //            showCancelAlert(true);
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            //            showCancelAlert(true);
            return true;
        }
        if (itemId == R.id.saveEvent) {
            saveEvent();
            return true;
        }
        return false;
    }

    /** Generates a DataItem based on an incrementing count. */
    private class DataItemGenerator implements Runnable {

        private int count = 0;

        @Override
        public void run() {
            
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
            putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            //            LOGD(TAG, "Generating DataItem: " + request);
            if (!mGoogleApiClient.isConnected()) {
                return;
            }
            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataItemResult>() {
                        @Override
                            public void onResult(DataItemResult dataItemResult) {
                            if (!dataItemResult.getStatus().isSuccess()) {
                                //                                Log.e(TAG, "ERROR: failed to putDataItem, status code: "                                      + dataItemResult.getStatus().getStatusCode());
                            }
                        }
                    });
        }
    }

    @Override //ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        //        LOGD(TAG, "Google API Client was connected");
        mResolvingError = false;
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
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            //            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
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
                  mDataItemListAdapter.add(
                  new Event("DataItem Changed", event.getDataItem().toString()));
                  } else if (event.getType() == DataEvent.TYPE_DELETED) {
                  mDataItemListAdapter.add(
                  new Event("DataItem Deleted", event.getDataItem().toString()));
                  }
                  }
                  }
                  });
        */
    }

    @Override //MessageListener
    public void onMessageReceived(final MessageEvent messageEvent) {
        /*        LOGD(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                  .getRequestId() + " " + messageEvent.getPath());
                  mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                  mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
                  }
                  });
        */
    }

    @Override //NodeListener
    public void onPeerConnected(final Node peer) {
        /*        LOGD(TAG, "onPeerConnected: " + peer);
                  mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                  mDataItemListAdapter.add(new Event("Connected", peer.toString()));
                  }
                  });
        */

    }

    @Override //NodeListener
    public void onPeerDisconnected(final Node peer) {
        /*        LOGD(TAG, "onPeerDisconnected: " + peer);
                  mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                  //                mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
                  }
                  });
        */
    }

    private void resetOtherUI()
    {
        min30But.setBackgroundColor(Color.LTGRAY);
        min60But.setBackgroundColor(Color.LTGRAY);
        min120But.setBackgroundColor(Color.LTGRAY);

        after30But.setBackgroundColor(Color.LTGRAY);
        after60But.setBackgroundColor(Color.LTGRAY);
        after90But.setBackgroundColor(Color.LTGRAY);
        after120But.setBackgroundColor(Color.LTGRAY);
        
        /*        min30But.setColorFilter(null);
        min30But.setColorFilter(null);
        min30But.setColorFilter(null);
        */
    }
    
    
}


