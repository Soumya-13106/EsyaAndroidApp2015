package com.iiitd.esya.app;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by darkryder on 27/6/15.
 */
public class APIDataFetcher {
    private static final String API_URL = "http://esya.iiitd.edu.in/m/";

    public static String getSimpleSignedResponse(String url_, String cookie_data) throws IOException
    {
        URL url = new URL(url_);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Cookie", "_esya2015_backend_session=" + cookie_data);
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) throw new IOException("InputStream was null");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }
        if (buffer.length() == 0) return null;

        return buffer.toString();
    }

    public static String getSimpleGetResponse(String url_) throws IOException
    {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(url_);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) throw new IOException("InputStream was null");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            return buffer.toString();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    private static String fetchAllEventsJsonNetworkWorker(String api_token)
    {
        final String LOG_TAG = "FETCH_ALL_EVENTS_WRK";
        String eventsJsonResponse = null;

        try
        {
            final String resource = "events";
            final String format = "json";

            Uri callableUri = Uri.parse(API_URL).buildUpon().appendPath(resource + "." + format).build();
            eventsJsonResponse = getSimpleSignedResponse(callableUri.toString(), api_token);

        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return eventsJsonResponse;
    }


    private static String fetchEventJsonNetworkWorker(int pk, String api_token)
    {
        final String LOG_TAG = "FETCH_EVENT_WRK";
        String eventJsonResponse = null;

        try
        {
            final String resource = "events";
            final String format = "json";

            Uri callableUri = Uri.parse(API_URL).buildUpon().
                    appendPath(resource).
                    appendPath(Integer.toString(pk) + "." + format).
                    build();
            eventJsonResponse = getSimpleSignedResponse(callableUri.toString(), api_token);

        } catch (IOException e){
            Log.e(LOG_TAG, e.toString());
        }
        return eventJsonResponse;
    }

    /**
     * @return array of events with basic data inbuilt.
     */
    public static Event[] fetchBasicAllEvents(String api_token){
        String jsonResponseString = fetchAllEventsJsonNetworkWorker(api_token);
        if (jsonResponseString == null) return null;

        ArrayList<Event> events = new ArrayList<>();

        try{
            JSONArray mainArray = new JSONArray(jsonResponseString);
            if (mainArray.length() == 0) return null;

            for(int i = 0; i < mainArray.length(); i++){
                JSONObject jsonEvent = mainArray.getJSONObject(i);
                String temp_image_url = jsonEvent.getJSONObject("photo").
                        getJSONObject("photo").getString("url");
                ArrayList<Category> categories = new ArrayList<>();
                JSONArray json_categories = jsonEvent.getJSONArray("categories");
                for(int j = 0; j < json_categories.length(); j++)
                {
                    categories.add(Category.resolveToCategory(((JSONObject)json_categories.get(j)).getInt("id")));
                }
                Event event = new Event(
                        jsonEvent.getInt("id"),
                        jsonEvent.getString("name"),
                        categories.toArray(new Category[categories.size()]),
                        temp_image_url.equals("null") ? null : temp_image_url,
                        jsonEvent.getString(DataHolder.UPDATED_AT_RESPONSE)
                );
                event.registered = jsonEvent.optInt(DataHolder.REGISTERED_RESPONSE, DataHolder.REGISTERED_DEFAULT)!= 0;
                event.team_id = jsonEvent.optString(DataHolder.TEAM_ID_RESPONSE, DataHolder.TEAM_ID_DEFAULT);
                events.add(event);
            }

            return events.toArray(new Event[events.size()]);

        } catch (JSONException e){
            Log.e("ParseException", e.toString());
            return null;
        }
    }

    public static Event fetchEventDetails(int pk, String api_token){
        String jsonResponseString = fetchEventJsonNetworkWorker(pk, api_token);
        if (jsonResponseString == null) return null;

        try{
            Event event = null;
            JSONObject jsonEvent = new JSONObject(jsonResponseString);

            String temp_image_url = jsonEvent.getJSONObject("photo").
                                        getJSONObject("photo").getString("url");

            ArrayList<Category> categories = new ArrayList<>();
            JSONArray json_categories = jsonEvent.getJSONArray("categories");
            for(int j = 0; j < json_categories.length(); j++)
            {
                categories.add(Category.resolveToCategory(((JSONObject)json_categories.get(j)).getInt("id")));
            }
            event = new Event(
                    jsonEvent.getInt("id"),
                    jsonEvent.getString("name"),
                    categories.toArray(new Category[categories.size()]),
                    temp_image_url.equals("null") ? null : temp_image_url,
                    jsonEvent.getString(DataHolder.UPDATED_AT_RESPONSE)
            );


            event.eligibility = jsonEvent.optString(DataHolder.ELIGIBILITY_RESPONSE, DataHolder.ELIGIBILITY_DEFAULT);
            event.judging = jsonEvent.optString(DataHolder.JUDGING_RESPONSE, DataHolder.JUDGING_DEFAULT);
            event.prizes = jsonEvent.optString(DataHolder.PRIZES_RESPONSE, DataHolder.PRIZES_DEFAULT);
            event.rules = jsonEvent.optString(DataHolder.RULES_RESPONSE, DataHolder.RULES_DEFAULT);
            event.venue = jsonEvent.optString(DataHolder.VENUE_RESPONSE, DataHolder.VENUE_DEFAULT);
            event.description = jsonEvent.optString(DataHolder.DESCRIPTION_RESPONSE, DataHolder.DESCRIPTION_DEFAULT);
            event.team_size = jsonEvent.optInt(DataHolder.TEAM_SIZE_RESPONSE, DataHolder.TEAM_SIZE_DEFAULT);
            event.contact = jsonEvent.optString(DataHolder.CONTACT_RESPONSE, DataHolder.CONTACT_DEFAULT);
            String temporary_event_date_time = jsonEvent.optString(DataHolder.EVENT_DATE_TIME_RESPONSE, "null");

            if(temporary_event_date_time == "null"){
                event.event_date_time = new Date();
            } else {
                event.event_date_time = Event.parseStringToDate(temporary_event_date_time);
            }

            if (event.eligibility.equals("")) event.eligibility = DataHolder.ELIGIBILITY_DEFAULT;
            if (event.judging.equals("")) event.judging = DataHolder.JUDGING_DEFAULT;
            if (event.prizes.equals("")) event.prizes = DataHolder.PRIZES_DEFAULT;
            if (event.rules.equals("")) event.rules = DataHolder.RULES_DEFAULT;
            if (event.venue.equals("")) event.venue = DataHolder.VENUE_DEFAULT;
            if (event.contact.equals("")) event.contact = DataHolder.CONTACT_DEFAULT;


            if ((event.description == null) ||
                    (event.description != null && event.description.equals("")))
                event.description = DataHolder.DESCRIPTION_DEFAULT;

            event.registered = jsonEvent.optInt(DataHolder.REGISTERED_RESPONSE, DataHolder.REGISTERED_DEFAULT)!= 0;
            event.team_event = jsonEvent.optBoolean(DataHolder.TEAM_EVENT_RESPONSE, DataHolder.TEAM_EVENT_DEFAULT);
            event.team_id = jsonEvent.optString(DataHolder.TEAM_ID_RESPONSE, DataHolder.TEAM_ID_DEFAULT);

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

    public static String get_last_modified_header(String url_)
    {
        try
        {
            URL url = new URL(url_);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("HEAD");
            return urlConnection.getHeaderField("Last-Modified");
        } catch (IOException e)
        {
            Log.d("LMHeader", url_ + e.toString());
            return null;
        }
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
    protected String api_token;

    public FetchAllEventsTask(String api_token)
    {
        this.api_token = api_token;
    }

    public FetchAllEventsTask(Context context){
        this(PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.api_auth_token), "nope"
        ));
    }

    @Override
    protected Event[] doInBackground(Void... voids) {
        Log.v(LOG_TAG, "Fetching events task started");
        return APIDataFetcher.fetchBasicAllEvents(this.api_token);
    }
}

/**
 * This is an abstract class so that you have
 * to override the onPostExecuteMethod as and when
 * needed by creating an anonymous class.
 */
abstract class FetchSpecificEventTask extends AsyncTask<Integer[], Void, Event[]>
{
    public FetchSpecificEventTask(String api_token)
    {
        this.api_token = api_token;
    }
    protected  String api_token;
    final String LOG_TAG = "FETCH_EVENT";

    @Override
    protected Event[] doInBackground(Integer[]... integers) {
        if (integers.length != 1) return null;
        Integer[] pks = integers[0];
        ArrayList<Event> events = new ArrayList<>();
        for(int pk: pks)
        {
            Log.v(LOG_TAG, "Fetching event " + pk + " task started.");
            events.add(APIDataFetcher.fetchEventDetails(pk, api_token));
        }
        return events.toArray(new Event[events.size()]);
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
                    Log.v(LOG_TAG, "fetched image from: " + url);
                }
            }
            else
            {
                image = APIDataFetcher.getImageFromURL(url);
            }
            Log.v(LOG_TAG, "fetched image for: " + event.name);
            result[i] = image;
        }
        return result;
   }
}

