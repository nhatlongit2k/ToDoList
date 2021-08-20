package com.example.todolist;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    Database database;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "onReceive: ");
        Intent alarmMusicIntent = new Intent(context, AlarmMusic.class);

        Bundle args = intent.getBundleExtra("DATA");
        Job job = (Job) args.getSerializable("chatobj");

        //Job job = (Job) intent.getSerializableExtra("Job");
        Log.d("IDJob", "ID: "+job.getId());
        database = new Database(context, "testSql.sqlite", null,1);
        database.QueryData("DELETE FROM CongViec WHERE Id = "+job.getId());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.todoicon)
                .setContentText("Nội dung công việc: "+job.getJobDetail())
                .setContentTitle("Công việc phải làm: "+job.getJobName())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

        MainActivity.getData();
        context.startService(alarmMusicIntent);

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(MainActivity.ArrayPending.get(0));
//        MainActivity.ArrayPending.remove(index);
    }
}