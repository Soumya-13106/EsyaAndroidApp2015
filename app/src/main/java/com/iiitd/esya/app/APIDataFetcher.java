package com.iiitd.esya.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by darkryder on 27/6/15.
 */
public class APIDataFetcher {
    private static final String API_URL = "http://esya.iiitd.edu.in/m/";

    private static String fetchAllEventsJsonNetworkWorker()
    {
        final String LOG_TAG = "FETCH_ALL_EVENTS_WRK";
        String eventsJsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try
        {
            final String resource = "events";
            final String format = "json";

            Uri callableUri = Uri.parse(API_URL).buildUpon().appendPath(resource + "." + format).build();

            URL url = new URL(callableUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            eventsJsonResponse = buffer.toString();

        } catch (IOException e){
            Log.e(LOG_TAG, e.toString());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not close stream" +
                            e.toString());
                }
            }
        }
        return eventsJsonResponse;
    }


    private static String fetchEventJsonNetworkWorker(int pk)
    {
        final String LOG_TAG = "FETCH_EVENT_WRK";
        String eventJsonResponse = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try
        {
            final String resource = "events";
            final String format = "json";

            Uri callableUri = Uri.parse(API_URL).buildUpon().
                    appendPath(resource).
                    appendPath(Integer.toString(pk) + "." + format).
                    build();

            URL url = new URL(callableUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            eventJsonResponse = buffer.toString();

        } catch (IOException e){
            Log.e(LOG_TAG, e.toString());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not close stream" +
                            e.toString());
                }
            }
        }
        return eventJsonResponse;
    }

    /**
     * @return array of events with basic data inbuilt.
     */
    public static Event[] fetchBasicAllEvents(){
        String jsonResponseString = fetchAllEventsJsonNetworkWorker();
        if (jsonResponseString == null) return null;

        ArrayList<Event> events = new ArrayList<>();

        try{
            JSONArray mainArray = new JSONArray(jsonResponseString);
            if (mainArray.length() == 0) return null;

            for(int i = 0; i < mainArray.length(); i++){
                JSONObject jsonEvent = mainArray.getJSONObject(i);
                String temp_image_url = jsonEvent.getJSONObject("photo").
                        getJSONObject("photo").getString("url");
                events.add(new Event(
                        jsonEvent.getInt("id"),
                        jsonEvent.getString("name"),
                        Category.resolveToCategory(jsonEvent.getString("category")),
                        temp_image_url.equals("null") ? null : temp_image_url
                ));
            }

            return events.toArray(new Event[events.size()]);

        } catch (JSONException e){
            Log.e("ParseException", e.toString());
            return null;
        }
    }

    public static Event fetchEventDetails(int pk){
        String jsonResponseString = fetchEventJsonNetworkWorker(pk);
        if (jsonResponseString == null) return null;

        try{
            Event event = null;
            JSONObject jsonEvent = new JSONObject(jsonResponseString);

            String temp_image_url = jsonEvent.getJSONObject("photo").
                                        getJSONObject("photo").getString("url");

            event = new Event(
                    jsonEvent.getInt("id"),
                    jsonEvent.getString("name"),
                    Category.resolveToCategory(jsonEvent.getString("category")),
                    temp_image_url.equals("null") ? null : temp_image_url
            );


            event.eligibility = jsonEvent.optString(DataHolder.ELIGIBILITY_RESPONSE, DataHolder.ELIGIBILITY_DEFAULT);
            event.judging = jsonEvent.optString(DataHolder.JUDGING_RESPONSE, DataHolder.JUDGING_DEFAULT);
            event.prizes = jsonEvent.optString(DataHolder.PRIZES_RESPONSE, DataHolder.PRIZES_DEFAULT);
            event.rules = jsonEvent.optString(DataHolder.RULES_RESPONSE, DataHolder.RULES_DEFAULT);
            event.venue = jsonEvent.optString(DataHolder.VENUE_RESPONSE, DataHolder.VENUE_DEFAULT);
            event.description = jsonEvent.optString(DataHolder.DESCRIPTION_RESPONSE, DataHolder.DESCRIPTION_DEFAULT);
            event.team_size = jsonEvent.optInt(DataHolder.TEAM_SIZE_RESPONSE, DataHolder.TEAM_SIZE_DEFAULT);

            if (event.eligibility.equals("")) event.eligibility = DataHolder.ELIGIBILITY_DEFAULT;
            if (event.judging.equals("")) event.judging = DataHolder.JUDGING_DEFAULT;
            if (event.prizes.equals("")) event.prizes = DataHolder.PRIZES_DEFAULT;
            if (event.rules.equals("")) event.rules = DataHolder.RULES_DEFAULT;
            if (event.venue.equals("")) event.venue = DataHolder.VENUE_DEFAULT;

            // TODO: get this fixed in the API. It's not being sent as of now.
//            if ((event.description != null) && event.description.equals("")) event.description = DataHolder.DESCRIPTION_DEFAULT;

            String temp_contact = jsonEvent.optString(DataHolder.CONTACT_RESPONSE, DataHolder.CONTACT_DEFAULT);

            ArrayList<String> temp_contact_list = new ArrayList<>();
            for(String c: temp_contact.split("<br>")){
                temp_contact_list.add(c);
            }
            event.contact = temp_contact_list.toArray(new String[temp_contact_list.size()]);

            return event;

        } catch (JSONException e){
            Log.e("ParseExeption", e.toString());
            return null;
        }
    }

    public static Bitmap getImageFromURL(String url)
    {
        Log.v("getImageFromURL", "Started image download from " + url);
        String LOG_TAG = "FETCH_IMAGE";
        try
        {
            InputStream in = new URL(url).openStream();
            return BitmapFactory.decodeStream(in);
        } catch (IOException e)
        {
            Log.e(LOG_TAG, e.toString());
        }
        return null;
    }
}

