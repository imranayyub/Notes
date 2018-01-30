package com.example.imran.notes.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.imran.notes.activities.HomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by imran on 29/1/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    //generates Android Notification In case any firebase notification being recieved.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        //gets Title and body of the notification.
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent;
        //pending Intent in case notification is being clicked.
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //NotificationCompat is helper for accessing feature in Notification.
        NotificationCompat.Builder notificationBUilder = new NotificationCompat.Builder(this);
        notificationBUilder.setContentTitle(title);
        notificationBUilder.setContentText(body);
        notificationBUilder.setAutoCancel(true);
        notificationBUilder.setContentIntent(pendingIntent);
        //Initializing Notification Manager(NotificationManager is a class that notify user that something has happened in background).
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //NotificationChannel is required for Android version from Oreo.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        //shows the notification in the notification bar.
        notificationManager.notify(0, notificationBUilder.build());
    }
}
