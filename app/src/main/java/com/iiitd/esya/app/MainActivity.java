package com.iiitd.esya.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    FrameLayout mContentFrame;
    private int mCurrentSelectedPosition;

    private CharSequence mTitle;
    private static String TAG = MainActivity.class.getSimpleName();
    private static GoogleApiClient mGoogleApiClient;
    private static final String esyaShare = "IIITD's Technical Fest Esya is around the corner. Go visit : http://esya.iiitd.edu.in/ ";
    private static final String EVENT_SHARE_HASHTAG = "#Esya2015 #IIITD";

    public static GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }


    @Override
    protected void onDestroy() {
        if (mGoogleApiClient !=null && mGoogleApiClient.isConnected())
        {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    private void attachToGoogleLoginApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope("https://www.googleapis.com/auth/plus.profile.emails.read"))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        if (checkPlayServices()) {
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(getApplicationContext(),
                                    RegistrationIntentService.class);
                            startService(intent);
                        } else {
                            Log.d(TAG, "Playservices not available.");
                            Toast.makeText(getApplicationContext(),
                                    "Could not register with services", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("onConnectionSuspended", i + "");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("Failed", "failed" + connectionResult);
                    }
                })
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DataHolder.init(this);

        attachToGoogleLoginApiClient();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean logged_in = prefs.getBoolean(getString(R.string.pref_logged_in), false);
        if (!logged_in){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


        LoginPingTest test = new LoginPingTest(this);
        test.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setContentView(R.layout.activity_main);

        Fragment frag = new CategoryListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.nav_contentframe, frag);
        fragmentTransaction.commit();

        setUpToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setUpNavDrawer();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_0:
                        Snackbar.make(mContentFrame, "Events", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 0;
                        break;
                    case R.id.navigation_item_1:
                        Snackbar.make(mContentFrame, "Profile", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 1;
                        break;
                    case R.id.navigation_item_2:
                        Snackbar.make(mContentFrame, "About IIITD", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 2;
                        break;
                    case R.id.navigation_item_3:
                        Snackbar.make(mContentFrame, "About Esya", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 3;
                        break;
                    case R.id.navigation_item_4:
                        Snackbar.make(mContentFrame, "Contact Us", Snackbar.LENGTH_SHORT).show();
                        mCurrentSelectedPosition = 4;
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return changeFragment(mCurrentSelectedPosition);
            }
        });
        mTitle = getTitle();

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra("UpdateProfile"))
            {
                if (intent.getBooleanExtra("UpdateProfile", false))
                {
                    changeFragment(PROFILE_FRAGMENT_POSITION);
                }
            }
        }

    }

    private void setUpNavDrawer() {
        if (mToolbar != null && getSupportActionBar()!=null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private final int PROFILE_FRAGMENT_POSITION = 1;

    public boolean changeFragment (int position) {
        Fragment objFragment = null;
        switch (position) {
            case 0:
                objFragment = new CategoryListFragment();
                break;
            case PROFILE_FRAGMENT_POSITION:
                objFragment = new ProfileFragment();
                break;
            case 2:
                objFragment = new AboutIIITDFragment();
                break;
            case 3:
                objFragment = new AboutEsyaFragment();
                break;
            case 4:
                objFragment = new ContactUsFragment();
                break;
        }
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_contentframe, objFragment);
        fragmentTransaction.commit();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareEsyaIntent());
        } else {
            Log.d(TAG, "Share Action Provider is null?");
        }
        return super.onCreateOptionsMenu(menu);
    }

    private Intent createShareEsyaIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                esyaShare + EVENT_SHARE_HASHTAG);
        return shareIntent;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    private boolean checkPlayServices()
    {
        int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }
}
