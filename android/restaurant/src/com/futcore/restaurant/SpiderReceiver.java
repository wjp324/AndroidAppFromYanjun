package com.futcore.restaurant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 
public class SpiderReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SpiderService.class);
        context.startService(service);
    }
}
