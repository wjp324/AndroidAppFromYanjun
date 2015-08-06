package com.futcore.restaurant.util;

import java.util.Date;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;



//import com.futcore.restaurant.models.ManItemScore;
import com.futcore.restaurant.models.*;
    
public class PreferenceUtil
{
    public static final int MAX_BUFFERED_EVENT = 10;
    
    private static final String PREF_KEY_CON_RECO = "conreco";
    private static final String PREF_KEY_ACC_RECO = "accreco";
    private static final String PREF_KEY_DAILY_ALARM_TOUCH = "dailyalarmtouch";
    private static final String PREF_KEY_BUF_EVENT = "bufevent";

    public static void saveDailyAlarmTouchToPreference(Context context, Long sec)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(PREF_KEY_DAILY_ALARM_TOUCH, sec).apply();
    }

    public static Long getDailyAlarmTouchFromPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(PREF_KEY_DAILY_ALARM_TOUCH, 0l);
    }
    
    public static void saveConRecoToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value < 0) {
            // we want to remove
            pref.edit().remove(PREF_KEY_CON_RECO).apply();
        } else {
            pref.edit().putInt(PREF_KEY_CON_RECO, value).apply();
        }
    }

    /**
     * Retrieves the value of counter from preference manager. If no value exists, it will return
     * <code>0</code>.
     */
    public static int getConRecoFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_KEY_CON_RECO, 0);
    }

    public static void saveAccRecoToPreference(Context context, ManItemScore mis, long time)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        
        Map<Long, ManItemScore> orio = getAccRecoFromPreference(context);
        orio.put(time, mis);

        //        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Gson gson=  new GsonBuilder().create();
        //        StringEntity  postingString =new StringEntity(gson.toJson(eves)); //convert your pojo to   json
        pref.edit().putString(PREF_KEY_ACC_RECO, gson.toJson(orio)).apply();
        
    }

    public static Map<Long, ManItemScore> getAccRecoFromPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String misStr = pref.getString(PREF_KEY_ACC_RECO, "");
        if(misStr.equals(""))
            return (new HashMap<Long, ManItemScore>());
        //            return null;

        GsonBuilder gsonBuilder=  new GsonBuilder();
        Gson gson = gsonBuilder.create();
        
        Type recoType = new TypeToken<Map<Long, ManItemScore>>() {}.getType();

        return gson.fromJson(misStr, recoType);
    }

    //    public static void savedBufferedEventsToPreference(Context context, List<ManEventWear>)
    public static void saveBufferedEventToPreference(Context context, ManEventWear eve)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        
        List<ManEventWear> orio = getBufferedEventsFromPreference(context);
        //        orio.put(time, mis);
        orio.add(eve);
        if(orio.size()>MAX_BUFFERED_EVENT)
            orio.remove(0);
        
        //        Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Gson gson=  new GsonBuilder().create();
        //        StringEntity  postingString =new StringEntity(gson.toJson(eves)); //convert your pojo to   json
        pref.edit().putString(PREF_KEY_BUF_EVENT, gson.toJson(orio)).apply();
    }

    public static List<ManEventWear> getBufferedEventsFromPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String misStr = pref.getString(PREF_KEY_BUF_EVENT, "");
        if(misStr.equals(""))
            return (new ArrayList<ManEventWear>());
            //            return (new HashMap<Long, ManItemScore>());
        //            return null;
        
        GsonBuilder gsonBuilder=  new GsonBuilder();
        Gson gson = gsonBuilder.create();
        
        //        Type recoType = new TypeToken<Map<Long, ManItemScore>>() {}.getType();
        Type recoType = new TypeToken<List<ManEventWear>>() {}.getType();

        return gson.fromJson(misStr, recoType);
    }
    
    public static void clearAccRecoFromPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_ACC_RECO, "").apply();
    }
}

