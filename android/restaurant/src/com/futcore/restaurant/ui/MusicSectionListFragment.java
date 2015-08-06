package com.futcore.restaurant.ui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Date;

import com.futcore.restaurant.util.*;

import com.futcore.restaurant.service.EventStatusService;
import com.futcore.restaurant.SpiderService;

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

import com.futcore.restaurant.models.*;


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

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;

import android.graphics.BitmapFactory;

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


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.AudioManager;
import com.futcore.restaurant.RemoteControlReceiver;

import android.content.ComponentName;

public class MusicSectionListFragment extends PullToRefreshListFragment {

    private AudioManager am;
    public static final String MUSIC_DIR = "/storage/sdcard0/Music";
    private static final String TEST_RECORD_FILE = "/storage/sdcard0/Music/spider_test.3gp";

    private static String mFileName = TEST_RECORD_FILE;

    private boolean isRecording = false;
    
    
    public List<MusicSection> itemModel = new ArrayList<MusicSection>();
    private PullToRefreshListView mPullToRefreshListView;
    DemoAdapter sAdapter = null;

    IItemsReadyListener iilistener;

    private MediaPlayer mMediaPlayer;
    private MediaPlayer mMediaPlayer1;
    private MediaPlayer mRecordPlayer;
    private MediaRecorder mRecorder = null;

    ComponentName mMediaButtonReceiverComponent;
    
    private android.os.Handler mStartOriHan;
    private android.os.Handler mStartRecordHan;
    private android.os.Handler mStartReplayHan;

