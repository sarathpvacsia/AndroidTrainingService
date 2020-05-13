package com.example.boundserviceexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class RandomNumberGeneratorService extends Service {

    private final int MIN = 0;
    private final int MAX = 100;
    private int mRandomNumber;
    public static final String TAG = RandomNumberGeneratorService.class.getSimpleName();
    private boolean mRandomNumberGeneratorOn = false;

    public class MyServiceBinder extends Binder {

        public RandomNumberGeneratorService getService() {
            return RandomNumberGeneratorService.this;
        }
    }

    private IBinder mBinder = new MyServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRandomNumberGeneratorOn = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "onStartCommand: ThreadID "+Thread.currentThread().getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundRGS();
                } else {
                    startRandomNumberGenerator();
                }
            }
        }).start();
        return START_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startForegroundRGS() {
        String NOTIFICATION_CHANNEL_ID = "example.boundserviceexample";
        String channelName = "RandomNumberGeneratorService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
        startRandomNumberGenerator();
    }

    @Override
    public void onDestroy() {
        stopRandomNumberGenerator();
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void startRandomNumberGenerator() {
        while (mRandomNumberGeneratorOn) {
            try {
                Thread.sleep(1000);
                mRandomNumber = new Random().nextInt(MAX) + MIN;
                Log.i(TAG, "Thread id: " + Thread.currentThread().getId() + ", Random Number: " + mRandomNumber);

            } catch (InterruptedException e) {
                Log.i(TAG, "Thread Interrupted");
            }
        }
    }

    private void stopRandomNumberGenerator() {
        mRandomNumberGeneratorOn = false;
    }

    public int getmRandomNumber() {
        return mRandomNumber;
    }

}
