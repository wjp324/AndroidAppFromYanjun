package com.futcore.restaurant.util;

import java.util.Date;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.futcore.restaurant.models.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class PreferenceUtil
{
    
    private static final String MUSIC_START_TIMEST = "musicstartst";
    private static final String MUSIC_END_TIMEST = "musicendst";
    private static final String MUSICS = "musics";
    private static final String MUSIC_SECTIONS = "musicsections";

    public static void saveMusicStartStToPreference(Context context, Long sec)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(MUSIC_START_TIMEST, sec).apply();
    }

    public static Long getMusicStartStToPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(MUSIC_START_TIMEST, 0l);
    }

    public static void saveMusicEndStToPreference(Context context, Long sec)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(MUSIC_END_TIMEST, sec).apply();
    }

    public static Long getMusicEndStToPreference(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(MUSIC_END_TIMEST, 0l);
    }

    public static void saveMusicStart(Context context, Long sec, Long id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(id+"stm", sec).apply();
    }

    public static Long getMusicStart(Context context, Long id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(id+"stm", 0l);
    }

    public static void saveMusicEnd(Context context, Long sec, Long id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putLong(id+"enm", sec).apply();
    }

    public static Long getMusicEnd(Context context,Long id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(id+"enm", 0l);
    }

    public static void delMusicStartAndEnd(Long id)
    {
    }

    public static void saveMusicIndex(Context context, Long id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        List<Long> musics = getMusicIndex(context);
        musics.add(id);
        Gson gson=  new GsonBuilder().create();
        pref.edit().putString(MUSICS, gson.toJson(musics)).apply();
        //        pref.edit().putLong(id+"enm", sec).apply();
    }

    public static List<Long> getMusicIndex(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String misStr = pref.getString(MUSICS, "");
        if(misStr.equals(""))
            return (new ArrayList<Long>());

        GsonBuilder gsonBuilder=  new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type recoType = new TypeToken<List<Long>>() {}.getType();

        return gson.fromJson(misStr, recoType);
        //        return pref.getLong(id+"enm", 0l);
        //        return null;
    }

    public static void saveMusicSections(Context context, MusicSection sect)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        List<MusicSection> musics = getMusicSections(context);
        musics.add(sect);
        Gson gson=  new GsonBuilder().create();
        pref.edit().putString(MUSIC_SECTIONS, gson.toJson(musics)).apply();
        //        pref.edit().putLong(id+"enm", sec).apply();
    }

    public static void removeMusicSection(Context context, MusicSection sect)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        List<MusicSection> musics = getMusicSections(context);
        System.out.println("*********************"+musics.size());
        //        musics.remove(sect);

        for (Iterator<MusicSection> iter = musics.listIterator(); iter.hasNext(); ) {
            MusicSection a = iter.next();
            if (a.getSectid().equals(sect.getSectid())) {
                iter.remove();
            }
        }
        System.out.println("4*********************"+musics.size());
        
        for(MusicSection cc:musics){
            System.out.println(cc.getSectid());
        }
        System.out.println("00000000000000000000000+"+sect.getSectid());
        Gson gson=  new GsonBuilder().create();
        pref.edit().putString(MUSIC_SECTIONS, gson.toJson(musics)).apply();
    }

    public static List<MusicSection> getMusicSections(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        //        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String misStr = pref.getString(MUSIC_SECTIONS, "");
        if(misStr.equals(""))
            return (new ArrayList<MusicSection>());

        GsonBuilder gsonBuilder=  new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type recoType = new TypeToken<List<MusicSection>>() {}.getType();

        return gson.fromJson(misStr, recoType);
        //        return pref.getMusicSection(id+"enm", 0l);
        //        return null;
    }
    

    
}

