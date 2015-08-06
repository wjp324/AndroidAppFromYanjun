package com.futcore.restaurant.ui;

import android.support.v4.app.FragmentManager;

import com.futcore.restaurant.SpiderService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.DeleteBuilder;


import org.apache.http.util.EntityUtils;


import android.content.Context;

import com.futcore.restaurant.util.AlertUtil;

import java.sql.SQLException;

import com.futcore.restaurant.SendRecoService;

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

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManRecoItem;


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

//public class UpdateAllActivity extends SherlockFragmentActivity implements OnClickListener
//public class UpdateAllActivity extends SherlockFragmentActivity implements OnClickListener
public class UpdateAllActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener
{
	private static Dao<ManUser, Integer> userDao = null;
	private static Dao<ManRecoItem, Integer> recoItemDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManCertPlace, Integer> placeDao = null;
    private static DatabaseHelper helper;

    Intent sendRecoService = null;

    //    private GetRecoItemTask mGetRecoItemTask = null;
    private PushEventsTask mPushEventsTask = null;

    //    private TextView mTestJson;
    //    private LinearLayout mItemEvents;
    //    private EventItemsListFragment itemList;

    //    public void someMethod() {
    public void getDao() {
        if(userDao == null||placeDao == null||recoItemDao ==null||itemDao == null||eventDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            helper = (DatabaseHelper) OpenHelperManager.getHelper(UpdateAllActivity.this);
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

    //	private void doSampleDatabaseStuff(String action, TextView tv) {
	private void doSampleDatabaseStuff(){
		// get our dao
        //		RuntimeExceptionDao<ManUser, Integer> userDao = getHelper().getManUserDao();
		// query for all of the data objects in the database
        try{
            //            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
            //            List<ManUser> list = userDao.queryForAll();

            ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
            helper.clearAllUser();
			userDao.create(tuser);
            //            System.out.println(String.valueOf(list.size()));
            //            System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk11111");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
        
        
		// our string builder for building the content-view
        //		StringBuilder sb = new StringBuilder();
        //		sb.append("got ").append(list.size()).append(" entries in ").append(action).append("\n");

		// if we already have items in the database
        /*		int simpleC = 0;
		for (ManUser simple : list) {
			sb.append("------------------------------------------\n");
			sb.append("[").append(simpleC).append("] = ").append(simple).append("\n");
			simpleC++;
		}
		sb.append("------------------------------------------\n");
		for (ManUser simple : list) {
			userDao.delete(simple);
			sb.append("deleted id ").append(simple.id).append("\n");
			Log.i(LOG_TAG, "deleting simple(" + simple.id + ")");
			simpleC++;
		}

		int createNum;
		do {
			createNum = new Random().nextInt(3) + 1;
		} while (createNum == list.size());
		for (int i = 0; i < createNum; i++) {
			// create a new simple object
			long millis = System.currentTimeMillis();
			ManUser simple = new ManUser(millis);
			// store it in the database
			userDao.create(simple);
			Log.i(LOG_TAG, "created simple(" + millis + ")");
			// output it
			sb.append("------------------------------------------\n");
			sb.append("created new entry #").append(i + 1).append(":\n");
			sb.append(simple).append("\n");
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// ignore
			}
		}
        */

        //		tv.setText(sb.toString());
        //		Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
	}
    
    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //        Bundle extras = getIntent().getExtras();
        //        setContentView(R.layout.new_event);
        setContentView(R.layout.update_all);
        
        checkAndStartSpiderService();
        
        /*        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        */

        //        new GetCertPlaceTask().execute();
        getDao();
        
        //        mGetRecoItemTask = (GetRecoItemTask) new GetRecoItemTask().execute();
        mPushEventsTask = (PushEventsTask) new PushEventsTask().execute();


        /*        sendRecoService = new Intent(this, SendRecoService.class);
        stopService(sendRecoService);
        startService(sendRecoService);
        */
        
        //        doSampleDatabaseStuff();
        //        FragmentManager fm = getSupportFragmentManager();
        //        itemList = (EventItemsListFragment) fm.findFragmentById(R.id.eventItemList);
        //        itemList.refreshItems(false,false,false);
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
        //        stopService(sendRecoService);
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
                String postUrl="http://"+SpiderService.SPIDER_HOST+"/spider/insertevents";// put in your url
                //                Gson gson= new Gson();
                Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                
                HttpPost post = new HttpPost(postUrl);
                StringEntity  postingString =new StringEntity(gson.toJson(eves)); //convert your pojo to   json
                post.setEntity(postingString);
                post.setHeader("Content-type", "application/json");
                HttpResponse response = httpClient.execute(post);            
                
                System.out.println("#################################@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println(gson.toJson(eves));
                System.out.println(eves.size());

                return EntityUtils.toString(response.getEntity());
                
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
            
            /*            String json = "";
            try {
                json = JSONReader.readUrl("http://spider.ebaystratus.com/spider/testrecos");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
            return "post fails";
            //            return json;
        }
        
        protected void onPostExecute(String result) {
            AlertUtil.showAlert(UpdateAllActivity.this, R.string.required_fields, "Events Pushed :"+result);
            if(result.startsWith("ok")){
                try{
                    DeleteBuilder<ManEvent, String> deleteBuilder = eventDao.deleteBuilder();
                    deleteBuilder.where().isNotNull(ManEvent.UPDATETIME_FIELD_NAME);
                    PreparedDelete<ManEvent> preparedDelete = deleteBuilder.prepare();
                    int res = eventDao.delete(preparedDelete);

                    DeleteBuilder<ManEvent, String> deleteBuilder1 = eventDao.deleteBuilder();
                    deleteBuilder1.where().eq(ManEvent.DELETE_FIELD_NAME, ManEvent.DELETE_TYPE_PLAN).and().gt(ManEvent.EST_END_TIME_FIELD_NAME, new Date());
                    PreparedDelete<ManEvent> preparedDelete1 = deleteBuilder1.prepare();
                    int res1 = eventDao.delete(preparedDelete1);
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                
                //                eventDao                
            }
            
            super.onPostExecute(result);
        }
    }

    /*    private class GetRecoItemTask extends AsyncTask<String, Void, String> {
        protected GetRecoItemTask(){
        }
        
        protected String doInBackground(String... params)
        {
            String json = "";
            try {
                json = JSONReader.readUrl("http://spider.ebaystratus.com/spider/testrecos");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return json;
        }
        
        protected void onPostExecute(String result) {
            //            if(result==null){
            //            AlertUtil.showAlert(UpdateAllActivity.this, R.string.error, result);
            
            if(result.equals("")){
                AlertUtil.showAlert(UpdateAllActivity.this, R.string.error, R.string.connection_error_occured);
            }
            else{
                GsonBuilder gsonBuilder=  new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong()); 
                        } 
                    });
                
                Gson gson = gsonBuilder.create();

                //                List<ManCertPlace> places = new ArrayList<ManCertPlace>();
                //                Type placeType = new TypeToken<List<ManCertPlace>>() {}.getType();

                List<ManRecoItem> recos = new ArrayList<ManRecoItem>();
                Type recoType = new TypeToken<List<ManRecoItem>>() {}.getType();

                recos = gson.fromJson(result, recoType);

                System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
                System.out.println(recos.size());

                try{
                    //                    List<ManUser> list = userDao.queryForAll();
                    
                    //                    ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
                    //                    helper.clearAllUser();
                    //                    helper.clearAllPlace();
                    //                    helper.clearAllItem();
                    helper.clearAllRecoItem();
                    //                    System.out.println(String.valueOf(list.size()));
                    //                    List<ManCertPlace> places = new ArrayList<ManCertPlace>();
                    
                    //                    for(ManCertPlace pl:places){
                    //                        placeDao.create(pl);
                    //                    }
                    for(ManRecoItem reco:recos){
                        itemDao.createIfNotExists(reco.getManItem());
                        recoItemDao.create(reco);
                    }
                    
                    List<ManRecoItem> listt = recoItemDao.queryForAll();
                    System.out.println(String.valueOf(listt.size()));
                    System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr22222222");
                }
                catch(SQLException e){
                    e.printStackTrace();
                }

                AlertUtil.showAlert(UpdateAllActivity.this, R.string.required_fields, "Model Updated");
                
                
            }
            super.onPostExecute(result);
        }
    }
    */
                

    private class GetCertPlaceTask extends AsyncTask<String, Void, String> {
        //        private Integer mStart;
        //        private int mStart;
        //        private Boolean mShouldRefresh;

        //        IItemsReadyListener listener;

        /*        protected GetItemTask(IItemsReadyListener listener, Boolean shouldRefresh){
            this.listener = listener;
            mShouldRefresh = shouldRefresh;
        }

        protected GetItemTask(IItemsReadyListener listener){
            this.listener = listener;
            mShouldRefresh = false;
        }
        */

        protected GetCertPlaceTask(){
            //            this.listener = null;
            //            mShouldRefresh = false;
        }
        
        //        protected Map<Integer, Map<?, ?>> doInBackground(Map<String,?>... params)
        protected String doInBackground(String... params)
        {
            String json = "";
            try {
                //                json = JSONReader.readJsonArrayFromUrl("http://spider.ebaystratus.com/man/getitems");
                //                json = JSONReader.readUrl("http://spider.ebaystratus.com/man/getitems");
                json = JSONReader.readUrl("http://spider.ebaystratus.com/man/testplaces");

                //                mTestJson.setText(json.toString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return json;
        }
        
        protected void onPostExecute(String result) {
            //            if(result==null){
            //            AlertUtil.showAlert(UpdateAllActivity.this, R.string.error, result);
            
            if(result.equals("")){
                AlertUtil.showAlert(UpdateAllActivity.this, R.string.error, R.string.connection_error_occured);
            }
            else{
                GsonBuilder gsonBuilder=  new GsonBuilder();

                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong()); 
                        } 
                    });
                
                Gson gson = gsonBuilder.create();

                List<ManCertPlace> places = new ArrayList<ManCertPlace>();
                Type placeType = new TypeToken<List<ManCertPlace>>() {}.getType();

                places = gson.fromJson(result, placeType);



                try{
                    System.out.println("gggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
                    //                    List<ManUser> list = userDao.queryForAll();
                    
                    ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
                    helper.clearAllUser();
                    helper.clearAllPlace();
                    userDao.createIfNotExists(tuser);
                    //                    System.out.println(String.valueOf(list.size()));
                    System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggg111111111");
                    //                    List<ManCertPlace> places = new ArrayList<ManCertPlace>();
                    
                    for(ManCertPlace pl:places){
                        placeDao.create(pl);
                    }
                    
                    List<ManCertPlace> listt = placeDao.queryForAll();
                    System.out.println(String.valueOf(listt.size()));
                    System.out.println(listt.get(0).getPlaceName());
                    System.out.println(listt.get(1).getPlaceName());
                    System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggg22222222");
                }
                catch(SQLException e){
                    e.printStackTrace();
                }

                AlertUtil.showAlert(UpdateAllActivity.this, R.string.required_fields, "Model Updated");
                
                
                /*                if(mShouldRefresh)
                    itemModel = new ArrayList<ManItem>();

                Type itemType = new TypeToken<List<ManItem>>() {}.getType();
                //                Gson gson = new Gson(); 
                //                Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                GsonBuilder gsonBuilder=  new GsonBuilder();

                // Register an adapter to manage the date types as long values 
                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong()); 
                        } 
                    });
                Gson gson = gsonBuilder.create();
               
                itemModel = gson.fromJson(result, itemType);
                    
                if(mShouldRefresh)
                {
                    sAdapter = new DemoAdapter(UpdateAllActivity.this,itemModel);
                    sAdapter.setRunInBackground(false);
                    //            promoList.setAdapter(adapter);
                    setListAdapter(sAdapter);
                }

                if(!mShouldRefresh){
                    //                listener.onItemsReady(cModel);
                    listener.onItemsReady();
                }
                */
            }
            //            getPullToRefreshListView().onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    private void checkAndStartSpiderService()
    {
        if(!isMyServiceRunning(SpiderService.class)){
            System.out.println("+++++++++++++++++++++===================================+++++++++++++++++++++++++++++++++++++++++");
            Intent intent = new Intent(this, SpiderService.class);
            stopService(intent);
            startService(intent);
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
        //        mGetRecoItemTask.cancel(true);
        mPushEventsTask.cancel(true);
    }
    
}


