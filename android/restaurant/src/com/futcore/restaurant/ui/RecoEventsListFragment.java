/*brand-primary: #428bca;
brand-success: #5cb85c;
brand-info:    #5bc0de;
brand-warning: #f0ad4e;
brand-danger:  #d9534f;
*/

package com.futcore.restaurant.ui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONException;

import com.futcore.restaurant.NewEventItemActivity;

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
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;


import com.android.volley.toolbox.NetworkImageView;

import com.futcore.restaurant.Constants;
import com.futcore.restaurant.WordPress;
//import com.futcore.restaurant.models.Promo;
//import com.futcore.restaurant.models.Shop;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManCertPlace;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManRecoItem;



import com.futcore.restaurant.util.AlertUtil;
import com.futcore.restaurant.util.StringUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


/*import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
*/





import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import com.futcore.restaurant.R;


import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;

import com.commonsware.cwac.endless.EndlessAdapter;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import com.futcore.restaurant.models.DatabaseHelper;
import java.sql.SQLException;

import android.content.DialogInterface;

import android.os.CountDownTimer;

//import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshExpandableListFragment;

//public class RecoEventsListFragment extends ListFragment {
//public class RecoEventsListFragment extends PullToRefreshExpandableListFragment {
public class RecoEventsListFragment extends PullToRefreshListFragment {
    public static final int BRAND_PRIMARY = 0xff428bca;
    public static final int BRAND_SUCCESS = 0xff5cb85c;
    public static final int BRAND_INFO = 0xff5bc0de;
    public static final int BRAND_WARNING = 0xfff0ad4e;
    public static final int BRAND_DANGER = 0xffd9534f;

    public static final long RECOTHRESHOLDSEC = 3600000;

    private static final int TIMEZONE_OFFSET = 8;


    private int ACTIVITY_NEW_EVENT = 0;

    
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItem, Integer> recoItemDao = null;
    
    private static DatabaseHelper helper;
    
    private String errorMsg = "";
    //    public ArrayList<Shop> shopModel = new ArrayList<Shop>();
    //    public ArrayList<ManEvent> eventModel = new ArrayList<ManEvent>();
    //    public List<ManEvent> eventModel = new ArrayList<ManEvent>();
    public List<ManEvent> eventModel = null;
    public List<ManItem> itemModel = null;
    public List<ManEvent> itemEveModel = null;
    public List<ManRecoItem> recoItemModel = null;
    
    private PullToRefreshListView mPullToRefreshListView;

    //    DemoAdapter sAdapter = null;
    RecoDemoAdapter sAdapter = null;

    private String orderBy = "";
    private String order = "";

    private Boolean isTakeaway = false;

