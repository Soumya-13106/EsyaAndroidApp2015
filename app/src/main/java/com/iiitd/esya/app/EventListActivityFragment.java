package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventListActivityFragment extends Fragment {

    ArrayAdapter<String> mEventsAdapter;

    public EventListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        String categoryStr = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);

        String[] data = DataHolder.CATEGORY_TO_EVENTS.get(categoryStr);
        List<String> e = new ArrayList<>(Arrays.asList(data));
        mEventsAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_event,
                R.id.list_item_event_textview, e);
        ListView listview_events = (ListView)rootView.findViewById(R.id.listview_event);
        listview_events.setAdapter(mEventsAdapter);

        listview_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String event = mEventsAdapter.getItem(i);
                // Toast.makeText(getActivity(), event, Toast.LENGTH_SHORT).show();
                // Toast.makeText(getActivity(), DataHolder.EVENT_TO_DETAILS.get(event)[0], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EventActivity.class).
                        putExtra(Intent.EXTRA_TEXT, event).
                        setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
