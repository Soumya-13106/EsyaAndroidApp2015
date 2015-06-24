package com.iiitd.esya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Soumya on 24-06-2015.
 */
public class AboutEsyaFragment extends Fragment {

    public AboutEsyaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_esya, container, false);
        return rootView;
    }

}
