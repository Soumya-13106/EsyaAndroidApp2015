package com.iiitd.esya.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by Soumya on 24-06-2015.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button)view.findViewById(R.id.profile_submit);

        final Context context = getActivity();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        ((EditText) view.findViewById(R.id.profile_name)).
                setText(pref.getString(context.getString(R.string.profile_name), "Name"));
        ((EditText)view.findViewById(R.id.profile_college)).
                setText(pref.getString(context.getString(R.string.profile_college), "College"));
        ((EditText)view.findViewById(R.id.profile_phone)).
                setText(pref.getString(context.getString(R.string.profile_phone), "Phone"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)view.findViewById(R.id.profile_name)).getText().toString();
                String college = ((EditText)view.findViewById(R.id.profile_college)).getText().toString();
                String phone = ((EditText)view.findViewById(R.id.profile_phone)).getText().toString();

                new UpdateProfile(context){
                    @Override
                    protected void onPostExecute(Boolean updated) {
                        super.onPostExecute(updated);
                        if (updated)
                        {
                            Toast.makeText(getActivity(), "Profile updated.", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(getActivity(), "Unable to update profile",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }.executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, name, college, phone);
            }
        });

        ((Button)view.findViewById(R.id.profile_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                GoogleApiClient googleApiClient = MainActivity.getGoogleApiClient();

                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(googleApiClient);
                    googleApiClient.disconnect();
                    googleApiClient = null;
                }

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                sharedPref.edit().putBoolean(getString(R.string.pref_logged_in), false).commit();
                sharedPref.edit().putBoolean(getString(R.string.pref_login_skipped), false).commit();
                sharedPref.edit().putString(getString(R.string.api_auth_token), "Nope").commit();
                DataHolder.EVENT_UPDATED_AFTER_LOGIN = 0;

                Toast.makeText(activity, "Logged out", Toast.LENGTH_SHORT).show();

                DBHelper.deleteDatabase(activity);
                if (DataHolder.EVENTS.values() != null) {

                    for (Event e : DataHolder.EVENTS.values()) {
                        e.registered = false;
                        e.team_id = DataHolder.TEAM_ID_DEFAULT;
                    }
                }

                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

}
