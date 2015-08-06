package com.futcore.restaurant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
//import com.commonsware.cwac.wakeful.WakefulIntentService;

//public class AlertListener implements WakefulIntentService.AlarmListener {
public class AlertListener
{
    /*  public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
      //      mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+60000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
      //      mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5000, AlarmManager.INTERVAL_FIFTEEN_SECONDS, pi);
      //      mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5000, 15000, pi);
      final int FIFTEEN_SEC_MILLIS = 10000;

      System.out.println("startlonggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg4444444444444444444444444444444444444444444444444444444444444444443333333333333333333333333333333");
      
      //      mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,FIFTEEN_SEC_MILLIS, pi);

      mgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+FIFTEEN_SEC_MILLIS, FIFTEEN_SEC_MILLIS, pi);
  }

  public void sendWakefulWork(Context ctxt) {
    WakefulIntentService.sendWakefulWork(ctxt, AlertService.class);
  }

  public long getMaxAge() {
    return(AlarmManager.INTERVAL_FIFTEEN_MINUTES*2);
  }
    */
}

