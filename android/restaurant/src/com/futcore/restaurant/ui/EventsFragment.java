package com.futcore.restaurant.ui;

import com.futcore.restaurant.util.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.content.ComponentName;

import android.os.AsyncTask;


import android.graphics.Color;

import android.media.MediaRecorder;
import android.media.AudioManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
///import android.support.v4.app.Fragment;

import com.futcore.restaurant.models.*;
import com.futcore.restaurant.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.io.File;

import com.futcore.restaurant.service.MusicService.MusicBinder;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;

import android.media.MediaPlayer;

import java.io.IOException;

import java.text.SimpleDateFormat;

import com.futcore.restaurant.RemoteControlReceiver;
import android.media.AudioManager;

//public class IndexFragment extends Fragment implements OnClickListener
public class EventsFragment extends SherlockFragment implements OnClickListener
{
    private AudioManager am;
    public static final String MUSIC_DIR = "/storage/sdcard0/Music";
    private static final String TEST_RECORD_FILE = "/storage/sdcard0/Music/spider_test.3gp";

    private static String mFileName = TEST_RECORD_FILE;

    private boolean isRecording = false;
    
    private View mView;
    
    public String errorMsg = "";

    private Button addNew;

    /*    private Button mTestplay;
          private Button mTestplay1;
          private Button mTeststop;
    */

    private Button mStartcap;
    private Button mTime1;
    private Button mTime2;
    private Button mSave;
    private Button mBegin;

    private MediaPlayer mMediaPlayer;
    private MediaPlayer mMediaPlayer1;

    private MediaPlayer mRecordPlayer;

    private MediaRecorder mRecorder = null;
    //    private MediaRecorder mMediaRecorder;

    private Date mStartTime;
    private Date mTimest1;
    private Date mTimest2;

    private TextView mTimesttext1;
    private TextView mTimesttext2;

    private TextView mTimestper1;
    private TextView mTimestper2;

    private long mTimeper1;
    private long mTimeper2;

    ComponentName mMediaButtonReceiverComponent;

