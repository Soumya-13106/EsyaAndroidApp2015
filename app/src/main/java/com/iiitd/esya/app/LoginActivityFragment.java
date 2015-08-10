package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final View loading = view.findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);
        ((Button) view.findViewById(R.id.skipButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).onSkipButtonClicked(view);

            }
        });
        ((SignInButton) view.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity) getActivity()).onSignInClicked(view, loading);
            }
        });
        return view;
    }
}
