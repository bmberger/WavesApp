/*
 * Project: Waves
 *
 * Purpose: Utilized when an alarm is signaled to go off to display the notification
 *
 * Reference(s): Briana Berger
 */

package com.example.waves_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

// Class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Alarm was triggered
        String taskDetail = intent.getStringExtra("taskDetail");
        String earlyReminder = intent.getStringExtra("earlyPoint");

        int id = taskDetail.hashCode();
        if (earlyReminder.equals("true")) {
            // If task had more than two dates from current date (when set), early reminder is also set for two days before to-do is due
            id = taskDetail.hashCode() + 1;
            createNotification(context, "Remember to do " + taskDetail + "! It is due in two days.", id,"Alert");
            Log.d("MyAlarm", "Early alarm just fired");
        } else {
            createNotification(context, "Your task " + taskDetail + " is due today!", id,"Alert");
            Log.d("MyAlarm", "Deadline alarm just fired");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification (Context context, String msg, int id, String msgAlert) {
        PendingIntent goToWhenOpenNotif = PendingIntent.getActivity(context,id,
                new Intent(context, HomeActivity.class),0);

        // Displays the notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.waves_notification)
                .setTicker(msgAlert)
                .setContentTitle("Waves: Task Reminder!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(goToWhenOpenNotif)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        // Channels between channel and manager to display notification to user
        NotificationChannel channel = new NotificationChannel("1", "Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager nNotifManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nNotifManager.createNotificationChannel(channel);

        nNotifManager.notify(1 ,mBuilder.build());
    }
}