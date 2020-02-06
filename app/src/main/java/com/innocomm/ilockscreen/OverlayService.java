package com.innocomm.ilockscreen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import static com.innocomm.ilockscreen.LockScreenMgr.printlog;

public class OverlayService extends Service {

    private static final String TAG = OverlayService.class.getSimpleName();
    WindowManager mWindowManager;
    View mView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        printlog(TAG,"onCreate ");
        LockApplication.getInstance().setServiceState(true);

        //foreground Service
        channel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(false);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(getString(R.string.is_active))
                .build();
        startForeground(NOTIF_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        printlog(TAG,"onStartCommand ");
        registerOverlayReceiver();
        if(intent!=null){
            boolean shownow = intent.getBooleanExtra(LockScreenMgr.TAG_SHOW_NOW,false);
            if(shownow) showDialog();
        }
        return START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    private void showDialog() {
        printlog(TAG,"showDialog ");
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mView = View.inflate(getApplicationContext(), R.layout.fragment_overlay, null);
        mView.setTag(TAG);

        /*
        RelativeLayout dialog = (RelativeLayout) mView.findViewById(R.id.dialog);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();*/


        ImageButton imageButton = (ImageButton) mView.findViewById(R.id.close);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setVisibility(View.INVISIBLE);
            }
        });
        imageButton.setVisibility(View.GONE);

        // Attach listener
        UnlockBar unlock = (UnlockBar) mView.findViewById(R.id.unlock);
        unlock.setOnUnlockListenerRight(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock()
            {
                mView.setVisibility(View.INVISIBLE);
            }
        });


        unlock.setOnUnlockListenerLeft(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock()
            {
                mView.setVisibility(View.INVISIBLE);
            }
        });

        final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mView.setVisibility(View.VISIBLE);
        //mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in);
        // mView.startAnimation(mAnimation);
        mWindowManager.addView(mView, mLayoutParams);

    }

    private void hideDialog() {
        if (mView != null && mWindowManager != null) {
            printlog(TAG,"hideDialog ");
            mWindowManager.removeView(mView);
            mView = null;
        }
    }

    @Override
    public void onDestroy() {
        printlog(TAG,"onDestroy ");
        LockApplication.getInstance().setServiceState(false);

        unregisterOverlayReceiver();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        printlog(TAG,"onTaskRemoved ");
        super.onTaskRemoved(rootIntent);
        //LockApplication.getInstance().setServiceState(false);

    }

    boolean receiverRegistered = false;
    private void registerOverlayReceiver() {
        if(!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(overlayReceiver, filter);
        }
    }

    private void unregisterOverlayReceiver() {
        hideDialog();
        if(receiverRegistered) {
            receiverRegistered = false;
            unregisterReceiver(overlayReceiver);
        }
    }


    private BroadcastReceiver overlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            printlog(TAG, "[onReceive]" + action);
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                showDialog();
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {
                //hideDialog();
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                hideDialog();
            }
        }
    };

    public float convertDpToPixel(float dp){
        float px = dp * getDensity(this);
        return px;
    }

    public float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    //foreground Service
    private static final String CHANNEL_ID_STRING = "iLOCKSCREEN_CHANNEL_ID";
    private NotificationChannel channel = null;
    private NotificationManager notificationManager;
    private static final int NOTIF_ID=1;

    private void UpdateNotification(String text){

        Notification notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID_STRING)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification).build();

        notificationManager.notify(NOTIF_ID, notification);

    }

    //~foreground Service
}