    private long mTimeper1;
    private long mTimeper2;

    
    private  void startOriMusic()
    {
        try{
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo((int)mTimeper1);
            mMediaPlayer.start();
            
            mStartOriHan = new android.os.Handler();
            mStartOriHan.
                postDelayed(
                            new Runnable() {
                                public void run() {
                                    mMediaPlayer.stop();
                                    startRecord();
                                    //                                                Log.i("tag", "This'll run 300 milliseconds later");
                                }
                            }, 
                            mTimeper2-mTimeper1);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void startRecord()
    {
        try{
            mMediaPlayer1.prepare();
            mMediaPlayer1.seekTo((int)mTimeper1);
            mMediaPlayer1.start();

            //            new MediaPrepareTask().execute(null, null, null);
            startRecording();

            mStartRecordHan =  new android.os.Handler();
            mStartRecordHan.
                postDelayed(
                            new Runnable() {
                                 public void run() {
                                    mMediaPlayer1.stop();
                                    //                                    stopRecord();
                                    stopRecording();
                                    //                                startOriMusic();
                                    startReplay();
                                    //                                                Log.i("tag", "This'll run 300 milliseconds later");
                                }
                            }, 
                            mTimeper2-mTimeper1);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void startReplay()
    {
        try{
            mMediaPlayer1.prepare();
            mMediaPlayer1.seekTo((int)mTimeper1);
            mMediaPlayer1.start();

            startPlaying();

            mStartReplayHan =  new android.os.Handler();
            mStartReplayHan.
                postDelayed(
                            new Runnable() {
                                public void run() {
                                    stopPlaying();
                                    mMediaPlayer1.stop();
                                    //                                startOriMusic();
                                    startOriMusic();
                                    //                                                Log.i("tag", "This'll run 300 milliseconds later");
                                }
                            }, 
                            mTimeper2-mTimeper1);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public PullToRefreshListView onCreatePullToRefreshListView(LayoutInflater inflater,Bundle savedInstanceState)
    {
        try{
        
            mMediaPlayer = new MediaPlayer();
            String path = "/storage/sdcard0/Music/melody.mp3";
            mMediaPlayer.setDataSource(path);
        
            mMediaPlayer.prepare();

            mMediaPlayer1 = new MediaPlayer();
            String path1 = "/storage/sdcard0/Music/melody_r.mp3";
            mMediaPlayer1.setDataSource(path1);

            mMediaPlayer1.prepare();
        
        }
        catch(IOException e){
            e.printStackTrace();
        }
        

        
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
                    //terry

                    
                    System.out.println("sssssssssssssssss!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!3333333333333333333333333333333333333333333333333333");
                    
                    updateSectionList();
                    //                    new GetItemTask(null, true).execute("0", "","");
                }
            });




        /*        RemoteControlReceiver.setActivity(this);
        
        mMediaButtonReceiverComponent = new ComponentName(getActivity().getPackageName(), RemoteControlReceiver.class.getName());
        am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        
        //        System.out.println("fragentsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssshow1111111111111111111111111");
        am.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);
        */


        RemoteControlReceiver.setActivity(this);
        
        mMediaButtonReceiverComponent = new ComponentName(getActivity().getPackageName(), RemoteControlReceiver.class.getName());
        am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        //        System.out.println("fragentsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssshow1111111111111111111111111");
        am.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);
        
        
        return listView;
        
        
    }


    

    @Override
    public void onStop()
    {
        super.onStop();
        //        am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
        am.unregisterMediaButtonEventReceiver(mMediaButtonReceiverComponent);
    }
    

    private void updateSectionList()
    {
        Boolean mShouldRefresh = true;

        itemModel = PreferenceUtil.getMusicSections(getActivity());

        System.out.println("99999999999999999999999999999999999999999999999999999999999999999999999999222222222222222222222222222222222222222" + itemModel.size());
        
        sAdapter = new DemoAdapter(getActivity(),itemModel);
        sAdapter.setRunInBackground(false);
        //            promoList.setAdapter(adapter);
        setListAdapter(sAdapter);
        
        getPullToRefreshListView().onRefreshComplete();
    }

    public void onListItemClick(ListView l, View v, int position, long id)
    {
        //        ManItem item = itemModel.get((int)id);
        //        AlertUtil.showAlert(getActivity(), R.string.required_fields, item.getItemName());
    }


    class DemoAdapter extends EndlessAdapter implements IItemsReadyListener{
        private RotateAnimation rotate=null;
        private View pendingView=null;
  
        DemoAdapter(Context ctxt, List<MusicSection> list ) {
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
            //            System.out.println("wawawawawa");
            View row=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);

            ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.throbber);
            progressBar.setIndeterminate(true);
    
            return(row);
        }
        

        @Override
        protected boolean cacheInBackground() {
            //terry
            //            updateSectionList();
            //            new GetItemTask(this).execute(String.valueOf(itemModel.size()), orderBy, order);
            
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
    
    class ItemAdapter extends ArrayAdapter<MusicSection>
    {
        private final List<MusicSection> model;
        
        ItemAdapter(List<MusicSection> model){
            super(getActivity().getApplicationContext(), R.layout.event_item_row, model);
            this.model = model;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowView = inflater.inflate(R.layout.music_section_row, parent, false);
            //            NetworkImageView thumb = (NetworkImageView) rowView.findViewById(R.id.itemIcon);
            TextView sectId = (TextView)rowView.findViewById(R.id.sectid);
            final MusicSection sect = (MusicSection) model.get(position);
            sectId.setText(sect.getSectid());

            Button delBut = (Button)rowView.findViewById(R.id.del);

            delBut.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceUtil.removeMusicSection(getActivity(),sect);
                        updateSectionList();
                    }
                });


            sectId.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTimeper1 = sect.getStartsec();
                        mTimeper2 = sect.getEndsec();
                        
                        //                        startOriMusic();
                        jumpToStartOri();
                        //                        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                    }
                });
            
            //            ManItem item = (ManItem)model.get(position);
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
                    //                    new GetItemTask(null, true).execute("0", orderBy, order);
                    updateSectionList();
                }
            });

        System.out.println("ccc99999999999999999999999999999999999999999999999999999999999999999999999999222222222222222222222222222222222222222");
        return true;
    }

    public boolean loadItems(boolean refresh, boolean loadMore)
    {
        //        new GetItemTask(null, false).execute("0", orderBy, order);
        return true;
    }

    public void refreshItems(final boolean more, final boolean refresh, final boolean background, List<String> params)
    {
        //        if(params!=null){
        //            orderBy = params.get(0);
        //            order = params.get(1);
        //        }
        //        new GetItemTask(null, true).execute("0", orderBy, order);
    }

    interface IItemsReadyListener {
        public void onItemsReady();
    }

    public void refreshEvents()
    {
        updateSectionList();
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            //            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }    

    private void stopRecording() {
        if(mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    

    private void startPlaying() {
        mRecordPlayer = new MediaPlayer();

        /*        AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                  int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                  amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
                  mRecordPlayer.setAudioStreamType(AudioManager.STREAM_ALARM); // this is important.
        */
        
        ///        mFileName = "/storage/sdcard0/Music/melody.mp3";
        try {
            mRecordPlayer.setDataSource(mFileName);
            //            mRecordPlayer.setVolume(100,100);
            
            mRecordPlayer.prepare();
            mRecordPlayer.start();
            System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssplaying "+mFileName);
            
        } catch (IOException e) {
            e.printStackTrace();
            
            //            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void jumpToStartRecord()
    {
        if(mStartOriHan!=null)
            mStartOriHan.removeCallbacksAndMessages(null);
        if(mStartRecordHan!=null)
            mStartRecordHan.removeCallbacksAndMessages(null);
        if(mStartReplayHan!=null)
            mStartReplayHan.removeCallbacksAndMessages(null);
        
        mMediaPlayer.stop();
        mMediaPlayer1.stop();
        stopPlaying();
        stopRecording();
        
        startRecord();
    }

    public void jumpToStartOri()
    {
        if(mStartOriHan!=null)
            mStartOriHan.removeCallbacksAndMessages(null);
        if(mStartRecordHan!=null)
            mStartRecordHan.removeCallbacksAndMessages(null);
        if(mStartReplayHan!=null)
            mStartReplayHan.removeCallbacksAndMessages(null);
        
        mMediaPlayer.stop();
        mMediaPlayer1.stop();
        stopPlaying();
        stopRecording();
        
        //        mMediaPlayer = new MediaPlayer();
        startOriMusic();
    }

    private void stopPlaying() {
        if(mRecordPlayer!=null){
            mRecordPlayer.release();
            mRecordPlayer = null;
        }
    }
    
}
