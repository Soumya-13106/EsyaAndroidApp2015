package com.iiitd.esya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventActivityFragment extends Fragment {

    public EventActivityFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
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
