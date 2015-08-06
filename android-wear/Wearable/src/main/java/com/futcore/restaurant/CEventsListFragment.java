package com.futcore.restaurant;

import android.os.Bundle;  
import android.app.Activity;  
import android.app.FragmentManager;  
import android.app.ListFragment;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.ArrayAdapter;  
import android.widget.ListView;

import java.util.Date;

import com.futcore.restaurant.models.ManItem;
import com.futcore.restaurant.models.ManUser;
import com.futcore.restaurant.models.ManEvent;
import com.futcore.restaurant.models.ManCertPlace;

import com.futcore.restaurant.DatabaseHelper;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;

public class CEventsListFragment extends ListFragment
{
	private static Dao<ManUser, Integer> userDao = null;
	private static Dao<ManEvent, String> eventDao = null;
    
    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            //            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
    

    String[] numbers_text = new String[] { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen" };
    String[] numbers_digits = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };

    public void getDao() {
        if(userDao == null||eventDao == null){
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getContext());
            //            helper = (DatabaseHelper) OpenHelperManager.getHelper(getActivity());
            helper = getHelper();
            
            try {
                userDao = helper.getUserDao();
                eventDao = helper.getEventDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //        new CustomToast(getActivity(), numbers_digits[(int) id]);   
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDao();
        
        try{
            ManUser tuser = new ManUser(new Integer(1), "terry", "123456", new Date(), null, null, null, (byte)0);
            helper.clearAllUser();
            userDao.create(tuser);

            ManUser ccuser = userDao.queryForId(new Integer(1));
            
            numbers_text[0] = ccuser.getUsername();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        

        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, numbers_text);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