abstract class GetAndSendIdTokenTask extends AsyncTask<Void, Void, Void> {
    private Activity activity;
    private String API_URL;
    private SharedPreferences sharedPref;
    private GoogleApiClient mGoogleApiClient;
    private int AUTH_CODE_REQUEST_CODE = 23619;
    private static String TAG = GetAndSendIdTokenTask.class.getSimpleName();

    public GetAndSendIdTokenTask(Activity activity, GoogleApiClient googleApiClient)
    {
        mGoogleApiClient = googleApiClient;
        this.activity = activity;
        API_URL = activity.getString(R.string.URL_register_user_id);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        sharedPref.edit().putString("email", accountName).commit();
        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String scopes = "oauth2:" + Scopes.PLUS_ME + " " + Scopes.PLUS_LOGIN + " " + Scopes.PROFILE + " https://www.googleapis.com/auth/plus.profile.emails.read";
        String idToken;

        try {
            if (DataHolder.ONE_TIME_AUTH_TOKEN != null)
            {
                idToken = DataHolder.ONE_TIME_AUTH_TOKEN;
            }
            else
            {
                idToken =  GoogleAuthUtil.getToken(activity, account, scopes);
                DataHolder.ONE_TIME_AUTH_TOKEN = idToken;
            }
            sharedPref.edit().putString(
                    activity.getString(R.string.login_user_id), idToken).apply();
            DataHolder.EVENT_UPDATED_AFTER_LOGIN = 0;

        } catch(UserRecoverableAuthException e){
            activity.startActivityForResult(e.getIntent(), AUTH_CODE_REQUEST_CODE);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving ID token.", e);
            return null;
        } catch (GoogleAuthException e) {
            Log.e(TAG, "Error retrieving ID token.", e);
            return null;
        }
        String auth_token = "";
        try
        {
            Log.v(TAG, "Sending accesstoken to server: " + idToken);

            URL url = new URL(API_URL + "?token=" + idToken);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) throw new IOException("InputStream was null");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            JSONObject jsonResponse = new JSONObject(buffer.toString());

            int success =jsonResponse.getInt(activity.getString(R.string.api_response_key_success));
            if (success != 200) throw new JSONException("LoginFailed" + jsonResponse);

            String session_cookie_temp = urlConnection.getHeaderField("Set-Cookie");
            if (session_cookie_temp == null) throw new JSONException("Set-Cookie header not found");

            session_cookie_temp = session_cookie_temp.split("; ")[0];
            auth_token = session_cookie_temp.split("=")[1];

            sharedPref.edit().putString(activity.getString(R.string.api_auth_token), auth_token).commit();

            if (auth_token == null || auth_token.isEmpty() || auth_token.equals("null"))
            {
                throw new JSONException("EmptyResponse" + jsonResponse);
            }

            sharedPref.edit().putString(activity.getString(R.string.api_auth_token), auth_token).commit();
            Log.e("SendToken", "Received api auth: " + auth_token + " for loginToken " + idToken);
        } catch (IOException e){
            Log.e("SendToken", e.toString());
        } catch (JSONException e){
            Log.e("SendToken ParsingError", e.toString());
        }
        return null;
    }
}

