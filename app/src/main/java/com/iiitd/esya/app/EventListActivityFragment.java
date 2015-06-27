package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventListActivityFragment extends Fragment {

    public EventListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        String categoryStr = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);

        String[] data = DataHolder.CATEGORY_TO_EVENTS.get(categoryStr);
        List<String> e = new ArrayList<>(Arrays.asList(data));
        ArrayAdapter events = new ArrayAdapter(getActivity(), R.layout.list_item_event,
                R.id.list_item_event_textview, e);
        ListView listview_events = (ListView)rootView.findViewById(R.id.listview_event);
        listview_events.setAdapter(events);

        return rootView;
    }
}
