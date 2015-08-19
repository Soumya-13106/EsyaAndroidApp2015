package com.iiitd.esya.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

/**
 * Created by darkryder on 19/8/15.
 */
public class Notification {
    String title;
    String message;
    Date received;

    private static final String SEPARATOR = "_HOPEFULLY_NOONE_WILL_WRITE_THIS_161";

    public Notification(String title, String message, Date received)
    {
        this.title = title;
        this.message = message;
        this.received = received;
    }

    public static Notification[] getAllNotifications(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int count = preferences.getInt(context.getString(R.string.pref_count_notifications), 0);

        Notification[] notifications = new Notification[count];
        for(int i = 1; i <= count; i++)
        {
            String dumps = preferences.getString(context.getString(R.string.notification_save_prefix) + (i -1) , null);
            if (dumps == null)
            {
                notifications[count - i] = null;
                continue;
            }
            notifications[count - i] = _deserialize(dumps);
        }
        return notifications;
    }

    public static void addNotification(Notification notification, Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        int count = preferences.getInt(context.getString(R.string.pref_count_notifications), 0);

        editor.putString(context.getString(R.string.notification_save_prefix) + count, _serialize(notification));
        editor.putInt(context.getString(R.string.pref_count_notifications), count+1);
        editor.commit();
    }

    private static String _serialize(Notification notification)
    {
        if (notification == null) return "";
        return notification.title + SEPARATOR + notification.message +
                SEPARATOR + Event.parseDateToString(notification.received);
    }

    private static Notification _deserialize(String dumps)
    {
        String[] temp = dumps.split(SEPARATOR);
        if (temp.length != 3) return null;

        return new Notification(
                temp[0].trim(),
                temp[1].trim(),
                Event.parseStringToDate(temp[2].trim()));
    }

    @Override
    public String toString() {
        return this.title + ": " + this.message + " :: " + Event.parseDateToString(this.received);
    }
}
