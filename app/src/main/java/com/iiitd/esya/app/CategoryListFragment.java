package com.iiitd.esya.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Soumya on 24-06-2015.
 */
public class CategoryListFragment extends Fragment{

    ArrayAdapter<String> categoryListAdapter;
    public CategoryListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);

        String[] data = {
                "CSE Events",
                "ECE Events",
                "Workshops",
                "School Events",
                "All Events"
        };
        List<String> categoryListData = new ArrayList<String>(Arrays.asList(data));

        categoryListAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_category, // The name of the layout ID.
                        R.id.list_item_category_textview, // The ID of the textview to populate.
                        categoryListData);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_category);
        listView.setAdapter(categoryListAdapter);

        return rootView;
    }

}
