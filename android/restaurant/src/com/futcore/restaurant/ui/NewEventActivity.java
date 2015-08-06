package com.futcore.restaurant.ui;

import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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

import com.google.gson.Gson;

import com.futcore.restaurant.models.ManItem;


public class NewEventActivity extends SherlockFragmentActivity implements OnClickListener
{
    private TextView mTestJson;
    private LinearLayout mItemEvents;

    private EventItemsListFragment itemList;

    
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //        Bundle extras = getIntent().getExtras();

        //        setContentView(R.layout.new_event);
        setContentView(R.layout.event_items);
        
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);


        FragmentManager fm = getSupportFragmentManager();
        itemList = (EventItemsListFragment) fm.findFragmentById(R.id.eventItemList);
        itemList.refreshItems(false,false,false);

        //        mTestJson = (TextView)findViewById(R.id.test_json);
        //        mItemEvents = (LinearLayout)findViewById(R.id.item_events);
                    
        //        GetItemTask testTask = new GetItemTask();
        //        testTask.execute();
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

    /*    private class GetItemTask extends AsyncTask<String, Void, JSONArray> {
        private int mStart;
        private Boolean mShouldRefresh;

        IItemsReadyListener listener;

        protected GetItemTask(IItemsReadyListener listener, Boolean shouldRefresh){
            this.listener = listener;
            mShouldRefresh = shouldRefresh;
        }

        protected GetItemTask(IItemsReadyListener listener){
            this.listener = listener;
            mShouldRefresh = false;
        }

        protected GetItemTask(){
            this.listener = null;
            mShouldRefresh = false;
        }

        //        protected Map<Integer, Map<?, ?>> doInBackground(String... params){
        protected JSONArray doInBackground(String... params){

            JSONArray json = null;
            try {
                json = JSONReader.readJsonArrayFromUrl("http://spider.ebaystratus.com/man/getitems");


                Gson gson = new Gson();

                String jsonStr = JSONReader.readUrl("http://spider.ebaystratus.com/man/getitems");
                List<ManItem> obj = gson.fromJson(jsonStr, new ArrayList<ManItem>().getClass());
                System.out.println(obj);
                
                //                mTestJson.setText(json.toString());
                System.out.println("cccccccccccccccc"+json.toString());
                System.out.println("xxxxxxxxxxxxxxxxxx"+obj);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
            return json;
        }

        protected void onPostExecute(JSONArray result) {
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj;
				try {
					obj = result.getJSONObject(i);

                    ViewGroup layout = (ViewGroup) findViewById(R.id.item_events);
                    //                    TextView tv = new TextView(NewEventActivity.this);
                    Button tv = new Button(NewEventActivity.this);
                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    tv.setText(obj.get("itemName").toString());
                    layout.addView(tv);
                    
                    //                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //                    inflater.inflate(R.layout.single_item_event, mItemEvents);
                    
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            try {
                //				mTestJson.setText(result.getJSONObject(0).get("itemName").toString());
                System.out.println("bbbbbbbb"+result.getJSONObject(0).get("itemName").toString());
                //                mTestJson.setText("kkkkkk");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
    }


    public void refreshItems(final boolean more, final boolean refresh, final boolean background, List<String> params)
    {
        //        if(params!=null){
        //            orderBy = params.get(0);
        //            order = params.get(1);
        //        }
        //        new GetItemTask(null, true).execute(itemId.toString(), "0", orderBy, order);
        new GetItemTask().execute();
    }

    public void refreshItems(final boolean more, final boolean refresh, final boolean background)
    {
        refreshItems(more, refresh, background, null);
    }
    

    interface IItemsReadyListener {
        //        public void onItemsReady(ArrayList<Shop> data);
        public void onItemsReady();
    }
    */
}


