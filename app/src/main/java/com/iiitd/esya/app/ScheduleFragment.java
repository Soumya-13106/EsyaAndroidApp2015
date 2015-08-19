package com.iiitd.esya.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScheduleFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        Bitmap day1 = DataHolder.DAY1_SCHEDULE;
        Bitmap day2 = DataHolder.DAY2_SCHEDULE;

        if (day1 == null || day2 == null)
        {
            view = inflater.inflate(R.layout.no_elements_layout, container, false);
            ((TextView)view.findViewById(R.id.message)).setText(R.string.no_elem_schedule);
            view.findViewById(R.id.toolbar).setVisibility(View.GONE);
            return view;
        }

        final ImageView imgview1 = ((ImageView) view.findViewById(R.id.day1));
        final ImageView imgview2 = ((ImageView) view.findViewById(R.id.day2));

        imgview1.setImageBitmap(day1);
        imgview2.setImageBitmap(day2);

        return view;
    }
}
