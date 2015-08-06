package com.futcore.restaurant;

import com.futcore.restaurant.util.*;
import com.futcore.restaurant.testsensor.Utils;

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
import android.widget.ImageView;
import android.widget.Toast;

//import android.support.wearable.view.GridViewPager;

import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import android.content.Intent;

import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;

public class AllRecosActivity extends Activity implements WearableListView.ClickListener {
    
    //    private static final List<Integer> times = Arrays.asList(15, 30, 45, 60, 90, 120);
    //    private ManRecoItemWear mRecoWear = null;
    
    //    private TextView mEventTitle;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;

	private static Dao<ManRecoWrapper, Long> recoWrapperDao = null;

    private static List<ManEventWear> mEventWears = null;

    private static final long RECENT_THRESHOLD_SEC = 2*3600; // 2 hours
    static final int BACK_EVENT_REQUEST = 0;

    public Map<Long, List<ManItemScore>> mRecos = null;
    //    public static List<ManItem> mNearRecos = new ArrayList<ManItem>();
    public static List<ManItemScore> mNearRecos = new ArrayList<ManItemScore>();
    //    public static Map<Long, ManItemScore> mAccRecos = null;
    public static  List<ManItemScore> mAccRecosList = null;
    
    private static final int NEAR_SPREAD = 30;

    private ViewPager mPager;

    private RecoListFragment mCounterPage;
    private RecoListFragment mCounterPage1;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void testPreference()
    {
        //        int mJumpCounter = PreferenceUtil.getConRecoFromPreference(this);
        //        mJumpCounter++;
        //        PreferenceUtil.saveConRecoToPreference(this, mJumpCounter);
        //        mEventTitle.setText(String.valueOf(mJumpCounter));
        //        int sizesize = PreferenceUtil.getAccRecoFromPreference(this).size();
        //        mEventTitle.setText(String.valueOf(sizesize));
    }

    public void resetNearRecos()
    {
        mNearRecos = new ArrayList<ManItemScore>();
    }

