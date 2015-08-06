package com.futcore.restaurant;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.support.wearable.view.WearableListView;
import android.content.Context;

import com.futcore.restaurant.util.*;

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


public class RecoListFragment extends Fragment  implements WearableListView.ClickListener
{
    private TextView mEventTitle;

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;
	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;
	private static Dao<ManRecoItemWear, Integer> recoItemWearDao = null;
    
	private static Dao<ManRecoWrapper, Long> recoWrapperDao = null;

    private static final long RECENT_THRESHOLD_SEC = 2*3600; // 2 hours
    static final int BACK_EVENT_REQUEST = 0;

    public Map<Long, List<ManItemScore>> mRecos = null;
    //    public static List<ManItem> mNearRecos = new ArrayList<ManItem>();
    public static List<ManItemScore> mNearRecos = new ArrayList<ManItemScore>();
    //    public static Map<Long, ManItemScore> mAccRecos = null;
    public static  List<ManItemScore> mAccRecosList = null;
    
    private static final int NEAR_SPREAD = 3;

    public static final int FIXEND_TYPE = 0;
    public static final int INTERVAL_TYPE = 1;

    //    private static int mType;
    private int mType;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
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
                
    }
    
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reco_list, container, false);

        mType = getArguments().getInt("type");    

        mEventTitle = (TextView) view.findViewById(R.id.justTitle);

        if(mType==FIXEND_TYPE)
            mEventTitle.setText("Fixend Recos");
        else if(mType==INTERVAL_TYPE)
            mEventTitle.setText("Interval Recos");

        WearableListView listView = (WearableListView) view.findViewById(R.id.eventsList);
        //        listView.setAdapter(new Adapter(this));
        listView.setAdapter(new Adapter(getActivity()));
        listView.setClickListener(this);

        getDao();
        getRecos();

        mAccRecosList = new ArrayList<ManItemScore>(PreferenceUtil.getAccRecoFromPreference(getActivity()).values());

        return view;
    }


    //    private static final class Adapter extends WearableListView.Adapter {
    private final class Adapter extends WearableListView.Adapter {
        private final Context mContext;
        private final LayoutInflater mInflater;

        private Adapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                                                   mInflater.inflate(R.layout.all_recos_list_item, null));
                                                   
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            ImageView circlev = (ImageView) holder.itemView.findViewById(R.id.circle);
            view.setText(mNearRecos.get(position).getManItem().getItemName());

            //            if(mType==FIXEND_TYPE){
            if(mType==INTERVAL_TYPE){
                circlev.getDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.OVERLAY);
            }
                //               circlev.getDrawable().setColorFilter(Color.parseColor("#ffbb33"), PorterDuff.Mode.OVERLAY);

            if(mAccRecosList.contains(mNearRecos.get(position))){
                circlev.getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
            }
            
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mNearRecos.size();
        }
    }
    
    @Override
    public void onTopEmptyRegionClick() {
    }


    /*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BACK_EVENT_REQUEST:
                if(resultCode == RESULT_OK)
                    getActivity().finish();
                break;
        }
    }
    */

    @Override
    public void onClick(WearableListView.ViewHolder v) {
        //        Intent intentT =  new Intent(this, ChooseRecoActivity.class);
        Intent intentT =  new Intent(getActivity(), ChooseRecoActivity.class);
        intentT.putExtra("recoscore", mNearRecos.get((Integer)v.itemView.getTag()));
        intentT.putExtra("type", mType);
        startActivity(intentT);
    }
}