class LoginPingTest extends AsyncTask<Void, Void, Boolean>
{
    private String API_URL = null;

    private Context context;

    public LoginPingTest(Context context)
    {
        this.context = context;
        API_URL = context.getString(R.string.URL_api_base) + "m/profile.json";
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String resp = null;
        try {
//            Log.v("PingTest", "starting");
            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            String token = PreferenceManager.getDefaultSharedPreferences(context).getString(
                                context.getString(R.string.api_auth_token), "Nope");
            urlConnection.setRequestProperty("Cookie", "_esya2015_backend_session=" + token);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) throw new IOException("InputStream was null");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) return null;

            resp = buffer.toString();
            Log.v("PingTest", resp);

            JSONObject jsonResponse = new JSONObject(resp);

            boolean check = jsonResponse.getBoolean(DataHolder.PROFILE_LOGIN_RESPONSE);

            if (!check) return false;

            boolean completed = jsonResponse.getBoolean(DataHolder.PROFILE_COMPLETE_RESPONSE);

            String name = jsonResponse.getString(DataHolder.PROFILE_NAME_RESPONSE);
            if (name == null || name.equals("null")) name = "";
            String college = jsonResponse.getString(DataHolder.PROFILE_COLLEGE_RESPONSE);
            if (college == null || college.equals("null")) college = "";
            String phone = jsonResponse.getString(DataHolder.PROFILE_PHONE_RESPONSE);
            if (phone == null || phone.equals("null")) phone = "";

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                    context).edit();
            editor.putString(context.getString(R.string.profile_name), name);
            editor.putString(context.getString(R.string.profile_college), college);
            editor.putString(context.getString(R.string.profile_phone), phone);
            editor.putBoolean(context.getString(R.string.pref_profile_complete), completed);
            editor.commit();

            return true;
        } catch (IOException e)
        {
            if (resp != null) Log.d("PingTest", resp.toString());
            Log.d("PingTest", "NetworkError");
        } catch (JSONException e)
        {
            Log.d("PingTest JSON", e.toString());
        }
        return false;
    }
}

