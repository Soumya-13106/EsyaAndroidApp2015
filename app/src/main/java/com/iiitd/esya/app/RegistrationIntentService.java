package com.iiitd.esya.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkryder on 23/7/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "in onHandleRequest" + intent);
        try{
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(DataHolder.GCM_APP_SERVER_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("GCM_TOKEN", token);
            editor.commit();

            sendRegistrationTokenToServer(token);

        } catch (Exception e){
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendRegistrationTokenToServer(String token)
    {

        final String API_URL = "http://esya.iiitd.edu.in/m/registerGCMToken";

        String userId = PreferenceManager.getDefaultSharedPreferences(this).
                                            getString("ID_TOKEN", "0");
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(API_URL);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("userId", userId));
            nameValuePairs.add(new BasicNameValuePair("gcm_token", token));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);

        } catch (IOException e){
            Log.d(TAG, "Could not send token to server: " + e.toString());
        }
        Log.d(TAG, "Sent token " + token + " to server");
    }
}
