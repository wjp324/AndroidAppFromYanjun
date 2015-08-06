package com.futcore.restaurant.util;

import java.util.Date;
import java.util.Random;

public class ItemIdUtil {
    private static String DEVICE_ID="w1";

    public static String getGenId(String type)
    {
        Random rand= new Random();
        return DEVICE_ID+"_"+type+"_"+new Date().getTime()+"_"+(rand.nextInt(900)+100);
    }
}
