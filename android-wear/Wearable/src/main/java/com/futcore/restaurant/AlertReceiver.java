package com.futcore.restaurant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Vibrator;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;

import com.futcore.restaurant.models.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
 
public class AlertReceiver extends BroadcastReceiver {

    private static DatabaseHelper helper;
    private DatabaseHelper databaseHelper = null;

	private static Dao<ManEvent, String> eventDao = null;
	private static Dao<ManEventWear, String> eventWearDao = null;
	private static Dao<ManItem, String> itemDao = null;

    private List<ManEventWear> efeEventWearModel = new ArrayList<ManEventWear>();
    

    /*    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void getDao() {
        if(eventDao==null||itemDao==null||eventWearDao==null){
            helper = getHelper();
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    */
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }

        if(eventDao==null||itemDao==null||eventWearDao==null){
            helper = databaseHelper;
            
            try {
                eventDao = helper.getEventDao();
                itemDao = helper.getItemDao();
                eventWearDao = helper.getEventWearDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        updateEfeEventWearModel();
        
        
        //        getDao();
        
        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");

        System.out.println(efeEventWearModel.size());
        
        //        Intent service = new Intent(context, AlertService.class);

        if(efeEventWearModel.size()==1){
            if((efeEventWearModel.get(0).getEstEndTime()-new Date().getTime())<120*1000){

                PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
                WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
                wakeLock.acquire(10000+1000);
                
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(10000);
            }
        }
        
        //        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))1111");
        //        context.startService(service);
        //        System.out.println(")))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))22222");
    }

    private void updateEfeEventWearModel()
    {
        try{
            QueryBuilder<ManEventWear, String> queryBuilder1 = eventWearDao.queryBuilder();
            queryBuilder1.orderBy(ManEventWear.EST_END_TIME_FIELD_NAME, true).where().eq(ManEventWear.UPDATETIME_FIELD_NAME,0).and().isNotNull(ManEventWear.STARTTIME_FIELD_NAME).and().gt(ManEventWear.EST_END_TIME_FIELD_NAME, new Date().getTime());
            PreparedQuery<ManEventWear> preparedQuery1 = queryBuilder1.prepare();
            
            efeEventWearModel = eventWearDao.query(preparedQuery1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