    private android.os.Handler mStartOriHan;
    private android.os.Handler mStartRecordHan;
    private android.os.Handler mStartReplayHan;
    
    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        //        fragment.mContent = content;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        //        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
    }
    
    
    //    public void onCreate(Bundle savedInstanceState)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state){
        
        setHasOptionsMenu(true);
        
        mView = inflater.inflate(R.layout.events, parent, false);
        /*        mTestplay = (Button)mView.findViewById(R.id.testplay);
                  mTestplay1 = (Button)mView.findViewById(R.id.testplay1);
                  mTeststop = (Button)mView.findViewById(R.id.teststop);
        */

        //mMediaRecorder = new MediaRecorder();
        //        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        //mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mStartcap = (Button)mView.findViewById(R.id.startcap);
        mTime1 = (Button)mView.findViewById(R.id.time1);
        mTime2 = (Button)mView.findViewById(R.id.time2);
        mSave = (Button)mView.findViewById(R.id.save);
        mBegin = (Button)mView.findViewById(R.id.begin);

        mTimesttext1 = (TextView)mView.findViewById(R.id.timesttext1);
        mTimesttext2 = (TextView)mView.findViewById(R.id.timesttext2);

        mTimestper1 = (TextView)mView.findViewById(R.id.timestper1);
        mTimestper2 = (TextView)mView.findViewById(R.id.timestper2);


        initPerTime();

        mStartcap.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStartTime = new Date();
                    
                    try{
                        mMediaPlayer = new MediaPlayer();
                        String path = "/storage/sdcard0/Music/melody.mp3";
                        mMediaPlayer.setDataSource(path);
                        mMediaPlayer.prepare();
                        //                        mMediaPlayer.seekTo(60000);
                        mMediaPlayer.start();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });

        mTime1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTimest1 = new Date();
                    mTimesttext1.setText(String.valueOf(mTimest1.getTime()-mStartTime.getTime()));
                }
            });

        mTime2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTimest2 = new Date();
                    mTimesttext2.setText(String.valueOf(mTimest2.getTime()-mStartTime.getTime()));
                }
            });

        mSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTimesttext1.setTextColor(Color.BLUE);
                    mTimesttext2.setTextColor(Color.BLUE);

                    saveCutToPreference();
                    
                    saveReset();
                }
            });

        mBegin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        mMediaPlayer = new MediaPlayer();
                        String path = "/storage/sdcard0/Music/melody.mp3";
                        mMediaPlayer.setDataSource(path);
                        //                        mMediaPlayer.prepare();

                        mMediaPlayer1 = new MediaPlayer();
                        String path1 = "/storage/sdcard0/Music/melody_r.mp3";
                        mMediaPlayer1.setDataSource(path1);
                        //                        mMediaPlayer1.prepare();
                        
                        startOriMusic();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });

        /*        mTestplay.setOnClickListener(new OnClickListener() {
                  @Override
                  public void onClick(View v) {
                  System.out.println("kkkkkk0000000000000");
                  try{
                  mMediaPlayer = new MediaPlayer();
                  String path = "/storage/sdcard0/Music/melody.mp3";
                  mMediaPlayer.setDataSource(path);
                  mMediaPlayer.prepare();
                  mMediaPlayer.seekTo(60000);
                  mMediaPlayer.start();
                  }
                  catch(IOException e){
                  e.printStackTrace();
                  }
                  }
                  });


                  mTestplay1.setOnClickListener(new OnClickListener() {
                  @Override
                  public void onClick(View v) {
                  System.out.println("kkkkkk0000000000000");
                  try{
                  mMediaPlayer1 = new MediaPlayer();
                  String path = "/storage/sdcard0/Music/melody_r.mp3";
                  mMediaPlayer1.setDataSource(path);
                  mMediaPlayer1.prepare();
                  mMediaPlayer1.start();
                  }
                  catch(IOException e){
                  e.printStackTrace();
                  }
                    
                  }
                  });

                  mTeststop.setOnClickListener(new OnClickListener() {
                  @Override
                  public void onClick(View v) {
                  mMediaPlayer1.stop();
                  mMediaPlayer.stop();
                  mMediaPlayer1.release();
                  mMediaPlayer.release();

                  //                    mMediaPlayer = null;
                  //                    mMediaPlayer1 = null;
                  }
                  });
        */
        
        //        mMediaButtonReceiverComponent = new ComponentName(this, RemoteControlReceiver.class);
        //        mMediaButtonReceiverComponent = new ComponentName(getActivity(), RemoteControlReceiver.class);
        //        RemoteControlReceiver.setActivity(this);
        
        //        mMediaButtonReceiverComponent = new ComponentName(getActivity().getPackageName(), RemoteControlReceiver.class.getName());
        //        am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        //        System.out.println("fragentsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssshow1111111111111111111111111");
        //        am.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);

        return mView;
    }
    
    /*    public void unRegiserMediaButton()
    {
    }
    */

    private void initPerTime()
    {
        mTimeper1 = PreferenceUtil.getMusicStartStToPreference(getActivity());
        mTimeper2 = PreferenceUtil.getMusicEndStToPreference(getActivity());
        
        mTimestper1.setText(String.valueOf(mTimeper1));
        mTimestper2.setText(String.valueOf(mTimeper2));

        mTimestper1.setTextColor(Color.GREEN);
        mTimestper2.setTextColor(Color.GREEN);
    }
    

    private void saveCutToPreference()
    {
        PreferenceUtil.saveMusicStartStToPreference(getActivity(),mTimest1.getTime()-mStartTime.getTime());
        PreferenceUtil.saveMusicEndStToPreference(getActivity(),mTimest2.getTime()-mStartTime.getTime());
        long indid = System.currentTimeMillis();

        MusicSection csect = new MusicSection("h1"+indid, mTimest1.getTime()-mStartTime.getTime(), mTimest2.getTime()-mStartTime.getTime(), 0l);
        PreferenceUtil.saveMusicSections(getActivity(), csect);
        
        //        PreferenceUtil.saveMusicStart(getActivity(),mTimest1.getTime()-mStartTime.getTime(), indid);
        //        PreferenceUtil.saveMusicEnd(getActivity(),mTimest2.getTime()-mStartTime.getTime(), indid);
        //        PreferenceUtil.saveMusicIndex(getActivity(), indid);

    }
    
    public void onResume()
    {
        super.onResume();
        //        AlertUtil.showAlert(IndexActivity.this, R.string.required_fields, R.string.url_username_password_required);
        if(WordPress.hasValidWPComCredentials(getActivity()))
            System.out.println(WordPress.getValidUsername(getActivity()));
        //            System.out.println("haha");
        else
            ;
        //            System.out.println("wawa");
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );
        
        inflater.inflate(R.menu.events, menu);

        //        addNew = findViewById(R.id.addnew);
        //        addNew.setTypeface(font);
        
    }

    private void startOriMusic()
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

    /*    private void stopRecord()
          {
          mMediaRecorder.stop();  // stop the recording
          releaseMediaRecorder(); // release the MediaRecorder object
          isRecording = false;
          }
    */

    @Override
    public void onStart()
    {
        super.onStart();
        //        am.registerMediaButtonEventReceiver(RemoteControlReceiver);
        //        mAudioManager.registerMediaButtonEventReceiver(mRecoItem.getManItem().getItemName()));
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //        am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
        //        am.unregisterMediaButtonEventReceiver(mMediaButtonReceiverComponent);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
            /*        case R.id.events_add:
                      AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            
                      break;
            */
        case R.id.menu_signout:
            i = new Intent(getActivity(), NewEventActivity.class);
            startActivity(i);
            break;
        }
        
        return false;
    }


    /*    private void releaseMediaRecorder(){

          System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrlease record");
        
          if (mMediaRecorder != null) {
          // clear recorder configuration
          mMediaRecorder.reset();
          // release the recorder object
          mMediaRecorder.release();
          mMediaRecorder = null;
          // Lock camera for later use i.e taking it back from MediaRecorder.
          // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
          //            mCamera.lock();
          }
          }

          class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

          @Override
          protected Boolean doInBackground(Void... voids) {
          // initialize video camera
          if (prepareVideoRecorder()) {
          // Camera is available and unlocked, MediaRecorder is prepared,
          // now you can start recording
          System.out.println("sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssstart media record");
          mMediaRecorder.start();

          isRecording = true;

          } else {
          // prepare didn't work, release the camera
          releaseMediaRecorder();
          return false;
          }
          return true;
          }

          @Override
          protected void onPostExecute(Boolean result) {
          if (!result) {
          //MainActivity.this.finish();
          getActivity().finish();
          }
          // inform the user that recording has started
          //            setCaptureButtonText("Stop");
          }
          }

          //    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
          private boolean prepareVideoRecorder(){
        
          // BEGIN_INCLUDE (configure_preview)
          //        mCamera = CameraHelper.getDefaultCameraInstance();

          // We need to make sure that our preview and recording video size are supported by the
          // camera. Query camera to find all the sizes and choose the optimal size given the
          // dimensions of our preview surface.
          //        Camera.Parameters parameters = mCamera.getParameters();
          //        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
          //        Camera.Size optimalSize = CameraHelper.getOptimalPreviewSize(mSupportedPreviewSizes,
          //                mPreview.getWidth(), mPreview.getHeight());

          // Use the same size for recording profile.
          //        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
          //        profile.videoFrameWidth = optimalSize.width;
          //        profile.videoFrameHeight = optimalSize.height;

          // likewise for the camera object itself.
          //        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
          //        mCamera.setParameters(parameters);
          //        try {
          // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
          // with {@link SurfaceView}
          //                mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
          //        } catch (IOException e) {
          //            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
          //            return false;
          //        }
          // END_INCLUDE (configure_preview)

          // BEGIN_INCLUDE (configure_media_recorder)
          mMediaRecorder = new MediaRecorder();
        
          // Step 1: Unlock and set camera to MediaRecorder
          //        mCamera.unlock();
          //        mMediaRecorder.setCamera(mCamera);

          // Step 2: Set sources
          //        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
          //        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

          // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
          //mMediaRecorder.setProfile(profile);

          // Step 4: Set output file
          //        mMediaRecorder.setOutputFile(CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO).toString());

          mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
          mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
          //        mMediaRecorder.setOutputFile(mFileName);
          mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);        
          //        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());
          mMediaRecorder.setOutputFile(getOutputMediaFile());
          // END_INCLUDE (configure_media_recorder)
        
          // Step 5: Prepare configured MediaRecorder
          try {
          mMediaRecorder.prepare();
          } catch (IllegalStateException e) {
          //            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
          e.printStackTrace();
          releaseMediaRecorder();
          return false;
          } catch (IOException e) {
          e.printStackTrace();
          //            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
          releaseMediaRecorder();
          return false;
          }
          return true;
          }
    */

    //    private File getOutputMediaFile()
    /*    private String getOutputMediaFile()
          {
          if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
          System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeerrrrrrrrrrrrrrrrrr no media mount");
          return  null;
          }

          File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_PICTURES), "SpiderSample");

          if (! mediaStorageDir.exists()){
          if (! mediaStorageDir.mkdirs()) {
          System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeerrrrrrrrrrrrrrrrrr fail to create dir");
          //                Log.d("CameraSample", "failed to create directory");
          return null;
          }
          }

          String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
          File mediaFile;

          //        return new File(mediaStorageDir.getPath() + File.separator + "SPIDER_"+ timeStamp + ".mp3");
          return new File(mediaStorageDir.getPath() + File.separator + "SPIDER_"+ "test" + ".3gp");

        
          //        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/spider-test.3gp";
          //        return "/storage/sdcard0/Music/spider_test.3gp";
          //        mFileName += "/audiorecordtest.3gp";
          }
    */

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
    

    private void stopPlaying() {
        if(mRecordPlayer!=null){
            mRecordPlayer.release();
            mRecordPlayer = null;
        }
    }

    private void saveReset()
    {
        if(mStartOriHan!=null)
            mStartOriHan.removeCallbacksAndMessages(null);
        if(mStartRecordHan!=null)
            mStartRecordHan.removeCallbacksAndMessages(null);
        if(mStartReplayHan!=null)
            mStartReplayHan.removeCallbacksAndMessages(null);
        
        mMediaPlayer.stop();
        //        mMediaPlayer1.stop();
        //        stopPlaying();
        //        stopRecording();



        mTimesttext1.setTextColor(Color.GRAY);
        mTimesttext2.setTextColor(Color.GRAY);

        mTimesttext1.setText("0");
        mTimesttext2.setText("0");

        mTimest1 = null;
        mTimest2 = null;
    }
}

