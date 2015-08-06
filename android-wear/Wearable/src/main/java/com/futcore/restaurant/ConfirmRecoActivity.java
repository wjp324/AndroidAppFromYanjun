package com.futcore.restaurant;


import com.futcore.restaurant.util.ItemIdUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WearableListView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Intent;

import com.futcore.restaurant.models.ManRecoItemWear;

import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.futcore.restaurant.models.*;


public class ConfirmRecoActivity extends Activity implements WearableListView.ClickListener {
    
    private static final List<Integer> times = Arrays.asList(15, 30, 45, 60, 90, 120);
    private ManRecoItemWear mRecoWear = null;
    
    private TextView mEventTitle;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao == null||itemDao == null||eventWearDao == null||recoItemWearDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
                recoItemWearDao = helper.getRecoItemWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_reco);

        mEventTitle = (TextView)findViewById(R.id.eventTitle);

        WearableListView listView = (WearableListView) findViewById(R.id.timeList);
        listView.setAdapter(new Adapter(this));
        listView.setClickListener(this);

        getDao();

        //        Bundle extras = getIntent().getExtras();
        
        if(getIntent().getSerializableExtra("recowear")!=null){
            mRecoWear = (ManRecoItemWear) getIntent().getSerializableExtra("recowear");
            mEventTitle.setText(mRecoWear.getManItem().getItemName());

            
        }
        
        //            mEventId = extras.getString("eventid");
        
    }

    @Override
    public void onClick(WearableListView.ViewHolder v) {
        //        updateNotification((Integer) v.itemView.getTag());

        int estMin = times.get((Integer)v.itemView.getTag());
        System.out.println(estMin);

        try{
            ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
            //            recoItemWearDao.create();
            long curTime = new Date().getTime();
            itemDao.createIfNotExists(mRecoWear.getManItem());
            eventWearDao.create(new ManEventWear(ItemIdUtil.getGenId("event"), tuser, mRecoWear.getManItem(),0,0,0,curTime, curTime+estMin*60000,estMin, (byte)0));
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this, SendEventNotiService.class);
        //        stopService(intent);
        startService(intent);

        finish();
        //        finish();
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    private static final class Adapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private Adapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.reco_timepicker_list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
//            view.setText(mContext.getString(NotificationPresets.PRESETS[position].nameResId//));
            view.setText(times.get(position).toString());
            //            holder.itemView.setTag(position);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return times.size();
            //            return NotificationPresets.PRESETS.length;
        }
    }
    
}
