package com.iiitd.esya.app;

import android.net.Uri;
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

/**
 * Created by darkryder on 27/6/15.
 */
public class APIDataFetcher {
    private static final String API_URL = "http://esya.iiitd.edu.in/";

    private static String fetchAllEventsJsonNetworkWorker()
    {
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
            Log.e("Error", e.toString());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("fetchAllEventsJsNetWork", "Could not close stream" +
                            e.toString());
                }
            }
        }
        return eventsJsonResponse;
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
            for(int i = 0; i < mainArray.length(); i++){
                JSONObject jsonEvent = mainArray.getJSONObject(i);
                events.add(new Event(
                        jsonEvent.getInt("id"),
                        jsonEvent.getString("name"),
                        Category.resolveToCategory(jsonEvent.getString("category")),
                        jsonEvent.getJSONObject("photo").
                                getJSONObject("photo").
                                getString("url")
                ));
            }

            return events.toArray(new Event[events.size()]);

        } catch (JSONException e){
            Log.e("ParseException", e.toString());
            return null;
        }
    }
}
