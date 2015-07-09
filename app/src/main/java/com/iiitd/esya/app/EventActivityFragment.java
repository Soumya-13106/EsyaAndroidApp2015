package com.iiitd.esya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */

/**
 * TODO:
 * The cards are hardcoded as of now, we have to use RecyclerView othervise this wont work. The toolbar will not collapse while using ListView.
 * For recycleView Reference:  http://enoent.fr/blog/2015/01/18/recyclerview-basics/
 */
public class EventActivityFragment extends Fragment {

    ArrayAdapter<String> categoryListAdapter;

    public EventActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        List<String> categoryListData = new ArrayList<>();
        categoryListData.add("Palsh");
        categoryListData.add("Palsh");
        categoryListData.add("Palsh");
        categoryListData.add("Palsh");
        categoryListData.add("Palsh");

        categoryListAdapter =
                new ArrayAdapter<>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_category, // The name of the layout ID.
                        R.id.list_item_category_textview, // The ID of the textview to populate.
                        categoryListData);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) view.findViewById(R.id.scrollableview);
        listView.setAdapter(categoryListAdapter);


        final TextView textView = (TextView)view.findViewById(R.id.event_text);

        int eventPk = this.getActivity().getIntent().getIntExtra("pk", -1);

        final Event old_event = DataHolder.EVENTS.get(eventPk);

        FetchSpecificEventTask fetchTaskDetails = new FetchSpecificEventTask() {
            @Override
            protected void onPostExecute(Event event) {
                if (event == null){
                    Toast.makeText(getActivity(), "Network error.", Toast.LENGTH_SHORT).show();
                    textView.setText(old_event.debuggableToString());
                    Log.e(LOG_TAG, "Could not fetch event data from server");
                    return;
                }
                old_event.copyFrom(event);
                textView.setText(old_event.debuggableToString());
                Log.v(LOG_TAG, "Fetched fresh data of event " +
                        event.id + ": " + event.debuggableToString());
            }
        };
        fetchTaskDetails.execute(eventPk);

        String details = old_event.debuggableToString();

        textView.setText(details);


        return view;
    }
}
