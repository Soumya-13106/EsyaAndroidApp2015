package com.iiitd.esya.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LandingPage extends Fragment {
    public LandingPage() {
        // Required empty public constructor
    }

    private static View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view != null) return view;

        view = inflater.inflate(R.layout.fragment_landing_page, container, false);
        return view;
    }
}