    private DatabaseHelper databaseHelper = null;

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
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }    

    public void getDao() {
        if(eventDao == null||itemDao == null||recoItemDao == null){
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
    

    ///    private Boolean appendLock = false;
    
    //    public View onCreatePullToRefreshListView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    public PullToRefreshListView onCreatePullToRefreshListView(LayoutInflater inflater,Bundle savedInstanceState)
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        //        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,countries);
        getDao();
        
        PullToRefreshListView listView = super.onCreatePullToRefreshListView(inflater, savedInstanceState);

        ProgressBar progressBar = new ProgressBar(getActivity());

        //        progressBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER));
        //        progressBar.setLayoutParams(new LayoutParams(20, 20, Gravity.CENTER));
        progressBar.setLayoutParams(new LayoutParams(300, 300, Gravity.CENTER));
        
        progressBar.setIndeterminate(true);
        //        progressBar.setIndeterminate(false);
        listView.setEmptyView(progressBar);
        
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                    // Update the LastUpdatedLabel
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    // Do work to refresh the list here.
                    //                    args.set("shouldRefresh", true)d;
                    //                    Map<String,Object> args = new HashMap<String,Object>();
                    //                    args.put("shouldRefresh", true);
                    //                    args.put("start", 0);
                    //                    Integer testtest = args.get("start");
                    //                    new GetShopTask(null, true).execute("0", orderBy, order);
                    new GetEventTask().execute();
                    System.out.println("########################1111");
                }
            });
        
        //        loadShops(false,false);
        return listView;
    }


    public void onListItemClick(ListView l, View v, int position, long id)
    {
        //        ManEvent cevent = itemEveModel.get(position-1);
        //        ManItem citem = itemModel.get(position-1);
        ManRecoItem recoitem = recoItemModel.get(position-1);
        
        //        AlertUtil.showAlert(getActivity(), R.string.error, citem.getItemName());

        Intent i = new Intent(getActivity(), NewEventItemActivity.class);
        //        i.putExtra("item", (citem));
        //        i.putExtra("event",cevent);
        System.out.println("1111111111111111111111111111111111111111111111111111111122");
        
        i.putExtra("recoitem",recoitem);
        startActivity(i);
        //        startActivityForResult(i, ACTIVITY_NEW_EVENT);
        
        /*        final ManEvent cevent = eventModel.get((int) id);
        
        AlertUtil.showAlert(getActivity(), R.string.error, R.string.connection_error_occured, "Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int arg1) {

                try{
                    UpdateBuilder<ManEvent, Integer> updateBuilder = eventDao.updateBuilder();
                
                    updateBuilder.updateColumnValue(ManEvent.UPDATETIME_FIELD_NAME, new Date());
                    updateBuilder.where().eq(ManEvent.ID_FIELD_NAME, cevent.getEventId());
                    updateBuilder.update();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                
                refreshEvents();
                d.dismiss();
                
            }
            }, "Cancel", null);
        */
    }
    
    private class GetEventTask extends AsyncTask<String, Void, Map<Integer, Map<?, ?>>> {
        //        private Integer mStart;
        private int mStart;
        private Boolean mShouldRefresh;

        IItemsReadyListener listener;

        protected GetEventTask(IItemsReadyListener listener){
            this.listener = listener;
            mShouldRefresh = false;
        }

        protected GetEventTask(){
            this.listener = null;
            mShouldRefresh = false;
        }
        
        //        protected Map<Integer, Map<?, ?>> doInBackground(Map<String,?>... params)
        protected Map<Integer, Map<?, ?>> doInBackground(String... params)
        {
            return null;
        }
        
        protected void onPostExecute(Map<Integer, Map<?, ?>> result) {
            //            AlertUtil.showAlert(getActivity(), R.string.error, R.string.connection_error_occured);
            
            //            List<ManEvent> cevents = eventDao.queryForAll();

            try{
                /*                QueryBuilder<ManEvent, Integer> queryBuilder = eventDao.queryBuilder();
                queryBuilder.orderBy(ManEvent.ID_FIELD_NAME, false).where().isNull(ManEvent.UPDATETIME_FIELD_NAME);
                PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                eventModel = eventDao.query(preparedQuery);
                */
                //                eventModel = eventDao.queryForAll();

                QueryBuilder<ManEvent, String> queryBuilder = eventDao.queryBuilder();
                queryBuilder.orderBy(ManEvent.ID_FIELD_NAME, false);
                PreparedQuery<ManEvent> preparedQuery = queryBuilder.prepare();
                eventModel = eventDao.query(preparedQuery);

                //                recoItemModel = recoItemDao.queryForAll();
                //                long csecindi = new Date().getTime()
                recoItemModel = new ArrayList<ManRecoItem>();
                long dateindi = (new Date().getTime()+TIMEZONE_OFFSET*1000*3600)/(24*1000*3600);
                long timeindi = (new Date().getTime()+TIMEZONE_OFFSET*1000*3600)%(24*1000*3600);
                    
                List<ManRecoItem> recos = recoItemDao.queryForAll();
                for(ManRecoItem rec:recos){
                    System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
                    System.out.println(rec.getManItem().getItemId());
                    
                    
                    try{
                        JSONObject conff = new JSONObject(rec.getModelConfig());

                        System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiigggggg");
                        System.out.println(timeindi);
                        System.out.println(conff.getLong("timeindi"));
                        System.out.println(conff.getLong("timefuzzy"));
                        
                        if(timeindi>conff.getLong("timeindi")-conff.getLong("timefuzzy")*1000 && timeindi<conff.getLong("timeindi")+conff.getLong("timefuzzy")*1000)
                            recoItemModel.add(rec);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    
                    
                }
                
                
                /*                long cstamp = new Date().getTime()%(24*1000*3600);
                long dateindi = new Date().getTime()/(24*1000*3600);
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                System.out.println(dateindi);
                                
                itemModel = new ArrayList<ManItem>();
                itemEveModel = new ArrayList<ManEvent>();
                
                for(ManEvent cevent:eventModel){
                    // remove the hint events of today
                    if(dateindi==cevent.getCreateTime().getTime()/(24*1000*3600))
                        continue; 
                    
                    System.out.println(cevent.getCreateTime().getTime()%(24*1000*3600));
                    long tstamp = cevent.getCreateTime().getTime()%(24*1000*3600);
                    //                    System.out.println(cevent.getManItem().getItemName());
                    ManItem citem = cevent.getManItem();
                    if(Math.abs(cstamp-tstamp)<RECOTHRESHOLDSEC && !itemModel.contains(citem)){
                        itemModel.add(citem);
                        itemEveModel.add(cevent);
                    }
                }
                */

                //                QueryBuilder<ManItem, Integer> queryBuilder = itemDao.queryBuilder();
                //                queryBuilder.orderBy(ManItem.ID_FIELD_NAME, false).limit(1);
                //                PreparedQuery<ManItem> preparedQuery = queryBuilder.prepare();
                //                itemModel = itemDao.query(preparedQuery);


                //                sAdapter = new DemoAdapter(getActivity(), (ArrayList) itemModel);
                sAdapter = new RecoDemoAdapter(getActivity(), (ArrayList) recoItemModel);
                //                sAdapter = new DemoAdapter(getActivity(), (ArrayList) eventModel);

                //                sAdapter = new DemoAdapter(getActivity(), null);
                
                sAdapter.setRunInBackground(false);
                setListAdapter(sAdapter);
                
            }
            catch(SQLException e){
                e.printStackTrace();
            }
            
            
            /*            if(result==null){
                AlertUtil.showAlert(getActivity(), R.string.error, R.string.connection_error_occured);
            }
            else{
                if(mShouldRefresh)
                    shopModel = new ArrayList<Shop>();
            
                if(result.size()>0)
                {
                    for (int i = 0; i < result.size(); i++) {
                        Map<?,?> ele = result.get(i);
                        HashMap<Integer, String> shopTags = new HashMap<Integer,String>();
                        List<String> shopTags1 = new ArrayList<String>();
                        try {
                        
                            shopTags1 = new ObjectMapper().readValue(ele.get("shoptags").toString(), ArrayList.class);
                            for (String s : shopTags1){
                                String[] info = s.split(":");
                                shopTags.put(Integer.valueOf(info[0]), info[1]);
                            }
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }                    
                    
                        shopModel.add(new Shop(
                                               Integer.parseInt(ele.get("shopid").toString()),
                                               ele.get("shopname").toString(),
                                               shopTags,
                                               Integer.parseInt(ele.get("shopaverage").toString()),
                                               0,
                                               0,
                                               0,0,0,
                                               ele.get("shopaddr").toString(),
                                               ele.get("shoplandphone").toString(),
                                               ele.get("thumbmiddle").toString(),
                                               Double.parseDouble(ele.get("shoplat").toString()),
                                               Double.parseDouble(ele.get("shoplng").toString())
                                               )
                                      );
                    }


                    if(mShouldRefresh)
                    {
                        sAdapter = new DemoAdapter(getActivity(),shopModel);
                        sAdapter.setRunInBackground(false);
                        setListAdapter(sAdapter);
                    }

                    if(!mShouldRefresh){
                        listener.onItemsReady();
                    }
                }
            }
            */
            getPullToRefreshListView().onRefreshComplete();
            super.onPostExecute(result);
        }
    }

    class RecoDemoAdapter extends EndlessAdapter implements IItemsReadyListener{
        private RotateAnimation rotate=null;
        private View pendingView=null;
  
        RecoDemoAdapter(Context ctxt, ArrayList<ManRecoItem> list ) {
            super(new RecoItemAdapter(list));
        
            rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                                       0.5f, Animation.RELATIVE_TO_SELF,
                                       0.5f);
            rotate.setDuration(600);
            rotate.setRepeatMode(Animation.RESTART);
            rotate.setRepeatCount(Animation.INFINITE);
        }
  
        @Override
        protected View getPendingView(ViewGroup parent) {
            View row=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);

            ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.throbber);
            progressBar.setIndeterminate(true);
    
            return(row);
        }

        @Override
        protected boolean cacheInBackground() {
            return(getWrappedAdapter().getCount()<50);
        }

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

    class DemoAdapter extends EndlessAdapter implements IItemsReadyListener{
        private RotateAnimation rotate=null;
        private View pendingView=null;
  
        //        DemoAdapter(Context ctxt, ArrayList<ManEvent> list ) {
        //        DemoAdapter(Context ctxt, ArrayList<ManEvent> list ) {
        DemoAdapter(Context ctxt, ArrayList<ManItem> list ) {
            //            super(new EventAdapter(list));
            super(new ItemAdapter(list));
        
            rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                                       0.5f, Animation.RELATIVE_TO_SELF,
                                       0.5f);
            rotate.setDuration(600);
            rotate.setRepeatMode(Animation.RESTART);
            rotate.setRepeatCount(Animation.INFINITE);
        }
  
        @Override
        protected View getPendingView(ViewGroup parent) {
            View row=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);

            ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.throbber);
            progressBar.setIndeterminate(true);
    
            return(row);
        }

        @Override
        protected boolean cacheInBackground() {
            //            new GetEventTask(this).execute();
            //            System.out.println("########################222");
            return(getWrappedAdapter().getCount()<50);
        }

        //        public void onItemsReady(ArrayList<Shop> data) {
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

    class EventAdapter extends ArrayAdapter<ManEvent>
    {
        private final ArrayList<ManEvent> model;
        
        EventAdapter(ArrayList<ManEvent> model){
            super(getActivity().getApplicationContext(), R.layout.reco_event_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.reco_event_row, parent, false);
            /*            NetworkImageView thumb = (NetworkImageView) rowView.findViewById(R.id.shopIcon);
            TextView shopTitle = (TextView)rowView.findViewById(R.id.shopTitle);
            TextView shopTags = (TextView)rowView.findViewById(R.id.shopTags);
            thumb.setImageUrl(Constants.baseURL+model.get(position).thumbUrl.toString(), WordPress.imageLoader);
            */
            LinearLayout statusIndi = (LinearLayout)rowView.findViewById(R.id.statusIndi);
            TextView eventTitle = (TextView)rowView.findViewById(R.id.eventTitle);
            TextView eventCrit = (TextView)rowView.findViewById(R.id.eventCrit);
            final TextView eventCountDown = (TextView)rowView.findViewById(R.id.eventCountDown);


            long ccdate = new Date().getTime()-model.get(position).getCreateTime().getTime();
            ccdate = model.get(position).getEstDuration()*60*1000-ccdate;

            eventTitle.setText(model.get(position).getManItem().getItemName());

            /*            if(ccdate<15*60*1000){
                statusIndi.setBackgroundColor(BRAND_DANGER);            
            }
            else if(ccdate<30*60*1000){
                statusIndi.setBackgroundColor(BRAND_WARNING);            
            }
            else if(ccdate<60*60*1000){
                statusIndi.setBackgroundColor(BRAND_SUCCESS);            
            }
            else{
                statusIndi.setBackgroundColor(BRAND_PRIMARY);            
            }
            */
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ccdate);

            if(ccdate>60*60*1000){
                statusIndi.setBackgroundColor(BRAND_PRIMARY);            
            }
            else if(ccdate>30*60*1000){
                statusIndi.setBackgroundColor(BRAND_SUCCESS);            
            }
            else if(ccdate>15*60*1000){
                statusIndi.setBackgroundColor(BRAND_WARNING);            
            }
            else{
                statusIndi.setBackgroundColor(BRAND_DANGER);            
                //                statusIndi.setBackgroundColor(BRAND_PRIMARY);            
            }
            
            

            new CountDownTimer(ccdate, 1000) {
                public void onTick(long millisUntilFinished) {
                    //                    eventCountDown.setText("" + millisUntilFinished / 1000);
                    millisUntilFinished /= 1000;
                    eventCountDown.setText(String.format("%02d:%02d:%02d", millisUntilFinished / 3600,(millisUntilFinished % 3600) / 60, (millisUntilFinished % 60)));

                    //                    System.out.println(millisUntilFinished+"  "+millisUntilFinished%(60*15)+"uuuu");

                    if((millisUntilFinished+1)%(60*15)==0){
                        //                        AlertUtil.showAlert(getActivity(), R.string.error, R.string.connection_error_occured);
                        refreshEvents();
                        System.out.println("###################################################################################################################"+millisUntilFinished);
                    }
                }

                public void onFinish() {
                    eventCountDown.setText("done!");
                }
            }.start();
             


            
            eventCrit.setText(model.get(position).getManItem().getItemCrit());
            //            shopTitle.setText(model.get(position).shopName.toString());
            //            shopTags.setText(model.get(position).getShopTagsString());
            return rowView;
        }

        public void testAdd()
        {
            for (int i=0;i<8;i++) { this.model.add(this.model.get(0)); }
        }
    }


    class RecoItemAdapter extends ArrayAdapter<ManRecoItem>
    {
        private final ArrayList<ManRecoItem> model;
        
        RecoItemAdapter(ArrayList<ManRecoItem> model){
            super(getActivity().getApplicationContext(), R.layout.reco_event_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.reco_event_row, parent, false);
            
            TextView eventTitle = (TextView)rowView.findViewById(R.id.eventTitle);
            
            eventTitle.setText(model.get(position).getManItem().getItemName());
            
            return rowView;
        }

        public void testAdd()
        {
            for (int i=0;i<8;i++) { this.model.add(this.model.get(0)); }
        }
    }
    


    class ItemAdapter extends ArrayAdapter<ManItem>
    {
        private final ArrayList<ManItem> model;
        
        ItemAdapter(ArrayList<ManItem> model){
            super(getActivity().getApplicationContext(), R.layout.reco_event_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.reco_event_row, parent, false);
            /*            NetworkImageView thumb = (NetworkImageView) rowView.findViewById(R.id.shopIcon);
            TextView shopTitle = (TextView)rowView.findViewById(R.id.shopTitle);
            TextView shopTags = (TextView)rowView.findViewById(R.id.shopTags);
            thumb.setImageUrl(Constants.baseURL+model.get(position).thumbUrl.toString(), WordPress.imageLoader);
            */
            //            LinearLayout statusIndi = (LinearLayout)rowView.findViewById(R.id.statusIndi);
            TextView eventTitle = (TextView)rowView.findViewById(R.id.eventTitle);
            //            TextView eventCrit = (TextView)rowView.findViewById(R.id.eventCrit);
            //            final TextView eventCountDown = (TextView)rowView.findViewById(R.id.eventCountDown);


            //            long ccdate = new Date().getTime()-model.get(position).getCreateTime().getTime();
            //            ccdate = model.get(position).getEstDuration()*60*1000-ccdate;
            
            //            eventTitle.setText(model.get(position).getManItem().getItemName());
            eventTitle.setText(model.get(position).getItemName());

            /*            if(ccdate<15*60*1000){
                statusIndi.setBackgroundColor(BRAND_DANGER);            
            }
            else if(ccdate<30*60*1000){
                statusIndi.setBackgroundColor(BRAND_WARNING);            
            }
            else if(ccdate<60*60*1000){
                statusIndi.setBackgroundColor(BRAND_SUCCESS);            
            }
            else{
                statusIndi.setBackgroundColor(BRAND_PRIMARY);            
            }
            */
            
            //            eventCrit.setText(model.get(position).getManItem().getItemCrit());
            //            shopTitle.setText(model.get(position).shopName.toString());
            //            shopTags.setText(model.get(position).getShopTagsString());
            return rowView;
        }

        public void testAdd()
        {
            for (int i=0;i<8;i++) { this.model.add(this.model.get(0)); }
        }
    }
    

    /*    class ShopAdapter extends ArrayAdapter<Shop>
    {
        private final ArrayList<Shop> model;
        
        ShopAdapter(ArrayList<Shop> model){
            super(getActivity().getApplicationContext(), R.layout.shop_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.shop_row, parent, false);
            NetworkImageView thumb = (NetworkImageView) rowView.findViewById(R.id.shopIcon);
            TextView shopTitle = (TextView)rowView.findViewById(R.id.shopTitle);
            TextView shopTags = (TextView)rowView.findViewById(R.id.shopTags);
            thumb.setImageUrl(Constants.baseURL+model.get(position).thumbUrl.toString(), WordPress.imageLoader);
            shopTitle.setText(model.get(position).shopName.toString());
            shopTags.setText(model.get(position).getShopTagsString());
            return rowView;
        }

        public void testAdd()
        {
            for (int i=0;i<8;i++) { this.model.add(this.model.get(0)); }
        }
    }
    */

    public boolean pullInit()
    {
        getPullToRefreshListView().setOnRefreshListener(new OnRefreshListener<ListView>() {
                @Override
                    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    //                    new GetShopTask(null, true).execute("0", orderBy, order);
                    new GetEventTask().execute();
            
                    System.out.println("########################33333");
                    
                }
            });
        return true;
    }

    //    public void refreshShops(){
    public void refreshEvents(){
        new GetEventTask().execute();
        //        System.out.println("########################4444444444444");
    }
    
    /*    public void refreshShops(final boolean more, final boolean refresh, final boolean background, List<String> params)
    {
        if(params!=null){
            orderBy = params.get(0);
            order = params.get(1);
        }
        
        new GetShopTask(null, true).execute("0", orderBy, order);
    }

    public void refreshShops(final boolean more, final boolean refresh, final boolean background)
    {
        refreshShops(more, refresh, background, null);
    }
    */

    interface IItemsReadyListener {
        public void onItemsReady();
    }

}
