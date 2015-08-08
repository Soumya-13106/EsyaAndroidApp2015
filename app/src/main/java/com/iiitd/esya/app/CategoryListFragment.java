package com.iiitd.esya.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Soumya on 24-06-2015.
 */
public class CategoryListFragment extends Fragment{

    CategoryCardListAdapter categoryListAdapter;
    public CategoryListFragment() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DBHelper dbHelper = new DBHelper(getActivity());

        FetchAllEventsTask fetchAllEventsTask = new FetchAllEventsTask(
                PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                        getString(R.string.api_auth_token), "")) {

            @Override
            protected Event[] doInBackground(Void... voids) {
                if (DataHolder.EVENT_UPDATED_AFTER_LOGIN) return null;
                return super.doInBackground(voids);
            }

            @Override
            protected void onPostExecute(Event[] events) {
                super.onPostExecute(events);
                if (events == null) return;
                for(Event e: events)
                {
                    Event original = DataHolder.EVENTS.get(e.id);
                    if (original == null || original.team_id == null) continue;
                    if (original.team_id.equals(e.team_id)) continue;
                    original.team_id = e.team_id;
                    dbHelper.updateEvent(original);
                    Log.v(LOG_TAG, "Updated" + original.id + "");
                }
                DataHolder.EVENT_UPDATED_AFTER_LOGIN = true;
            }
        };
        fetchAllEventsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);

        List<String> categoryListData = new ArrayList<String>();

        for(Category c: Category.values()){
            categoryListData.add(c.naturalName);
        }

        categoryListAdapter = new CategoryCardListAdapter(getActivity(),
                new ArrayList<Category>(Arrays.asList(Category.values())));

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_category);
        listView.setAdapter(categoryListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Category category = (Category) categoryListAdapter.getItem(position);
                Intent startEventListActivityIntent = new Intent(getActivity(), EventListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        .putExtra(Intent.EXTRA_UID, category.id);
                startActivity(startEventListActivityIntent);
            }
        });


        return rootView;
    }

}

/**
 * Created by darkryder on 9/7/15.
 */
class CategoryCardListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<Category> categories;
    Context context;

    public CategoryCardListAdapter(Context context, ArrayList<Category> categories)
    {
        this.context = context;
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (categories == null) ? 0 : categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Category category = (Category)getItem(i);

        if (view == null){
            view = inflater.inflate(R.layout.list_item_category, null);
        }

        Bitmap image = null;
        int which = 0;
        switch (category.id)
        {
            case 0:
                which = R.drawable.all; break;
            case 1:
                which = R.drawable.cse; break;
            case 2:
                which = R.drawable.ece; break;
            case 3:
                which = R.drawable.school; break;
            case 4:
                which = R.drawable.flagship; break;
            case 5:
                which = R.drawable.nontech; break;
            case 6:
                which = R.drawable.workshops; break;
            case 7:
                which = R.drawable.techathlon; break;
        }
        image = BitmapFactory.decodeResource(context.getResources(), which);

        ((ImageView) view.findViewById(R.id.event_card_image)).setImageBitmap(image);

        return view;
    }
}