/**
 * This is an abstract class so that you have
 * to override the onPostExecuteMethod as and when
 * needed.
 */
abstract class FetchAllEventsTask extends AsyncTask<Void, Void, Event[]>
{
    final String LOG_TAG = "FETCH_ALL_EVENTS";
    @Override
    protected Event[] doInBackground(Void... voids) {
        Log.v(LOG_TAG, "Fetching events task started");
        return APIDataFetcher.fetchBasicAllEvents();
    }
}

/**
 * This is an abstract class so that you have
 * to override the onPostExecuteMethod as and when
 * needed by creating an anonymous class.
 */
abstract class FetchSpecificEventTask extends AsyncTask<Integer, Void, Event>
{
    final String LOG_TAG = "FETCH_EVENT";
    @Override
    protected Event doInBackground(Integer... integers) {
        if (integers.length != 1) return null;
        int pk = integers[0];
        Log.v(LOG_TAG, "Fetching event " + pk + " task started.");
        return APIDataFetcher.fetchEventDetails(pk);
    }
}

abstract class FetchImagesTask extends AsyncTask<String[], Void, Bitmap[]>
{
    protected Context context;
    public FetchImagesTask(Context context)
    {
        this.context = context;
    }

    private String LOG_TAG = FetchImagesTask.class.getSimpleName();
    @Override
    protected Bitmap[] doInBackground(String[]... strings_temp) {
        String[] strings = strings_temp[0];
        Log.v(LOG_TAG, "Starting FetchImagesTask with args: " + Arrays.deepToString(strings));

        if(strings.length == 0) return null;
        Bitmap[] result = new Bitmap[strings.length];

        for(int i = 0; i < strings.length; i++)
        {
            Bitmap image;
            String url = strings[i];
            boolean isEventPhoto = false;
            Event event = null;

            if ((url == null) || url.isEmpty()) continue;

            // gotta come up with something better
            if (url.contains("/uploads/event/photo/")) isEventPhoto = true;

            //  check if it's an event. whether I know how to cache the image_url
            if (isEventPhoto)
            {
                // extract event id
                // http://esya.iiitd.edu.in/uploads/event/photo/10/Hackon.png
                // 0    1 2                  3        4    5    6   7
                String[] url_parts = url.split("/");
                int id = Integer.parseInt(url_parts[6]);
                event = DataHolder.EVENTS.get(id);
                String name = Event.getImageNameFromUrl(url);
                if (event.isImageInCache(name, context))
                {
                    image = event.getCacheImage(name, context);
                    Log.v(LOG_TAG, "found image " + name + " in cache");
                }
                else{
                    image = APIDataFetcher.getImageFromURL(url);
                    if (image != null) event.setCacheImage(image, name, context);
                    Log.v(LOG_TAG, "fetched image for: " + event.name);
                }
            }
            else
            {
                image = APIDataFetcher.getImageFromURL(url);
            }
            Log.v(LOG_TAG, "fetched image from: " + url);
            result[i] = image;
        }
        return result;
   }
}