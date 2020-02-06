package com.innocomm.ilockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class BootReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Settings.canDrawOverlays(LockApplication.getInstance() )&& LockApplication.getInstance().getServiceSwitch()) {
                LockScreenMgr.getInstance().active(true);
            }
        }
    }
}
