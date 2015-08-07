package com.futcore.restaurant.ui;

import com.futcore.restaurant.NewEventItemActivity;
import com.futcore.restaurant.TestSendActivity;
import android.content.Intent;

import android.net.Uri;

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


import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

//public class IndexFragment extends Fragment implements OnClickListener
public class RecoFragment extends SherlockFragment implements OnClickListener, LocationListener
{
    private View mView;
    
    public String errorMsg = "";

    private Button recoEvent;

    private Location mCurrentLocation;
    private LocationHelper mLocationHelper;

    private int ACTIVITY_NEW_EVENT = 0;


    protected LinearLayout pipe1;
    protected LinearLayout pipe2;
    
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

    //    public void onCreate(Bundle savedInstanceState)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state){
        setHasOptionsMenu(true);
        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );
        
        mView = inflater.inflate(R.layout.reco, parent, false);

        pipe1 = (LinearLayout)mView.findViewById(R.id.pipe1);
        pipe2 = (LinearLayout)mView.findViewById(R.id.pipe2);

        for(int i=0; i<3;i++){
            LinearLayout itemBlock = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_block, null);

            itemBlock.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:18516211115"));
                        startActivity(callIntent);
                    }
                });


            TextView itemTitle = (TextView)itemBlock.findViewById(R.id.average);
            TextView itemDist = (TextView)itemBlock.findViewById(R.id.dist);
            TextView itemPrice = (TextView)itemBlock.findViewById(R.id.price);
            ImageView itemImage = (ImageView)itemBlock.findViewById(R.id.testBanner);

            if(i==0){
                itemTitle.setText("免费桃子谁要！");
                //                itemImage.setImageResource(R.drawable.i22);
                itemDist.setText("5m");
            }
            else if(i==1){
                itemTitle.setText("eBay T-shirt size S");
                itemImage.setImageResource(R.drawable.i22);
                itemDist.setText("8m");
                itemPrice.setText("$0.90");
            }
            else if(i==2){
                itemTitle.setText("Good Fan");
                itemImage.setImageResource(R.drawable.i55);
                itemDist.setText("3m");
                itemPrice.setText("$0.00");
            }
            
            pipe1.addView(itemBlock);
        }

        for(int i=0; i<2;i++){
            LinearLayout itemBlock = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.item_block, null);

            itemBlock.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:18516211115"));
                        startActivity(callIntent);
                    }
                });

            TextView itemTitle = (TextView)itemBlock.findViewById(R.id.average);
            TextView itemDist = (TextView)itemBlock.findViewById(R.id.dist);
            TextView itemPrice = (TextView)itemBlock.findViewById(R.id.price);
            ImageView itemImage = (ImageView)itemBlock.findViewById(R.id.testBanner);

            
            if(i==0){
                itemTitle.setText("Very Cute");
                itemImage.setImageResource(R.drawable.i33);
                itemDist.setText("12m");
                itemPrice.setText("$2.00");
            }
            else if(i==1){
                itemTitle.setText("It's free");
                itemImage.setImageResource(R.drawable.i44);
                itemDist.setText("15m");
                itemPrice.setText("$0.00");
            }
            
            //            itemTitle.setText("$20.00");
            
            pipe2.addView(itemBlock);
        }

        
        //        pipe2.addView();

        return mView;
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
    
        

    public void onLocationChanged(Location location) {
        AlertUtil.showAlert(getActivity(), R.string.required_fields, R.string.url_username_password_required);
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
        //        inflater.inflate(R.menu.reco, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        return false;
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onClick(View v)
    {
        
    }

}

