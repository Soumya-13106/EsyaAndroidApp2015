package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventListActivityFragment extends Fragment {

    EventCardListAdapter mEventsAdapter;

    public EventListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        String categoryStr = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);

        Category category = Category.resolveToCategory(categoryStr);

        ArrayList<Event> category_events = DataHolder.CATEGORY_TO_EVENTS.get(category);
        ArrayList<String> event_names = new ArrayList<>();
        final HashMap<String, Integer> temp_name_to_event_map = new HashMap<>();
        for(Event ev: category_events){
            event_names.add(ev.name);
            temp_name_to_event_map.put(ev.name, ev.id);
        }

        mEventsAdapter = new EventCardListAdapter(getActivity(), category_events);

        ListView listview_events = (ListView)rootView.findViewById(R.id.listview_event);
        listview_events.setAdapter(mEventsAdapter);

        listview_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = (Event)mEventsAdapter.getItem(i);
                // Toast.makeText(getActivity(), event, Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(), DataHolder.EVENT_TO_DETAILS.get(event)[0], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EventActivity.class).
                        putExtra("pk", event.id).
                        setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
