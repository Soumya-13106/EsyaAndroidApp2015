package com.iiitd.esya.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)view.findViewById(R.id.profile_name)).getText().toString();
                String college = ((EditText)view.findViewById(R.id.profile_college)).getText().toString();
                String phone = ((EditText)view.findViewById(R.id.profile_phone)).getText().toString();

                new UpdateProfile(getActivity()){
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

}
