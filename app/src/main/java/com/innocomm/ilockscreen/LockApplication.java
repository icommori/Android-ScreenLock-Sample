package com.innocomm.ilockscreen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import static com.innocomm.ilockscreen.LockScreenMgr.printlog;


public class LockApplication extends Application {
    private static final String TAG = "LockApplication";
    private SharedPreferences sharedPref;
    private static String sharedPrefTag = "iscreenlock";
    private static String TAG_SERVICE_STATE = "service_active";
    private static String TAG_SERVICE_SWITCH = "service_switch";
    private static LockApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPref = getSharedPreferences(sharedPrefTag, Context.MODE_PRIVATE);
        LockApplication.getInstance().setServiceState(false);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                System.exit(2);
            }
        });
        LockScreenMgr.getInstance().init(this);
    }

    public static LockApplication getInstance(){
        return mInstance;
    }

    public void setServiceState(boolean enable){
        printlog(TAG,"setServiceState "+enable);
        sharedPref.edit().putBoolean(TAG_SERVICE_STATE,enable).commit();
    }

    public boolean getServiceState(){

        boolean result=  sharedPref.getBoolean(TAG_SERVICE_STATE,false);
        printlog(TAG,"getServiceState "+result);
        return result;
    }

    public void setServiceSwitch(boolean on){
        printlog(TAG,"setServiceSwitch "+on);
        sharedPref.edit().putBoolean(TAG_SERVICE_SWITCH,on).commit();
    }

    public boolean getServiceSwitch(){

        boolean result=  sharedPref.getBoolean(TAG_SERVICE_SWITCH,false);
        printlog(TAG,"getServiceSwitch "+result);
        return result;
    }
}
