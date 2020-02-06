package com.innocomm.ilockscreen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by andika on 2/19/17.
 */

public class LockScreenMgr {
    private static final boolean DEBUG = true;
    private static final String TAG = "LockScreenMgr";
    public static final String TAG_SHOW_NOW = "shownow";
    private static LockScreenMgr singleton;
    Context context;

    public static LockScreenMgr getInstance() {
        if(singleton==null){
            singleton = new LockScreenMgr();

        }
        return singleton;
    }

    public void init(Context context){
        this.context = context;

    }

    public void active(boolean shownow){

        LockApplication.getInstance().setServiceSwitch(true);
        if(context!=null) {
            Intent mIntent = new Intent(context, OverlayService.class);
            if (shownow) mIntent.putExtra(TAG_SHOW_NOW, shownow);
            context.startForegroundService(mIntent);
        }
    }

    public void deactivate(){
        LockApplication.getInstance().setServiceSwitch(false);
        if(context!=null) {
            context.stopService(new Intent(context, OverlayService.class));
        }
    }
    public boolean isActive(){

        return LockApplication.getInstance().getServiceState();

    }

    public static final void printlog(String tag,String msg){
        if (DEBUG) Log.v(tag, msg);
    }

}
