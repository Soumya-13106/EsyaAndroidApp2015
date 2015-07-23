package com.iiitd.esya.app;

import android.accounts.Account;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    FragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    private String TAG = LoginActivity.class.getSimpleName();

    private final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    public void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Logging you in.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.

        class GetAndSendIdTokenTask extends AsyncTask<Void, Void, Void> {

            private static final String API_URL = "http://esya.iiitd.edu.in/m/";

            @Override
            protected Void doInBackground(Void... voids) {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                String scopes = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
                String idToken;
                try {
                    idToken =  GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("ID_TOKEN", idToken);
                    editor.commit();
                } catch (IOException e) {
                    Log.e(TAG, "Error retrieving ID token.", e);
                    return null;
                } catch (GoogleAuthException e) {
                    Log.e(TAG, "Error retrieving ID token.", e);
                    return null;
                }

                try
                {
                    URL url = new URL(API_URL + "registerUserToken&token=" + idToken);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    int code = urlConnection.getResponseCode();
                    if (code != 200){
                        Log.d("SendToken", "Received code " + code + " for token " + idToken);
                    } else {
                        Log.v("SendToken", "Sent token " + idToken);
                    }
                } catch (IOException e){
                    Log.e("SendToken", e.toString());
                }
                return null;
            }
        }

        GetAndSendIdTokenTask task = new GetAndSendIdTokenTask();
        task.execute();

        mShouldResolve = false;

        // Show the signed-in UI
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.pref_logged_in), true);
        editor.commit();
        Toast.makeText(this, "Logged in.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "OnConnectionSuspended");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
//                    startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
//                            RC_SIGN_IN, null, 0, 0, 0);
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
//            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_logged_in), false
        )){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .build();

        setContentView(R.layout.fragment_login_container);

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            Fragment aboutus = new AboutUsFragment();
            Fragment aboutesya = new AboutEsyaFragment();
            Fragment login = new LoginActivityFragment();

            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return aboutesya;
                    case 1:
                        return aboutus;
                    case 2:
                        return login;
                    default:
                        return login;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentPagerAdapter);

    }
}
