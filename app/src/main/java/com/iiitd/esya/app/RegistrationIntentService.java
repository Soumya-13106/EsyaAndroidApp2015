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
        try{
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.GCM_app_server_sender_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit().putString(getString(R.string.pref_GCM_token), token).apply();

            sendGCMTokenToServer(token);

        } catch (Exception e){
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendGCMTokenToServer(String token)
    {
        final String API_URL = getString(R.string.URL_register_GCM_token);

        String api_token = PreferenceManager.getDefaultSharedPreferences(this).
                                            getString(getString(R.string.api_auth_token), "0");
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(API_URL);
            httppost.addHeader("Cookie",  "_esya2015_backend_session=" + api_token);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("gcm_token", token));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);

        } catch (IOException e){
            Log.d(TAG, "Could not send token to server: " + e.toString());
        }
        Log.v(TAG, "Sent token " + token + " to server");
    }
}
