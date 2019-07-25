package com.example.waves_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

// Class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {

    // The method will be fired when the alarm is triggerred
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        // You can check the log to see if it is fired - you can do any task here
        // Log.d("MyAlarm", "Alarm just fired");

        String taskDetail = intent.getStringExtra("taskDetail");
        createNotification(context, taskDetail, "Alert");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification (Context context, String msg, String msgAlert) {
        int id = msg.hashCode();
        PendingIntent goToWhenOpenNotif = PendingIntent.getActivity(context,id,
                new Intent(context, HomeActivity.class),0);

        // Displays the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.waves_notification)
                .setTicker(msgAlert)
                .setContentTitle("Waves: Task Reminder!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Your task " + msg + " is due today!")
                .setAutoCancel(true)
                .setContentIntent(goToWhenOpenNotif)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationChannel channel = new NotificationChannel("1", "Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager nNotifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nNotifManager.createNotificationChannel(channel);

        nNotifManager.notify(1 ,mBuilder.build());
    }
}