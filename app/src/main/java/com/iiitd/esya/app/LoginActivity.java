package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


public class LoginActivity extends FragmentActivity{

    FragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    private String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_logged_in), false
        )){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
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
