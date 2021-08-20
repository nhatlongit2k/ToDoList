package com.example.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmMusic extends Service {
    MediaPlayer mediaPlayer;
    //private MainActivity context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(this,R.raw.nhacbaothuc);
        mediaPlayer.start();

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                mediaPlayer.stop();

            }
        }.start();
//        try {
//            Thread.sleep(30000);
//            //mediaPlayer.stop();
////            stopSelf();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        stopSelf();
        return START_NOT_STICKY;
    }
}
