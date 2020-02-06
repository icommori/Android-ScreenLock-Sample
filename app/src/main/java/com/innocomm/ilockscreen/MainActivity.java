package com.innocomm.ilockscreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import static com.innocomm.ilockscreen.LockScreenMgr.printlog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean askedForOverlayPermission = false;
    private static final int OVERLAY_PERMISSION_CODE = 2;
    private static final int REQUEST_IGNORE_BATTERY_CODE = 3;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        //toggleButton.setEnabled(false);
        if(LockScreenMgr.getInstance().isActive()){
            toggleButton.setChecked(true);
        }else{
            toggleButton.setChecked(false);

        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                printlog(TAG,"onCheckedChanged "+checked);

                if(checked){
                   /* if(!isIgnoringBatteryOptimizations()){
                        toggleButton.setChecked(false);
                        gotoSettingIgnoringBatteryOptimizations();
                        return;
                    }*/

                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        LockScreenMgr.getInstance().active(false);
                    }else{
                        toggleButton.setChecked(false);
                        addOverlay();
                    }
                }else{
                    LockScreenMgr.getInstance().deactivate();
                }
            }
        });

    }

    public void addOverlay() {

            if (!Settings.canDrawOverlays(this)) {
                askedForOverlayPermission = true;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            askedForOverlayPermission = false;
            if (Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                //Toast.makeText(MyProtector.getContext(), "ACTION_MANAGE_OVERLAY_PERMISSION Permission Granted", Toast.LENGTH_SHORT).show();
                //Intent serviceIntent = new Intent(Homepage.this, ChatHeadService.class);
                //serviceIntent.putExtra("removeUserId", friendId);
                //startService(serviceIntent);

            } else {
                Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_IGNORE_BATTERY_CODE){
            if(resultCode == RESULT_OK){
                printlog(TAG,"");
            }else if (resultCode == RESULT_CANCELED) {
                printlog(TAG,"");
            }
        }
    }


    private boolean isIgnoringBatteryOptimizations(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return pm.isIgnoringBatteryOptimizations(packageName);
        }
        return false;
    }

    private void gotoSettingIgnoringBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = getPackageName();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
