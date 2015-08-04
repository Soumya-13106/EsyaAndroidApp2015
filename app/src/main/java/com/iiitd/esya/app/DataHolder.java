package com.iiitd.esya.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by darkryder on 27/6/15.
 * As of now, this is a temporary class to hold the category/event/details data
 */
public class DataHolder {
    private static final String LOG_TAG = DataHolder.class.getSimpleName();
    public static final boolean COMPRESS_IMAGES_BEFORE_SAVING = true;

    public static final String ELIGIBILITY_RESPONSE = "eligibilty";
    public static final String JUDGING_RESPONSE =  "judging";
    public static final String PRIZES_RESPONSE =  "prizes";
    public static final String RULES_RESPONSE =  "rules";
    public static final String VENUE_RESPONSE =  "venue";
    public static final String DESCRIPTION_RESPONSE =  "description";
    public static final String TEAM_SIZE_RESPONSE =  "team_size";
    public static final String CONTACT_RESPONSE =  "contact";
    public static final String REGISTERED_RESPONSE = "registered";
    public static final String TEAM_EVENT_RESPONSE = "team_event";
    public static final String TEAM_ID_RESPONSE = "team_id";
    public static final String UPDATED_AT_RESPONSE = "updated_at";
    public static final String EVENT_DATE_TIME_RESPONSE = "event_date_time";
    public static final String PROFILE_LOGIN_RESPONSE = "login";
    public static final String PROFILE_NAME_RESPONSE = "name";
    public static final String PROFILE_PHONE_RESPONSE = "phone";
    public static final String PROFILE_COLLEGE_RESPONSE = "college";
    public static final String PROFILE_COMPLETE_RESPONSE = "complete";

    public static final String ELIGIBILITY_DEFAULT = "No specific eligibility criteria.";
    public static final String JUDGING_DEFAULT =  "Judging criteria has not been decided yet. Stay tuned!";
    public static final String PRIZES_DEFAULT =  "Prizes have not been announced yet. Stay tuned, though!";
    public static final String RULES_DEFAULT =  "Rules have not been decided yet.";
    public static final String VENUE_DEFAULT =  "IIIT - D";
    public static final String DESCRIPTION_DEFAULT =  "Description of the event";
    public static final int TEAM_SIZE_DEFAULT =  0;
    public static final String CONTACT_DEFAULT =  "events.esya@iiitd.ac.in";
    public static final int REGISTERED_DEFAULT = 0;
    public static final boolean TEAM_EVENT_DEFAULT = false;
    public static final int TEAM_ID_DEFAULT = 0;
    public static final Date UPDATED_AT_DEFAULT = new Date();
    public static final Date EVENT_DATE_TIME_DEFAULT = new Date();

    public static final HashMap<Category, ArrayList<Event>> CATEGORY_TO_EVENTS = new HashMap<>();

    public static boolean initialised = false;

    public static void init(Context context)
    {
        if (initialised) return;

        for(Category c: Category.values()){
            CATEGORY_TO_EVENTS.put(c, new ArrayList<Event>());
        }

        InitialDataFetcher task = new InitialDataFetcher(context);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static HashMap<Integer, Event> EVENTS = new HashMap<>();
}

class InitialDataFetcher extends FetchAllEventsTask
{
    static boolean first_time_app_launch_ever = false;
    Context context;
    public InitialDataFetcher(Context context)
    {
        super(context);
        this.context = context;
        this.db = new DBHelper(context);
    }

    public static final String ERROR_TOAST = "No network connection";
    public static final String LOG_TAG = InitialDataFetcher.class.getSimpleName();
    private DBHelper db;
    private HashMap<Integer, Event> network_event_ids = new HashMap<>();
    private HashMap<Integer, Event> database_event_ids = new HashMap<>();

    @Override
    protected void onPreExecute() {
        if (DataHolder.initialised == true) return;
        super.onPreExecute();
    }

    @Override
    protected Event[] doInBackground(Void... voids) {
        for(Event e: db.getAllEvents()) database_event_ids.put(e.id, e);
        return super.doInBackground(voids);
    }

