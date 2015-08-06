package com.futcore.restaurant.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.net.Uri;

import android.content.Context;

import java.io.File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.BitmapFactory;

import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.futcore.restaurant.util.LocationHelper;
import com.futcore.restaurant.util.AlertUtil;
import com.futcore.restaurant.util.LocationHelper.LocationResult;

import android.app.AlertDialog;

import android.provider.MediaStore;

import android.content.DialogInterface;
import android.content.Intent;

import com.actionbarsherlock.app.ActionBar;
import com.futcore.restaurant.Constants;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
import com.futcore.restaurant.custom_view.NoSwipeViewPager;
//import com.futcore.restaurant.ui.member.MemberIndexFragment;
import com.futcore.restaurant.util.AlertUtil;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.joanzapata.android.iconify.Iconify.IconValue;

import android.widget.Button;
import android.widget.EditText;

import com.futcore.restaurant.util.TimerFormat;

//import android.support.v13.app.FragmentStatePagerAdapter;

public class NewItemActivity extends WPActionBarActivity implements OnClickListener, LocationListener
{
    private Button cancelBut;
    private Button saveBut;
    private EditText nameEdit;
    private EditText priceEdit;

    private Location mCurrentLocation;
    private LocationHelper mLocationHelper;

    NotificationCompat.Builder mBuilder = null;
    NotificationManager mNotifyMgr = null;

    public static final int NOTI_DANGER = 1;
    public static final int NOTI_ERGENT = 2;
    public static final int NOTI_NORMAL = 3;
    public static final int NOTI_LESS_IMPORTANT = 4;

    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_item);

        cancelBut = (Button)findViewById(R.id.cancelItem);
        saveBut = (Button)findViewById(R.id.saveItem);
        nameEdit = (EditText)findViewById(R.id.itemname);
        priceEdit = (EditText)findViewById(R.id.itemprice);

        cancelBut.setOnClickListener(this);
        saveBut.setOnClickListener(this);

        getLocationProvider();

        initNotification();
    }

    private void initNotification()
    {
        mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_menu_view)
            .setVibrate(new long[] { 1000, 1000, 1000})
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spider_420))
            //            .setUsesChronometer(true)
            .setSubText("12 m")
            //            .setWhen(System.currentTimeMillis())
            .addAction(R.drawable.ic_cc_alarm, getString(R.string.event_finish), null)
            ;

        mNotifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void sendNotification(String title, String conStr, int type)
    {
        int mNotificationId = 001;
        //        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        
        switch(type){
        case NOTI_DANGER:
            mBuilder.setVibrate(new long[] { 1000, 2000, 1000});
            break;
        case NOTI_ERGENT:
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000});
            break;
        case NOTI_NORMAL:
            mBuilder.setVibrate(new long[] { 1000, 500, 1000});
            break;
        case NOTI_LESS_IMPORTANT:
            mBuilder.setVibrate(new long[] { 1000, 300, 1000});
            break;
        default:
            ;
        }
        //    public static final int NOTI_DANGER = 1;
        //    public static final int NOTI_ERGENT = 2;
        //    public static final int NOTI_NORMAL = 3;
        //    public static final int NOTI_LESS_IMPORTANT = 4;
        
        mBuilder.setContentTitle(title).
            setContentText(conStr);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());        
    }

    private void sendNotification(String title, long duration, int type)
    {
        int mNotificationId = 001;
        
        switch(type){
        case NOTI_DANGER:
            mBuilder.setVibrate(new long[] { 1000, 2000, 1000});
            break;
        case NOTI_ERGENT:
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000});
            break;
        case NOTI_NORMAL:
            mBuilder.setVibrate(new long[] { 1000, 500, 1000});
            break;
        case NOTI_LESS_IMPORTANT:
            mBuilder.setVibrate(new long[] { 1000, 300, 1000});
            break;
        default:
            ;
        }
        
        mBuilder.setContentTitle(title).
            setContentText("92 m");
            //            .setWhen(System.currentTimeMillis() + duration);
        
        mNotifyMgr.notify(mNotificationId, mBuilder.build());        
    }


    private void getLocationProvider() {
        boolean hasLocationProvider = false;
        //        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz---------------------------------------------------------------1111111111111111111");
        for (String providerName : providers) {
            if (providerName.equals(LocationManager.GPS_PROVIDER) || providerName.equals(LocationManager.NETWORK_PROVIDER)) {
                hasLocationProvider = true;
            }
        }
        if (hasLocationProvider) {
            System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz---------------------------------------------------------------222222222222222222222222222");
            //            System.out.println("hashashashashas");
            enableLBSButtons();
        }
    }

    private void enableLBSButtons()
    {
        mLocationHelper = new LocationHelper();
        //        mLocationHelper.getLocation(ShakeShopActivity.this, locationResult);
        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz---------------------------------------------------------------888888888888888888888888888888888");
        mLocationHelper.getLocation(NewItemActivity.this, locationResult);
    }

    private LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz---------------------------------------------------------------");
                    mCurrentLocation = location;
                } else {
                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz---------------------------------------------------------------7777777777777777777777777777777777777777");
                    NewItemActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                }
            }
        };
    
    public void onClick(View v)
    {
        int id = v.getId();
        switch(id){
        case R.id.saveItem:
            {
                mLocationHelper.getLocation(NewItemActivity.this, locationResult);
                AlertUtil.showAlert(NewItemActivity.this, R.string.required_fields, String.valueOf(mCurrentLocation.getLatitude())+" "+String.valueOf(mCurrentLocation.getLongitude())+" acc:"+String.valueOf(mCurrentLocation.getAccuracy()));
                finish();
            }
            break;
        case R.id.cancelItem:
            {
                //                sendNotification("test item", 2000, NOTI_NORMAL);
                sendNotification("Free pear!", 2000, NOTI_NORMAL);
                //                finish();
            }
            break;
        }
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onLocationChanged(Location location) {
        //        AlertUtil.showAlert(ShakeShopActivity.this, R.string.required_fields, R.string.url_username_password_required);
        AlertUtil.showAlert(NewItemActivity.this, R.string.required_fields, R.string.url_username_password_required);
        //                longitude = location.getLongitude();
        //                latitude = location.getLatitude();
    }
}