    public void getDao() {
        if(eventDao == null||itemDao == null||eventWearDao == null||recoItemWearDao == null||recoWrapperDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
                recoItemWearDao = helper.getRecoItemWearDao();
                recoWrapperDao = helper.getRecoWrapperDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRecos()
    {
        //        RecoGeneService.updateRecos();
        mRecos = RecoGeneService.mRecos;

        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrecos updated");

        if(mRecos==null){
            System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr self genearete");
            mRecos = new HashMap<Long, List<ManItemScore>>();

            try{
                QueryBuilder<ManRecoWrapper, Long> queryBuilder1 = recoWrapperDao.queryBuilder();
                queryBuilder1.orderBy(ManRecoWrapper.ID_FIELD_NAME, false).limit(1);
                PreparedQuery<ManRecoWrapper> preparedQuery1 = queryBuilder1.prepare();
            
                List<ManRecoWrapper> recoWs = recoWrapperDao.query(preparedQuery1);

                if(recoWs.size()>0){
                    mRecos = recoWs.get(0).getSeRecos();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        //        if(mRecos!=null&&mRecos.size()>0){
        if(mRecos.size()>0){
            System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrecos updated11111111");
            resetNearRecos();
            
            long timeOfDay = RecoGeneService.getIntervalTimeOfDay(new Date().getTime());

            System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr99999999999999999999999999999"+NEAR_SPREAD);

            System.out.println(mRecos.size()+"-------------------------------------------------");
            
            

            for(long i = timeOfDay-RecoGeneService.RECO_INTERVAL*NEAR_SPREAD;i<=timeOfDay+RecoGeneService.RECO_INTERVAL*NEAR_SPREAD;i+=RecoGeneService.RECO_INTERVAL){
                List<ManItemScore> recoItems = mRecos.get(i);
                if(recoItems!=null&&recoItems.size()>0){
                    for(ManItemScore mis:recoItems){
                        //                        if(!mNearRecos.contains(mis.getManItem()))
                        //                            mNearRecos.add(mis.getManItem());
                        if(!mNearRecos.contains(mis))
                            mNearRecos.add(mis);
                    }
                }
            }
        }
                
        /*        try{
            QueryBuilder<ManRecoWrapper, Long> queryBuilder1 = recoWrapperDao.queryBuilder();
            queryBuilder1.orderBy(ManRecoWrapper.ID_FIELD_NAME, false).limit(1);
            PreparedQuery<ManRecoWrapper> preparedQuery1 = queryBuilder1.prepare();
            
            //            List<ManRecoWrapper> recoWs = recoWrapperDao.query(preparedQuery1);

            //            if(recoWs.size()>0){
            //            mRecos = recoWs.get(0).getSeRecos();
                //                if(mRecos.size()>0){
                //                    long timeOfDay = RecoGeneService.getIntervalTimeOfDay(new Date().getTime());
                //                }
                //            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //        if(!com.futcore.restaurant.Utils.isMyServiceRunning(this, RecoLongService.class)){
        Intent intent = new Intent(this, RecoLongService.class);
        startService(intent);
        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr start  auto roco service");
            //        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_recos);

        mPager = (ViewPager) findViewById(R.id.pager);

        final RecoPagerAdapter adapter = new RecoPagerAdapter(getFragmentManager());

        mCounterPage = new RecoListFragment();
        mCounterPage1 = new RecoListFragment();

        Bundle args = new Bundle();
        args.putInt("type", RecoListFragment.FIXEND_TYPE);
        mCounterPage.setArguments(args);

        Bundle args1 = new Bundle();
        args1.putInt("type", RecoListFragment.INTERVAL_TYPE);
        mCounterPage1.setArguments(args1);
        
        adapter.addFragment(mCounterPage);
        adapter.addFragment(mCounterPage1);

        mPager.setAdapter(adapter);

        //        mPager = (ViewPager) findViewById(R.id.pager);
        //        final PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        //        mEventTitle = (TextView)findViewById(R.id.justTitle);

        //   mEventTitle.setText("Fixend Recos");

        //        testPreference();
        
        /*        WearableListView listView = (WearableListView) findViewById(R.id.eventsList);
                  listView.setAdapter(new Adapter(this));
                  listView.setClickListener(this);
        */

        getDao();
        getRecos();

        mAccRecosList = new ArrayList<ManItemScore>(PreferenceUtil.getAccRecoFromPreference(this).values());
        
        //        updateEventWearModel();
        //        Bundle extras = getIntent().getExtras();
        
        /*        if(getIntent().getSerializableExtra("recowear")!=null){
            mRecoWear = (ManRecoItemWear) getIntent().getSerializableExtra("recowear");
            mEventTitle.setText(mRecoWear.getManItem().getItemName());
        }
        */
        
        //            mEventId = extras.getString("eventid");
    }

    @Override
    public void onClick(WearableListView.ViewHolder v) {
        Intent intentT =  new Intent(this, ChooseRecoActivity.class);
        intentT.putExtra("recoscore", mNearRecos.get((Integer)v.itemView.getTag()));
            //            intentT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentT);

        
                //        Intent intentT =  new Intent(this, HistoryDetailActivity.class);
        //        intentT.putExtra("recoscore", recoItems.get(0));
        //        intentT.putExtra("event",mEventWears.get((Integer)v.itemView.getTag()) );
            //            intentT.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //        intentT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //            intentT.putExtra("eveid", intent.getExtras().getString("eveid"));
        //        intentT.putExtra();
        //        startActivityForResult(intentT, BACK_EVENT_REQUEST);
        //        startActivity(intentT);
        
        //        updateNotification((Integer) v.itemView.getTag());

        /*        int estMin = times.get((Integer)v.itemView.getTag());
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
        */
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
                                                   //                    mInflater.inflate(R.layout.history_eventpicker_list_item, null));
                                                   mInflater.inflate(R.layout.all_recos_list_item, null));
                                                   
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            ImageView circlev = (ImageView) holder.itemView.findViewById(R.id.circle);
            //            TextView durView = (TextView) holder.itemView.findViewById(R.id.dur);

            //            durView.setText();
            
            //            if(mEventWears.get(position).getUpdateTime()==0l)
            //                circlev.getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            
//            view.setText(mContext.getString(NotificationPresets.PRESETS[position].nameResId//));
//            view.setText(times.get(position).toString());
//            view.setText(mEventWears.get(position).getManItem().getItemName());
//            view.setText(mNearRecos.get(position).getItemName());
            view.setText(mNearRecos.get(position).getManItem().getItemName());

            if(mAccRecosList.contains(mNearRecos.get(position))){
                circlev.getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
            }
            
            //            holder.itemView.setTag(position);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mNearRecos.size();
            //            return mRecos.size();
            //            return mEventWears.size();
            //            return times.size();
            //            return NotificationPresets.PRESETS.length;
        }
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BACK_EVENT_REQUEST:
                if(resultCode == RESULT_OK)
                    finish();
                break;
        }
    }
    
    
}
