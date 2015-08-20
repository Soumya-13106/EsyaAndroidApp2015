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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

        final Bitmap day1 = DataHolder.DAY1_SCHEDULE;
        final Bitmap day2 = DataHolder.DAY2_SCHEDULE;

        if (day1 == null || day2 == null)
        {
            view = inflater.inflate(R.layout.no_elements_layout, container, false);
            ((TextView)view.findViewById(R.id.message)).setText(R.string.no_elem_schedule);
            view.findViewById(R.id.toolbar).setVisibility(View.GONE);
            return view;
        }

        ListView listView = (ListView) view.findViewById(R.id.schedule_list);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Object getItem(int position) {
                return (position == 0 ? day1 : day2);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Bitmap image = (position == 0) ? day1 : day2;

                if (convertView== null){
                    convertView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_schedule_element, null);
                }

                ((ImageView)convertView.findViewById(R.id.image_schedule_element)).setImageBitmap(image);

                return convertView;
            }
        });

        return view;
    }
}