package com.iiitd.esya.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class EventActivityFragment extends Fragment {

    public EventActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        TextView textView = (TextView)view.findViewById(R.id.event_text);

        String eventName = this.getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);

        String details = Arrays.deepToString(DataHolder.EVENT_TO_DETAILS.get(eventName));

        textView.setText(details);
        return  view;
    }
}