abstract class CheckRegistrationForEvent extends AsyncTask<Void, Void, Boolean>
{
    private String TAG = CheckRegistrationForEvent.class.getSimpleName();
    protected Context context;
    protected Event event;
    public CheckRegistrationForEvent(Context c, Event event)
    {
        this.context = c;
        this.event = event;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String url =  context.getString(R.string.URL_api_base) + "m/check_registration/" + event.id;
            String token = PreferenceManager.getDefaultSharedPreferences(context).getString(
                    context.getString(R.string.api_auth_token), "Nope");

            Log.v(TAG, APIDataFetcher.getSimpleSignedResponse(url, token));

            //TODO: check whether event has been registered for or not.
        } catch (IOException e)
        {
            Log.d(TAG, e.toString());
        }
        return false;
    }
}

abstract class RegisterForEventIndividual extends AsyncTask<Void, Void, Boolean>
{
    private String TAG = RegisterForEventIndividual.class.getSimpleName();
    protected Context context;
    protected Event event;
    public RegisterForEventIndividual(Context c, Event event)
    {
        this.context = c;
        this.event = event;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            String url = context.getString(R.string.URL_api_base) + "m/register/" + event.id + ".json";
            String token = PreferenceManager.getDefaultSharedPreferences(context).getString(
                    context.getString(R.string.api_auth_token), "Nope");
            String response = APIDataFetcher.getSimpleSignedResponse(url, token);
//            Log.v(TAG, response);

            return (new JSONObject(response).getString("data").equals("Success"));

        } catch (IOException e)
        {
            Log.d(TAG, e.toString());
        } catch (JSONException e) {
            Log.d(TAG, "ParseException" + e.toString());
        }
        return false;
    }
}

