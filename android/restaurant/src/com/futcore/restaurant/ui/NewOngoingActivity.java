package com.futcore.restaurant.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.net.Uri;

import android.app.PendingIntent;


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

public class NewOngoingActivity extends WPActionBarActivity implements OnClickListener
{
    private Button saveBut;
    private EditText nameEdit;

    private int ACTIVITY_NEW_EVENT = 0;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_ongoing);

        nameEdit = (EditText)findViewById(R.id.itemname);
        saveBut = (Button)findViewById(R.id.saveWish);
        
        saveBut.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        int id = v.getId();
        switch(id){
        case R.id.saveWish:
            {
                Intent intent = new Intent(Intent.ACTION_CALL);
                
                intent.setData(Uri.parse("tel:18516211115"));
                startActivity(intent);                
                AlertUtil.showAlert(this, R.string.required_fields, nameEdit.getText().toString().trim());
            }
            break;
        }
    }
}
