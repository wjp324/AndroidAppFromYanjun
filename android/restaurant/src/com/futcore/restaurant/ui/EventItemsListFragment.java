package com.futcore.restaurant.ui;


import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;


import com.android.volley.toolbox.NetworkImageView;

import com.futcore.restaurant.Constants;
import com.futcore.restaurant.WordPress;
import com.futcore.restaurant.models.Promo;
import com.futcore.restaurant.models.Shop;
import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.util.AlertUtil;
import com.futcore.restaurant.util.StringUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//
//
//
//

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import com.futcore.restaurant.R;


import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;

import com.commonsware.cwac.endless.EndlessAdapter;

import com.futcore.restaurant.util.JSONReader;
import com.google.gson.Gson;

public class EventItemsListFragment extends PullToRefreshListFragment {
    //public class EventItemsListFragment
    private String errorMsg = "";
    public List<ManItem> itemModel = new ArrayList<ManItem>();
    private PullToRefreshListView mPullToRefreshListView;

    DemoAdapter sAdapter = null;

    private String orderBy = "";
    private String order = "";

    public PullToRefreshListView onCreatePullToRefreshListView(LayoutInflater inflater,Bundle savedInstanceState)
    {
        PullToRefreshListView listView = super.onCreatePullToRefreshListView(inflater, savedInstanceState);

        ProgressBar progressBar = new ProgressBar(getActivity());

        progressBar.setLayoutParams(new LayoutParams(300, 300, Gravity.CENTER));
        
        progressBar.setIndeterminate(true);
        
        listView.setEmptyView(progressBar);
        
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    //                    new GetItemTask(null, true).execute("0", orderBy, order);
                    //                    new GetItemTask().execute();
                    new GetItemTask(null, true).execute("0", "","");
                }
            });
        
        return listView;
    }


    public void onListItemClick(ListView l, View v, int position, long id)
    {
        ManItem item = itemModel.get((int)id);
        AlertUtil.showAlert(getActivity(), R.string.required_fields, item.getItemName());
    }

    //    private class GetItemTask extends AsyncTask<Map<String,?>, Void, Map<Integer, Map<?, ?>>> {
    private class GetItemTask extends AsyncTask<String, Void, String> {
        //        private Integer mStart;
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
        
        //        protected Map<Integer, Map<?, ?>> doInBackground(Map<String,?>... params)
        protected String doInBackground(String... params)
        {
            String json = "";
            try {
                //                json = JSONReader.readJsonArrayFromUrl("http://spider.ebaystratus.com/man/getitems");
                json = JSONReader.readUrl("http://spider.ebaystratus.com/man/getitems");
                //                mTestJson.setText(json.toString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return json;
        }
        
        protected void onPostExecute(String result) {
            //            if(result==null){
            if(result.equals("")){
                AlertUtil.showAlert(getActivity(), R.string.error, R.string.connection_error_occured);
            }
            else{
                if(mShouldRefresh)
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
                    sAdapter = new DemoAdapter(getActivity(),itemModel);
                    sAdapter.setRunInBackground(false);
                    //            promoList.setAdapter(adapter);
                    setListAdapter(sAdapter);
                }

                if(!mShouldRefresh){
                    //                listener.onItemsReady(cModel);
                    listener.onItemsReady();
                }
            }

            getPullToRefreshListView().onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    class DemoAdapter extends EndlessAdapter implements IItemsReadyListener{
        private RotateAnimation rotate=null;
        private View pendingView=null;
  
        DemoAdapter(Context ctxt, List<ManItem> list ) {
            super(new ItemAdapter(list));
        
            rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setRepeatMode(Animation.RESTART);
            rotate.setRepeatCount(Animation.INFINITE);
        }
  
        @Override
        protected View getPendingView(ViewGroup parent) {
            //            System.out.println("wawawawawa");
            View row=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);

            ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.throbber);
            progressBar.setIndeterminate(true);
    
            return(row);
        }

        @Override
        protected boolean cacheInBackground() {
            new GetItemTask(this).execute(String.valueOf(itemModel.size()), orderBy, order);
            
            return(getWrappedAdapter().getCount()<50);
        }

        //        public void onItemsReady(ArrayList<Item> data) {
        public void onItemsReady() {
            sAdapter.onDataReady(); // Tell the EndlessAdapter to
        }        
  
        @Override
        protected void appendCachedData() {
        }
  
        void startProgressAnimation() {
            if (pendingView!=null) {
                pendingView.startAnimation(rotate);
            }
        }

    }

    class ItemAdapter extends ArrayAdapter<ManItem>
    {
        private final List<ManItem> model;
        
        ItemAdapter(List<ManItem> model){
            super(getActivity().getApplicationContext(), R.layout.event_item_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.event_item_row, parent, false);
            //            NetworkImageView thumb = (NetworkImageView) rowView.findViewById(R.id.itemIcon);
            TextView itemTitle = (TextView)rowView.findViewById(R.id.eventItemTitle);
            System.out.println(model);
            ManItem item = (ManItem)model.get(position);
            itemTitle.setText(item.getItemName());
            //            TextView itemTags = (TextView)rowView.findViewById(R.id.itemTags);
            //            thumb.setImageUrl(Constants.baseURL+model.get(position).thumbUrl.toString(), WordPress.imageLoader);
            //            itemTitle.setText(model.get(position).itemName.toString());
            //            itemTags.setText(model.get(position).getItemTagsString());
            return rowView;
        }

        public void testAdd()
        {
            for (int i=0;i<8;i++) { this.model.add(this.model.get(0)); }
        }
    }

    public boolean pullInit()
    {
        getPullToRefreshListView().setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    new GetItemTask(null, true).execute("0", orderBy, order);
                }
            });
        return true;
    }

    public boolean loadItems(boolean refresh, boolean loadMore)
    {
        new GetItemTask(null, false).execute("0", orderBy, order);
        return true;
    }
    
    public void refreshItems(final boolean more, final boolean refresh, final boolean background, List<String> params)
    {
        if(params!=null){
            orderBy = params.get(0);
            order = params.get(1);
        }
                
        new GetItemTask(null, true).execute("0", orderBy, order);
    }

    public void refreshItems(final boolean more, final boolean refresh, final boolean background)
    {
        refreshItems(more, refresh, background, null);
    }

    interface IItemsReadyListener {
        public void onItemsReady();
    }
}