abstract class RegisterForEventTeam extends AsyncTask<Void, Void, String>
{
    private String TAG = RegisterForEventTeam.class.getSimpleName();
    protected Context context;
    protected Event event;
    protected String team_name;
    protected boolean new_team;
    public RegisterForEventTeam(Context c, Event event, String team_name, boolean new_team)
    {
        this.context = c;
        this.event = event;
        this.team_name = team_name;
        this.new_team = new_team;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = context.getString(R.string.URL_api_base) + "/m/register/" + event.id + "/";

        String response = "Failed";

        try
        {
            if (new_team)
            {
                url += "team/" + URLEncoder.encode(team_name, "UTF-8") + ".json";
            }
            else
            {
                url += team_name + ".json";
            }
        } catch (UnsupportedEncodingException e){
            Log.d(TAG, "UnsupportedEncodingException: " + team_name + " " + e.toString());
            return response;
        }
//        Log.v(TAG, "Connecting to "+ url);

        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.api_auth_token), "Nope");



        try {
            response = APIDataFetcher.getSimpleSignedResponse(url, token);
//            if (response != null) Log.v(TAG, response.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return response;
        }

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("data");
            if (status.contains("Success")) {
                return jsonResponse.getString("team_code");
            }
        } catch (JSONException e){
            Log.d("RegisterTeamJson", e.toString());
        }

        return "Failed";
    }
}

class UpdateProfile extends AsyncTask<String, Void, Boolean>
{
    private Context context;
    public UpdateProfile(Context context)
    {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url =  context.getString(R.string.URL_api_base) + "m/profile/update.json";
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.api_auth_token), "Nope");

        if (params.length != 3)
        {
            Log.d("UpdateProfile", "Got wrong number of params:" + Arrays.toString(params));
            return false;
        }

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.addHeader("Cookie", "_esya2015_backend_session=" + token);
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", params[0]);
            jsonObject.put("college", params[1]);
            jsonObject.put("phone", params[2]);


            StringEntity se = new StringEntity(new JSONObject().put("participant", jsonObject).toString());

            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);

            LoginPingTest loginPingTestAndUpdate = new LoginPingTest(context){
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (!aBoolean)
                    {
                        Log.d("ProfileAfterUpdate: ", "Not done:" + aBoolean);
                        return;
                    }
                }
            };
            loginPingTestAndUpdate.execute();

            return true;
        } catch (IOException e){
            Log.d("UpdateProfile", "Could not update profile: " + e.toString());
        } catch (JSONException e){
            Log.d("UpdateProfile", "JSON Exception: " + e.toString());
        }
        return false;
    }
}

class FetchScheduleImagesTask extends AsyncTask<Void, Void, Bitmap[]>
{
    private final String OLDEST_LAST_MODIFIED = "Wed, 09 Apr 2008 23:55:38 GMT";
    private final String NEWEST_LAST_MODIFIED = "Sat, 05 Sep 2015 23:55:38 GMT";
    private final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private final String day1_file_name = "schedule__day1.png";
    private final String day2_file_name = "schedule__day2.png";



    protected Context context;
    public FetchScheduleImagesTask(Context context)
    {
        this.context = context;
    }

    private String LOG_TAG = FetchScheduleImagesTask.class.getSimpleName();

