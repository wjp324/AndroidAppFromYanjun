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

import android.widget.Button;
import android.widget.EditText;

//public class IndexFragment extends Fragment implements OnClickListener
public class OngoingFragment extends SherlockFragment implements OnClickListener
{
    private View mView;

    private Button saveBut;
    private EditText nameEdit;

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

        nameEdit = (EditText)mView.findViewById(R.id.itemname);
        saveBut = (Button)mView.findViewById(R.id.saveWish);
        
        saveBut.setOnClickListener(this);
        
        return mView;
    }

    public void onClick(View v)
    {
        //        super.onClick(v);
        int id = v.getId();
        switch(id){
        case R.id.saveWish:
            {
                AlertUtil.showAlert(getActivity(), R.string.required_fields, nameEdit.getText().toString().trim());
            }
            break;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }







    // HTTP GET request
    /*	private void sendGet() throws Exception {
		String url = "http://www.google.com/search?q=mkyong";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
	}
	
	// HTTP POST request
	private void sendPost() throws Exception {
		String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());

	}
    */
}

