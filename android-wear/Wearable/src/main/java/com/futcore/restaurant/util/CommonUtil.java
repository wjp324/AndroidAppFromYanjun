package com.futcore.restaurant.util;

public class CommonUtil
{
    private static final int TIMEZONE_OFFSET = 8;

    public static long getTimeZoneOffsetOfTime(long time)
    {
        return time%(3600*24*1000)+TIMEZONE_OFFSET*3600*1000>=24*3600*1000?TIMEZONE_OFFSET-24:TIMEZONE_OFFSET;
    }

    public static long getTimeStampOfDay(long time)
    {
        return (time/1000)%(3600*24)+3600*getTimeZoneOffsetOfTime(time);
    }
    
}