    @Override
    protected Bitmap[] doInBackground(Void... voids) {
        String day1_url = context.getString(R.string.URL_schedule_image_base) + "Day1.png";
        String day2_url = context.getString(R.string.URL_schedule_image_base) + "Day2.png";

        boolean download1 = false;
        boolean download2 = false;

        // first see if images are in cache
        boolean day1_old_present = Arrays.asList(context.fileList()).contains(day1_file_name);
        boolean day2_old_present = Arrays.asList(context.fileList()).contains(day2_file_name);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String known_last_modified_1 = prefs.getString("schedule__1__last_modified", OLDEST_LAST_MODIFIED);
        String known_last_modified_2 = prefs.getString("schedule__2__last_modified", OLDEST_LAST_MODIFIED);
        SharedPreferences.Editor editor = prefs.edit();

        // for the images that are present, send a head request to find when they were changed.
        Log.v(LOG_TAG, "1 already present: " + day1_old_present);
        if (day1_old_present)
        {
            String new_last_modified = APIDataFetcher.get_last_modified_header(day1_url);
            Log.v(LOG_TAG, "new_last_modified_1" + new_last_modified);
            if (new_last_modified == null) new_last_modified = NEWEST_LAST_MODIFIED;
            Log.v(LOG_TAG, "new_last_modified_1" + new_last_modified);
            try {
                if (format.parse(new_last_modified).after(format.parse(known_last_modified_1))
                        && !format.parse(new_last_modified).equals(format.parse(known_last_modified_1)))
                {
                    known_last_modified_1 = new_last_modified;
                    download1 = true;
                }
            } catch (ParseException e) {
                Log.i(LOG_TAG, e.toString());
                download1 = true;
            }
        }
        else
        {
            download1 = true;
        }
        Log.v(LOG_TAG, "1 should download" + download1);

        Log.v(LOG_TAG, "2 already present" + day2_old_present);
        if (day2_old_present)
        {
            String new_last_modified = APIDataFetcher.get_last_modified_header(day2_url);
            Log.v(LOG_TAG, "new_last_modified_2" + new_last_modified);
            if (new_last_modified == null) new_last_modified = NEWEST_LAST_MODIFIED;
            Log.v(LOG_TAG, "new_last_modified_2" + new_last_modified);

            try {
                if (format.parse(new_last_modified).after(format.parse(known_last_modified_2))
                        && !format.parse(new_last_modified).equals(format.parse(known_last_modified_1)))
                {
                    known_last_modified_2 = new_last_modified;
                    download2 = true;
                }
            } catch (ParseException e) {
                Log.i(LOG_TAG, e.toString());
                download2 = true;
            }
        }
        else
        {
            download2 = true;
        }
        Log.v(LOG_TAG, "2 should download" + download2);

        Bitmap[] result = new Bitmap[2];
        Bitmap image;
        if (download1) {
            Log.v(LOG_TAG, "Downloading 1");
            image = APIDataFetcher.getImageFromURL(day1_url);
            //save image
            if (image != null)
            {
                Log.v(LOG_TAG, "downloaded");
                try
                {
                    FileOutputStream fileOutputStream = context.openFileOutput(day1_file_name,
                            Context.MODE_PRIVATE);

                    image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    editor.putString("schedule__1__last_modified", known_last_modified_1).commit();
                    Log.v(LOG_TAG, "Saved 1.: " + known_last_modified_1);
                } catch (IOException e)
                {
                    Log.e("saveImage", "Error writing while saving image of " + day1_file_name + ": " + e.toString());
                }
            }else Log.v(LOG_TAG, "Could not download 1");
        }
        try
        {
            result[0] = BitmapFactory.decodeStream(new FileInputStream(context.getFileStreamPath(day1_file_name)));
            DataHolder.DAY1_SCHEDULE = result[0];
            Log.v(LOG_TAG, "DAY1" + DataHolder.DAY1_SCHEDULE);
        } catch (FileNotFoundException e)
        {
            Log.v(LOG_TAG, "File not downloaded");
            DataHolder.DAY1_SCHEDULE = null;
        }

        if (download2) {
            Log.v(LOG_TAG, "Downloading 2");
            image = APIDataFetcher.getImageFromURL(day2_url);
            //save image

            if (image != null)
            {
                Log.v(LOG_TAG, "Downloaded 2");
                try
                {
                    FileOutputStream fileOutputStream = context.openFileOutput(day2_file_name,
                            Context.MODE_PRIVATE);

                    image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    editor.putString("schedule__2__last_modified", known_last_modified_2).commit();
                    Log.v(LOG_TAG, "Saved 2:: " + known_last_modified_2);
                } catch (IOException e)
                {
                    Log.e("saveImage", "Error writing while saving image of " + day2_file_name + ": " + e.toString());
                }
            }
            else Log.d(LOG_TAG, "Could not download 2");
        }
        try
        {
            result[1] = BitmapFactory.decodeStream(new FileInputStream(context.getFileStreamPath(day2_file_name)));
            DataHolder.DAY2_SCHEDULE = result[1];
            Log.v(LOG_TAG, "DAY2" + DataHolder.DAY2_SCHEDULE);
        } catch (FileNotFoundException e)
        {
            Log.v(LOG_TAG, "File not downloaded");
            DataHolder.DAY2_SCHEDULE = null;
        }
        return result;
    }
}