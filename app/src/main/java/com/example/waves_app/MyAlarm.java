package com.example.waves_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.waves_app.fragments.HomeFragment;

//class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {

    //the method will be fired when the alarm is triggerred
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        // you can check the log to see if it is fired - you can do any task here
        Log.d("MyAlarm", "Alarm just fired");

        String taskDetail = intent.getStringExtra("taskDetail");
        createNotification(context, taskDetail, "Alert");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification (Context context, String msg, String msgAlert) {
        PendingIntent goToWhenOpenNotif = PendingIntent.getActivity(context,0,
                new Intent(context, HomeActivity.class),0);

        // displays the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_new_home) //TODO: change to logo icon
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
