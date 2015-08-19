package com.iiitd.esya.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by darkryder on 23/7/15.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String TAG = GCMListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Set temp = data.keySet();
        Log.d(TAG, temp.toString());

        String title = data.getString("title", "Update");
        String body = data.getString("body");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + title + " :: " + body);

        sendNotification(title, body);
    }

    private void sendNotification(String title, String message)
    {
        Log.d(TAG, "in sendNotification: " + title + " :: " + message);

        com.iiitd.esya.app.Notification.addNotification(new com.iiitd.esya.app.Notification(
                title, message, new Date()
        ), getApplicationContext());


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder notificationBuilder = new Notification.Builder(this).
                setSmallIcon(R.drawable.androidicon).
                setContentText(message).
                setContentTitle(title).
                setAutoCancel(true).
                setSound(defaultSoundUri).
                setContentIntent(pendingIntent);


        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            notification = new Notification.BigTextStyle(notificationBuilder).
                    bigText(message).build();
        }
        else
        {
            notification = notificationBuilder.build();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
    }
}
