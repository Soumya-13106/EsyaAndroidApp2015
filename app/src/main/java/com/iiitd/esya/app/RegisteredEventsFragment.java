package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Soumya on 24-06-2015.
 */
public class RegisteredEventsFragment extends Fragment{

    public RegisteredEventsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registered_events, container, false);

        ArrayList<Event> events = new ArrayList<>();

        if (!DataHolder.EVENTS.isEmpty())
        {
            for(Event e: DataHolder.EVENTS.values())
            {
                if (e.registered) events.add(e);
            }
        }

        final HashMap<String, Event> map = new HashMap<>();

        ListView listView = (ListView) rootView.findViewById(R.id.events_list);

        final String[] names = new String[events.size()];
        for(int i = 0; i < events.size(); i++)
        {
            names[i] = events.get(i).name;
            map.put(events.get(i).name, events.get(i));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_registered_event, R.id.event_name, names);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = map.get(names[i]);

                Intent intent = new Intent(getActivity(), EventActivity.class).
                        putExtra(Intent.EXTRA_UID, event.id).
                        setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
