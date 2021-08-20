package com.example.todolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ToDoListService extends Service {
    Calendar calendar;
    public ArrayList<Job> arrayJob;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Database database;
    Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String input = intent.getStringExtra("inputExtra");
        database = new Database(this, "testSql.sqlite", null,1);
        Bundle bundleObject = intent.getExtras();
        arrayJob = (ArrayList<Job>) bundleObject.getSerializable("jobList");
//        Log.d("TAG", "onStartCommand: "+arrayJob.get(0).getDateTime());
        if(arrayJob.isEmpty())
            return START_NOT_STICKY;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(arrayJob.get(0).getDateTime());
//        Log.d("TAG", "onStartCommand: "+strDate);

        int year = Integer.parseInt(strDate.substring(0,4));
        int month = Integer.parseInt(strDate.substring(5,7));
        int date = Integer.parseInt(strDate.substring(8,10));
        int hour = Integer.parseInt(strDate.substring(11,13));
        int minute = Integer.parseInt(strDate.substring(14,16));
        int second = Integer.parseInt(strDate.substring(17,19));

        Log.d("TAG", "onStartCommand: "+year+"/"+month+"/"+date+":"+hour+"."+minute+"."+second);
        calendar = Calendar.getInstance();
        calendar.set(year,month-1,date,hour,minute,second);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent1Alarm = new Intent(this, AlarmReceiver.class);

//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
//                .setContentTitle("Test")
//                .setContentText("Test")
//                .setSmallIcon(R.drawable.todoicon)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1, notification);



//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        thread.start();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(arrayJob.isEmpty()==false) {
                    long countTime = (calendar.getTimeInMillis()-Calendar.getInstance().getTimeInMillis())/(1000*60);
                    try {
                        Thread.sleep(1000*60);
                        Log.d("Time: ", "Thời gian còn lại: " + countTime);
                        if(countTime <= 5){
                            Log.d("Message", "Thông báo ");
                            Intent notificationIntent = new Intent(ToDoListService.this, MainActivity.class);
                            PendingIntent pendingIntentNoti = PendingIntent.getActivity(ToDoListService.this,
                                    0, notificationIntent, 0);

                            Notification notification = new NotificationCompat.Builder(ToDoListService.this, App.CHANNEL_ID)
                                    .setContentTitle("Sắp có công việc phải làm")
                                    .setContentText(arrayJob.get(0).getJobName())
                                    .setSmallIcon(R.drawable.todoicon)
                                    .setContentIntent(pendingIntentNoti)
                                    .build();
//                            startForeground(1, notification);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, notification);

                            intent1Alarm.putExtra("idJob", String.valueOf(arrayJob.get(0).getId()));
                            pendingIntent = PendingIntent.getBroadcast(
                                    ToDoListService.this, 0,intent1Alarm,pendingIntent.FLAG_UPDATE_CURRENT
                            );
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
                            arrayJob.remove(0);
                        }
                        if(countTime == 0){
//                            database.QueryData("DELETE FROM CongViec WHERE Id = "+arrayJob.get(0).getId());
//                            arrayJob.remove(0);
//                            new MainActivity().getData();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        super.onDestroy();
        Log.d("Destroy", "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
