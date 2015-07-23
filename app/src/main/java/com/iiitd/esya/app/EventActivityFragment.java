package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
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
 * TODO: ALL THIS CLASS DESIGN IS LEFT.
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
        String auth_token = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(getString(R.string.api_auth_token), "nope");


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

        int eventPk = this.getActivity().getIntent().getIntExtra(Intent.EXTRA_UID, -1);

        final Event old_event = DataHolder.EVENTS.get(eventPk);

        FetchSpecificEventTask fetchTaskDetails = new FetchSpecificEventTask(auth_token) {
            @Override
            protected void onPostExecute(Event event) {
                if (event == null){
                    Toast.makeText(getActivity(), "Network error.", Toast.LENGTH_SHORT).show();
                    textView.setText(old_event.debuggableToString());
                    Log.e(LOG_TAG, "Could not fetch event data from server");
                    return;
                }
                old_event.copyFrom(event);
                textView.setText(Html.fromHtml(old_event.debuggableToString()));
                Log.v(LOG_TAG, "Fetched fresh data of event " +
                        event.id + ": " + event.debuggableToString());
            }
        };
        fetchTaskDetails.execute(eventPk);

        return view;
    }
}
