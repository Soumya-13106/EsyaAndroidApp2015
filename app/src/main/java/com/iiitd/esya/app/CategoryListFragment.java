package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
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

        List<String> categoryListData = new ArrayList<String>();

        for(Category c: Category.values()){
            categoryListData.add(c.naturalName);
        }

        categoryListAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_category, // The name of the layout ID.
                        R.id.list_item_category_textview, // The ID of the textview to populate.
                        categoryListData);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_category);
        listView.setAdapter(categoryListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Category category = Category.resolveToCategory(categoryListAdapter.getItem(position));
                Intent startEventListActivityIntent = new Intent(getActivity(), EventListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .putExtra(Intent.EXTRA_UID, category.id);
                startActivity(startEventListActivityIntent);
            }
        });
        return rootView;
    }

}
