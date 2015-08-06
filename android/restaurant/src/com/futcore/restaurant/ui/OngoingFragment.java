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
import android.support.v4.app.FragmentTransaction;

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

import android.widget.Button;
import android.widget.EditText;


import java.io.BufferedReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.InputStreamReader;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import java.io.UnsupportedEncodingException;


//public class IndexFragment extends Fragment implements OnClickListener
public class OngoingFragment extends SherlockFragment implements OnClickListener
{
    private View mView;

    private int ACTIVITY_NEW_EVENT = 0;

    CurrentEventsListFragment eventList = null;

    public static OngoingFragment newInstance() {
        OngoingFragment fragment = new OngoingFragment();
        //        fragment.mContent = content;
        return fragment;
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state){
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.ongoing, parent, false);

        eventList = new CurrentEventsListFragment();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.currentEventsList, eventList).commit();

        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();

        eventList.refreshWishs();
        
        
        return mView;
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
        switch(id){
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reco, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
        case R.id.new_item_event:
            //            i = new Intent(getActivity(), NewEventItemActivity.class);
            i = new Intent(getActivity(), NewOngoingActivity.class);
            startActivityForResult(i, ACTIVITY_NEW_EVENT);
            break;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ACTIVITY_NEW_EVENT) {
            eventList.refreshWishs();
            //            return;
        }
    }
    
    
}

