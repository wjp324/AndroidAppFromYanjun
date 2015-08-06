package com.futcore.restaurant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.view.KeyEvent;

//import com.futcore.restaurant.ui.LEventsFragment;
import com.futcore.restaurant.ui.MusicSectionListFragment;

public class RemoteControlReceiver extends BroadcastReceiver {
    //    private static MainActivity mActivity;
    //    private static EventsFragment mFragment;
    //    private static LEventsFragment mFragment;
    private static MusicSectionListFragment mFragment;
    
    public static void setActivity(MusicSectionListFragment fragment) {
        mFragment = fragment;
        //        mActivity = activity;
    }

    
    /*    public static void setActivity(LEventsFragment fragment) {
        mFragment = fragment;
        //        mActivity = activity;
    }
    */
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk555555555555555555555555555");
                mFragment.jumpToStartRecord();
            }
            
            /*            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                
                System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk2222k222222222222222222222222222222222222222222222222");
                // Handle key press.
            }
            */
        }
    }
}
