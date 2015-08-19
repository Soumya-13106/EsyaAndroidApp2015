package com.iiitd.esya.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ListView listView = (ListView) view.findViewById(R.id.notifications_list);

        ArrayList<Notification> notifications;
        notifications = new ArrayList<>(Arrays.asList(Notification.getAllNotifications(getActivity())));
        listView.setAdapter(new NotificationAdapter(getActivity(), R.layout.notification_detailed_layout, notifications));

        return view;
    }
}

class NotificationAdapter extends ArrayAdapter<Notification>
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yy");

    public NotificationAdapter(Context context, int textViewResourceId){
        super(context, textViewResourceId);
    }

    public NotificationAdapter(Context context, int resource, List<Notification> notificationList)
    {
        super(context, resource, notificationList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.notification_detailed_layout, null);
        }
        Notification notification = getItem(position);

        if (notification != null)
        {
            ((TextView)v.findViewById(R.id.notification_title)).setText(notification.title);
            ((TextView)v.findViewById(R.id.notification_details)).setText(notification.message);
            ((TextView)v.findViewById(R.id.notification_date)).setText(
                    dateFormat.format(notification.received));
        }
        return v;
    }
}