package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


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
        int categoryCode = getActivity().getIntent().getIntExtra(Intent.EXTRA_UID, 0);

        Category category = Category.resolveToCategory(categoryCode);

        ArrayList<Event> category_events = DataHolder.CATEGORY_TO_EVENTS.get(category);
        ArrayList<String> event_names = new ArrayList<>();


        if (category_events.size() == 0)
        {
            rootView= inflater.inflate(R.layout.no_elements_layout, container, false);
            ((TextView)rootView.findViewById(R.id.message)).setText(R.string.no_elem_events);
            return rootView;
        }

        mEventsAdapter = new EventCardListAdapter(getActivity(), category_events);

        ListView listview_events = (ListView)rootView.findViewById(R.id.listview_event);
        listview_events.setAdapter(mEventsAdapter);

        listview_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = (Event)mEventsAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), EventActivity.class).
                        putExtra(Intent.EXTRA_UID, event.id).
                        setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
