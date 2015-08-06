package com.futcore.restaurant.ui;

import android.support.v4.app.FragmentManager;

import com.futcore.restaurant.SpiderService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.DeleteBuilder;


import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Context;

import com.futcore.restaurant.util.AlertUtil;

import java.sql.SQLException;

//import com.futcore.restaurant.SendRecoService;
import com.futcore.restaurant.SendNewRecoService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.content.Intent;
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

import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import android.view.LayoutInflater;

import com.futcore.restaurant.models.*;

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

//public class UpdateRecoActivity extends SherlockFragmentActivity implements OnClickListener
//public class UpdateRecoActivity extends SherlockFragmentActivity implements OnClickListener
public class UpdateRecoActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener
{
    //    private static final String UPDATE_RECO_URL = "http://spider.ebaystratus.com/spider-touch/lastreco.json";
    private static final String UPDATE_RECO_URL = "http://121.40.94.93/spider-touch/lastreco.json";
    
    
	private static Dao<ManUser, Integer> userDao = null;
	private static Dao<ManRecoItem, Integer> recoItemDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManCertPlace, Integer> placeDao = null;
    private static DatabaseHelper helper;

    Intent sendNewRecoService = null;

    private GetRecoItemTask mGetRecoItemTask = null;

    private Map<Long, List<ManItemScore>> mRecos = new HashMap<Long, List<ManItemScore>>();
    
//    private PushEventsTask mPushEventsTask = null;

    //    private TextView mTestJson;
    //    private LinearLayout mItemEvents;
    //    private EventItemsListFragment itemList;

    //    public void someMethod() {
    public void getDao() {
        if(userDao == null||placeDao == null||recoItemDao ==null||itemDao == null||eventDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            helper = (DatabaseHelper) OpenHelperManager.getHelper(UpdateRecoActivity.this);
            //            helper = getHelper();
            try {
                userDao = helper.getUserDao();
                placeDao = helper.getPlaceDao();
                recoItemDao = helper.getRecoItemDao();
                itemDao = helper.getItemDao();
                eventDao = helper.getEventDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_reco);
        
        getDao();
        
        mGetRecoItemTask = (GetRecoItemTask) new GetRecoItemTask().execute();
        
        //        sendRecoService = new Intent(this, SendRecoService.class);
        //        stopService(sendRecoService);
        //        startService(sendRecoService);

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
        switch(id){
        }
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            //            showCancelAlert(true);
            return true;
        }
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        
        if(isMyServiceRunning(SendNewRecoService.class)){
            stopService(sendNewRecoService);
        }
    }
        
    private class GetRecoItemTask extends AsyncTask<String, Void, String> {
        protected GetRecoItemTask(){
        }
        
        protected String doInBackground(String... params)
        {
            String json = "";
            try {
                //                json = JSONReader.readUrl("http://spider.ebaystratus.com/spider/testrecos");
                json = JSONReader.readUrl(UPDATE_RECO_URL);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return json;
        }

        
        
        protected void onPostExecute(String result) {
            if(result.equals("")){
                AlertUtil.showAlert(UpdateRecoActivity.this, R.string.error, R.string.connection_error_occured);
            }
            else{
                AlertUtil.showAlert(UpdateRecoActivity.this, R.string.error, result);
                //                System.out.println(result);
                GsonBuilder gsonBuilder=  new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong()); 
                    } 
                });
                
                Gson gson = gsonBuilder.create();

                /*                List<ManRecoItem> recos = new ArrayList<ManRecoItem>();
                Type recoType = new TypeToken<List<ManRecoItem>>() {}.getType();

                recos = gson.fromJson(result, recoType);
                */
                
                Type recoType = new TypeToken<Map<Long, List<ManItemScore>>>() {}.getType();

                mRecos = gson.fromJson(result, recoType);

                sendNewRecoService = new Intent(UpdateRecoActivity.this, SendNewRecoService.class);
                Bundle extras = new Bundle();
                extras.putSerializable("recos",(HashMap<Long, List<ManItemScore>>)mRecos);
                sendNewRecoService.putExtras(extras);
        
                stopService(sendNewRecoService);
                System.out.println("222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222");
                startService(sendNewRecoService);

                
                /*                try{
                    helper.clearAllRecoItem();
                    for(ManRecoItem reco:recos){
                        itemDao.createIfNotExists(reco.getManItem());
                        recoItemDao.create(reco);
                    }
                    
                    List<ManRecoItem> listt = recoItemDao.queryForAll();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                */

                //                AlertUtil.showAlert(UpdateRecoActivity.this, R.string.required_fields, "Model Updated");

                AlertUtil.showAlert(UpdateRecoActivity.this, R.string.required_fields, String.valueOf(mRecos.size()));
            }
            super.onPostExecute(result);
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
    

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGetRecoItemTask.cancel(true);
        //        mPushEventsTask.cancel(true);
    }
}


