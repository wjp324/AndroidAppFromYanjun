package com.futcore.restaurant.ui;

import com.futcore.restaurant.NewEventItemActivity;
import com.futcore.restaurant.TestSendActivity;
import android.content.Intent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.futcore.restaurant.R;
import com.futcore.restaurant.WordPress;
///import android.support.v4.app.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.futcore.restaurant.util.LocationHelper;
import com.futcore.restaurant.util.AlertUtil;
import com.futcore.restaurant.util.LocationHelper.LocationResult;

import com.futcore.restaurant.util.JSONReader;

import java.util.Date;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.futcore.restaurant.models.ManCertPlace;


//public class IndexFragment extends Fragment implements OnClickListener
public class RecoFragment extends SherlockFragment implements OnClickListener, LocationListener
{
    private View mView;
    
    public String errorMsg = "";

    private Button recoEvent;

    private Location mCurrentLocation;
    private LocationHelper mLocationHelper;

    private int ACTIVITY_NEW_EVENT = 0;

    CurrentEventsListFragment eventList = null;
//    RecoEventsListFragment recoList = null;
        
    //    private int ACTIVITY_ADD_COMMENT = 1;
    
    public static RecoFragment newInstance() {
        RecoFragment fragment = new RecoFragment();
        //        fragment.mContent = content;
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getLocationProvider();
        //        mNum = getArguments() != null ? getArguments().getInt("num") : 0;
    }

    private void getLocationProvider() {
        boolean hasLocationProvider = false;
        //        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String providerName : providers) {
            if (providerName.equals(LocationManager.GPS_PROVIDER) || providerName.equals(LocationManager.NETWORK_PROVIDER)) {
                hasLocationProvider = true;
            }
        }
        if (hasLocationProvider) {
            //            System.out.println("hashashashashas");
            enableLBSButtons();
        }
    }

    private void enableLBSButtons()
    {
        mLocationHelper = new LocationHelper();
        //        mLocationHelper.getLocation(ShakeShopActivity.this, locationResult);
        mLocationHelper.getLocation(getActivity(), locationResult);
    }

    private LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });
                }
            }
        };
    
        
    //    public void onCreate(Bundle savedInstanceState)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state){
        setHasOptionsMenu(true);
        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );
        
        mView = inflater.inflate(R.layout.reco, parent, false);


        //        CurrentEventsListFragment eventList = new CurrentEventsListFragment();
        eventList = new CurrentEventsListFragment();
        //        recoList = new RecoEventsListFragment();
        
        
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.currentEventsList, eventList).commit();

        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
        //        transaction1.add(R.id.recoEventsList, recoList).commit();

        eventList.refreshEvents();
        //        recoList.refreshEvents();

        recoEvent = (Button)mView.findViewById(R.id.recoevent);
        
        recoEvent.setTypeface(font);
        
        recoEvent.setOnClickListener(this);
        

        return mView;
    }

    /*    //    @Override
          protected void onPause() {
          super.onPause();
          if (mLocationHelper != null)
          mLocationHelper.cancelTimer();
          }
    */

    public void onLocationChanged(Location location) {
        //        AlertUtil.showAlert(ShakeShopActivity.this, R.string.required_fields, R.string.url_username_password_required);
        AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
        //                longitude = location.getLongitude();
        //                latitude = location.getLatitude();
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
            /*        case R.id.justtest:
            //            AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            //            new GetCertPlaceTask().execute();
            i = new Intent(getActivity(), JustTestActivity.class);
            startActivity(i);
            break;
        case R.id.updateall:
            //            AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            //            new GetCertPlaceTask().execute();
            i = new Intent(getActivity(), UpdateAllActivity.class);
            startActivity(i);
            break;
        case R.id.updatereco:
            AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            //            new GetCertPlaceTask().execute();
            i = new Intent(getActivity(), UpdateRecoActivity.class);
            startActivity(i);
            break;
            */
        case R.id.new_item_event:
            //            AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            //            new GetCertPlaceTask().execute();
            i = new Intent(getActivity(), NewEventItemActivity.class);
            //            startActivity(i);
            startActivityForResult(i, ACTIVITY_NEW_EVENT);
            break;
            /*        case R.id.testsend:
            i = new Intent(getActivity(), TestSendActivity.class);
            startActivityForResult(i, ACTIVITY_NEW_EVENT);
            break;
            */
        }
        
        return false;
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ACTIVITY_NEW_EVENT) {
            eventList.refreshEvents();
            //            return;
        }
    }

    public void onClick(View v)
    {
        int id = v.getId();
        
        switch(id){
        case R.id.recoevent:
            //            AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
            mLocationHelper.getLocation(getActivity(), locationResult);
            AlertUtil.showAlert(getActivity(), R.string.required_fields, String.valueOf(mCurrentLocation.getLatitude())+" "+String.valueOf(mCurrentLocation.getLongitude())+" acc:"+String.valueOf(mCurrentLocation.getAccuracy()));
            
            break;
        }
    }

}