    @Override
    protected void onPostExecute(Event[] events) {
        if (events == null){
            Log.e(LOG_TAG, "Could not fetch initial Data. No connection");
            Toast.makeText(context, "Could not refresh data. No connection", Toast.LENGTH_SHORT).show();
        } else{
            Log.v(LOG_TAG, "Fetched all events: " + Arrays.deepToString(events));
        }



        if (events != null)
        {
            for(Event e: events) network_event_ids.put(e.id, e);
        }
        ArrayList<Integer> eventIdsToUpdate = new ArrayList<>();

        HashMap<Integer, Event> database_event_ids_copy = (HashMap<Integer, Event>)database_event_ids.clone();
        HashMap<Integer, Event> network_event_ids_copy = (HashMap<Integer, Event>)network_event_ids.clone();

        if (events != null)
        {
            for (int i : network_event_ids.keySet()) {
                Event dbEvent = database_event_ids.get(i);
                Event netEvent = network_event_ids.get(i);
                if (!database_event_ids.containsKey(i)) {
                    // inserts new events in the db

                    // make the date of the event updated_at to be equal to just epoch time.
                    // this is because of the following situation.
                    // suppose the initial data of all the events are fetched.
                    // Basic info of this event is added to the database, including the
                    // last updated_at timestamp as the correct time stamp.
                    // however if the proceeding async task to fetch rest of the complete
                    // details of the event gets terminated, the app will not fetch the new
                    // details from the server on further app restarts, because according to
                    // the database, the last updated times match.
                    // the updated_at time will automatically be fixed when the complete data
                    // is fetched.
                    netEvent.updated_at.setTime(10);

                    db.insertEvent(netEvent);
                    database_event_ids.put(netEvent.id, netEvent);
                    eventIdsToUpdate.add(netEvent.id);
                    continue;
                }
                if (Event.isDBStale(netEvent, dbEvent)) {
                    eventIdsToUpdate.add(netEvent.id);
                }
            }

            for (int i: database_event_ids_copy.keySet())
            {
                if (!network_event_ids_copy.containsKey(i))
                {
                    Log.v(LOG_TAG, "Attempting to delete event with id:" + i);
                    if(db.deleteEvent(i))
                    {
                        database_event_ids.remove(i);
                        Log.v(LOG_TAG, "Delete successful");
                    }
                    else
                    {
                        Log.d(LOG_TAG, "Could not delete event with id:" + i);
                    }
                }
            }
        }

        // The network isn't working.
        // set the database events to the events to show
        else {
            Log.e(LOG_TAG, "Network error. Reverting to database values");
            events = database_event_ids.values().toArray(new Event[database_event_ids.values().size()]);
        }

        if (!database_event_ids.isEmpty())
        {

            for(Event ev: database_event_ids.values()){
                for(Category category: ev.categories)
                {
                    DataHolder.CATEGORY_TO_EVENTS.get(category).add(ev);
                }
                DataHolder.CATEGORY_TO_EVENTS.get(Category.ALL).add(ev);
                DataHolder.EVENTS.put(ev.id, ev);
            }
        }
        else
        {
            first_time_app_launch_ever = true;
        }

        final InitialImagesFetcher imagesFetcherTask = new InitialImagesFetcher(context);
        final String[] event_image_urls = new String[events.length];
        for(int i = 0; i < events.length; i++)
        {
            event_image_urls[i] = events[i].image_url;
        }


        FetchSpecificEventTask fetchSpecificEventTask = new FetchSpecificEventTask(api_token) {

            @Override
            protected void onPostExecute(Event[] events) {
                super.onPostExecute(events);
                if (events == null || events.length == 0) return;
                for(Event event: events)
                {
                    if (!Event.updateEventInDB(DataHolder.EVENTS.get(event.id), event, context))
                    {
                        Log.d(LOG_TAG, "Unable to update event to db:" + event.toString());
                    } else Log.v(LOG_TAG, "Updated event in db: " + event.toString());
                }
                if(first_time_app_launch_ever)
                {
                    imagesFetcherTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event_image_urls);
                }
            }
        };
        fetchSpecificEventTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                eventIdsToUpdate.toArray(new Integer[eventIdsToUpdate.size()]));

        if (!first_time_app_launch_ever)
        {
            imagesFetcherTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event_image_urls);
        }


        DataHolder.initialised = true;
    }
}

class InitialImagesFetcher extends FetchImagesTask
{
    public InitialImagesFetcher(Context context)
    {
        super(context);
    }

    public static final String LOG_TAG =InitialImagesFetcher.class.getSimpleName();

    @Override
    protected void onPostExecute(Bitmap[] bitmaps) {
        Log.v(LOG_TAG, "FETCHED ALL IMAGES");
    }
}