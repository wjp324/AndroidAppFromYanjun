package com.futcore.restaurant.testsensor;

// import com.example.android.wearable.jumpingjack.fragments.CounterFragment;
// import com.example.android.wearable.jumpingjack.fragments.SettingsFragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.futcore.restaurant.R;

/**
 * The main activity for the Jumping Jack application. This activity registers itself to receive
 * sensor values. Since on wearable devices a full screen activity is very short-lived, we set the
 * FLAG_KEEP_SCREEN_ON to give user adequate time for taking actions but since we don't want to
 * keep screen on for an extended period of time, there is a SCREEN_ON_TIMEOUT_MS that is enforced
 * if no interaction is discovered.
 *
 * This activity includes a {@link android.support.v4.view.ViewPager} with two pages, one that
 * shows the current count and one that allows user to reset the counter. the current value of the
 * counter is persisted so that upon re-launch, the counter picks up from the last value. At any
 * stage, user can set this counter to 0.
 */

public class TestSensorActivity extends Activity implements SensorEventListener {
    
    private static final String TAG = "JJTestSensorActivity";

    /** How long to keep the screen on when no activity is happening **/
    private static final long SCREEN_ON_TIMEOUT_MS = 20000; // in milliseconds

    /** an up-down movement that takes more than this will not be registered as such **/
    private static final long TIME_THRESHOLD_NS = 2000000000; // in nanoseconds (= 2sec)

    /**
     * Earth gravity is around 9.8 m/s^2 but user may not completely direct his/her hand vertical
     * during the exercise so we leave some room. Basically if the x-component of gravity, as
     * measured by the Gravity sensor, changes with a variation (delta) > GRAVITY_THRESHOLD,
     * we consider that a successful count.
     */
    private static final float GRAVITY_THRESHOLD = 7.0f;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mLastTime = 0;
    private boolean mUp = false;
    private int mJumpCounter = 0;
    //    private ViewPager mPager;
    //    private CounterFragment mCounterPage;
    //    private SettingsFragment mSettingPage;
    //    private ImageView mSecondIndicator;
    //    private ImageView mFirstIndicator;
    private TextView mTestCount1;
    private TextView mTestCount2;
    private TextView mTestCount3;
    private TextView mTestCount4;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        setContentView(R.layout.jj_layout);
        setContentView(R.layout.testsensor);
        //        setupViews();
        mTestCount1 = (TextView)findViewById(R.id.testcount1);
        mTestCount2 = (TextView)findViewById(R.id.testcount2);
        mTestCount3 = (TextView)findViewById(R.id.testcount3);
        mTestCount4 = (TextView)findViewById(R.id.testcount4);

        mTestCount1.setText("0");
        mTestCount2.setText("0");
        mTestCount3.setText("0");
        mTestCount4.setText("0");
        
        mHandler = new Handler();
        mJumpCounter = Utils.getCounterFromPreference(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        renewTimer();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    /*    private void setupViews() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mFirstIndicator = (ImageView) findViewById(R.id.indicator_0);
        mSecondIndicator = (ImageView) findViewById(R.id.indicator_1);
        final PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        mCounterPage = new CounterFragment();
        mSettingPage = new SettingsFragment(this);
        adapter.addFragment(mCounterPage);
        adapter.addFragment(mSettingPage);
        setIndicator(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                setIndicator(i);
                renewTimer();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mPager.setAdapter(adapter);
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Successfully registered for the sensor updates");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Unregistered for sensor events");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //        detectJump(event.values[0], event.timestamp);
        detectJump(event.values[1], event.timestamp);
        //        detectJump1(event.values[1], event.timestamp);
        //        detectJump2(event.values[2], event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * A simple algorithm to detect a successful up-down movement of hand(s). The algorithm is
     * based on the assumption that when a person is wearing the watch, the x-component of gravity
     * as measured by the Gravity Sensor is +9.8 when the hand is downward and -9.8 when the hand
     * is upward (signs are reversed if the watch is worn on the right hand). Since the upward or
     * downward may not be completely accurate, we leave some room and instead of 9.8, we use
     * GRAVITY_THRESHOLD. We also consider the up <-> down movement successful if it takes less than
     * TIME_THRESHOLD_NS.
     */
    private void detectJump(float xValue, long timestamp) {
        if ((Math.abs(xValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (xValue > 0)) {
                onJumpDetected(!mUp);
            }
            mUp = xValue > 0;
            mLastTime = timestamp;
        }
    }

    /*    private void detectJump1(float yValue, long timestamp) {
        if ((Math.abs(yValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (yValue > 0)) {
                onJumpDetected1(!mUp);
            }
            mUp = yValue > 0;
            mLastTime = timestamp;
        }
    }

    private void detectJump2(float zValue, long timestamp) {
        if ((Math.abs(zValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (zValue > 0)) {
                onJumpDetected2(!mUp);
            }
            mUp = zValue > 0;
            mLastTime = timestamp;
        }
    }
    */
    
    

    /**
     * Called on detection of a successful down -> up or up -> down movement of hand.
     */
    private void onJumpDetected(boolean up) {
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        mJumpCounter++;
        setCounter(mJumpCounter);
        renewTimer();
    }

    /*    private void onJumpDetected1(boolean up) {
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        mJumpCounter++;
        setCounter1(mJumpCounter);
        renewTimer();
    }

    private void onJumpDetected2(boolean up) {
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        mJumpCounter++;
        setCounter2(mJumpCounter);
        renewTimer();
    }
    */
    
    

    /**
     * Updates the counter on UI, saves it to preferences and vibrates the watch when counter
     * reaches a multiple of 10.
     */
    private void setCounter(int i) {
        //        mCounterPage.setCounter(i);
        mTestCount1.setText(String.valueOf(i));
        Utils.saveCounterToPreference(this, i);
        if (i > 0 && i % 10 == 0) {
            Utils.vibrate(this, 0);
        }
    }

    /*    private void setCounter1(int i) {
        //        mCounterPage.setCounter(i);
        mTestCount2.setText(String.valueOf(i));
        Utils.saveCounterToPreference(this, i);
        if (i > 0 && i % 10 == 0) {
            Utils.vibrate(this, 0);
        }
    }

    private void setCounter2(int i) {
        //        mCounterPage.setCounter(i);
        mTestCount3.setText(String.valueOf(i));
        Utils.saveCounterToPreference(this, i);
        if (i > 0 && i % 10 == 0) {
            Utils.vibrate(this, 0);
        }
    }
    */

    public void resetCounter() {
        setCounter(0);
        renewTimer();
    }

    /**
     * Starts a timer to clear the flag FLAG_KEEP_SCREEN_ON.
     */
    private void renewTimer() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG,
                            "Removing the FLAG_KEEP_SCREEN_ON flag to allow going to background");
                }
                resetFlag();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, SCREEN_ON_TIMEOUT_MS);
    }

    /**
     * Resets the FLAG_KEEP_SCREEN_ON flag so activity can go into background.
     */
    private void resetFlag() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Resetting FLAG_KEEP_SCREEN_ON flag to allow going to background");
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                finish();
            }
        });
    }

    /**
     * Sets the page indicator for the ViewPager.
     */
    /*    private void setIndicator(int i) {
        switch (i) {
            case 0:
                mFirstIndicator.setImageResource(R.drawable.full_10);
                mSecondIndicator.setImageResource(R.drawable.empty_10);
                break;
            case 1:
                mFirstIndicator.setImageResource(R.drawable.empty_10);
                mSecondIndicator.setImageResource(R.drawable.full_10);
                break;
        }
    }
    */


}
