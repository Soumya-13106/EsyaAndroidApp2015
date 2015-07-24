package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */

/**
 * The cards are hardcoded as of now, we have to use RecyclerView othervise this wont work. The toolbar will not collapse while using ListView.
 * For recycleView Reference:  http://enoent.fr/blog/2015/01/18/recyclerview-basics/
 */
public class EventActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);
        String auth_token = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(getString(R.string.api_auth_token), "nope");

        int eventPk = getActivity().getIntent().getIntExtra(Intent.EXTRA_UID, -1);
        final Event old_event = DataHolder.EVENTS.get(eventPk);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.scrollableview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);




        //TODO: You'll have to create new adapters and recyclerviews for every tab,
        // with each adapter holding the data only for 1 card with that 1 card holding
        // data only for that one specific info of the event
        mAdapter = new EventAdapterForRecylerView(old_event.image_url);
        mRecyclerView.setAdapter(mAdapter);


//        final TextView textView = (TextView)view.findViewById(R.id.event_text);

        FetchSpecificEventTask fetchTaskDetails = new FetchSpecificEventTask(auth_token) {
            @Override
            protected void onPostExecute(Event event) {
                if (event == null){
                    Toast.makeText(getActivity(), "Network error.", Toast.LENGTH_SHORT).show();
//                    textView.setText(old_event.debuggableToString());
                    Log.e(LOG_TAG, "Could not fetch event data from server");
                    return;
                }
                old_event.copyFrom(event);
//                textView.setText(Html.fromHtml(old_event.debuggableToString()));
                Log.v(LOG_TAG, "Fetched fresh data of event " +
                        event.id + ": " + event.debuggableToString());
            }
        };
        fetchTaskDetails.execute(eventPk);

        return view;
    }
}

class EventAdapterForRecylerView extends RecyclerView.Adapter<EventAdapterForRecylerView.ViewHolder>
{
    private String info;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public View mView;
        public ViewHolder(View v)
        {
            super(v);
            mView = v;
        }
    }

    public EventAdapterForRecylerView(String info)
    {
        this.info = info;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view

        //  //  //  //  //  //  //  //  //  //  //  //  //
        // TODO: PALASH!!!                              //
        // The layout below is the card layout.         //
        // Create a new card layout and replace this    //
        //  //  //  //  //  //  //  //  //  //  //  //  //
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_event, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        //  //  //  //  //  //  //  //  //  // //  //
        // TODO: PALASH!!!                         //
        // This is the generic text box in card    //
        // you finally make.Replace the id         //
        //  //  //  //  //  //  //  //  //  //  // //
        ((TextView)holder.mView.findViewById(R.id.list_item_event_textview)).setText(info);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}