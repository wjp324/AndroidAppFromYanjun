package com.futcore.restaurant.util;

import java.util.Date;
import java.util.TimeZone;


public class CommonUtil
{
    public static final int TIMEZONE_OFFSET = 8;
    //    public static final int TIMEZONE_OFFSET = 0;
    
    public static int getTimeZoneOffsetOfTime(long time)
    {
        return ((time%(3600*24*1000)+TIMEZONE_OFFSET*3600*1000)>=24*3600*1000)?(TIMEZONE_OFFSET-24):TIMEZONE_OFFSET;
    }

    public static long getTimeStampOfDay(long time)  //sec
    {
        return (time/1000)%(3600*24)+3600*getTimeZoneOffsetOfTime(time);
        //        return (time/1000)%(3600*24)+3600*TIMEZONE_OFFSET;
        //        return 333l;
    }

    public static long getTimeOfDay(long time)  //sec
    {
        return (time)%(3600*24*1000)+3600*1000*getTimeZoneOffsetOfTime(time);
        //        return (time/1000)%(3600*24)+3600*TIMEZONE_OFFSET;
        //        return 333l;
    }

    public static long getTimeFromHourMin(int hour, int min, int dayOffset)
    {
        System.out.println("11155555fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff+"+dayOffset);
        
        long cTime = new Date().getTime();
        
        long timeOfDay = getTimeOfDay(cTime);
        return cTime-timeOfDay+hour*3600*1000+min*60*1000+dayOffset*24*3600*1000;
    }
}
