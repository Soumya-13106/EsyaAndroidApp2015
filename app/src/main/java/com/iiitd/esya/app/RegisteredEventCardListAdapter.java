package com.iiitd.esya.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by palash on 9/8/15.
 */
public class RegisteredEventCardListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Event> events;
    Context context;

    public RegisteredEventCardListAdapter(Context context, ArrayList<Event> events)
    {
        this.context = context;
        this.events = events;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (events == null) ? 0 : events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Event event = (Event)getItem(i);

        if (view == null){
            view = inflater.inflate(R.layout.list_item_registered_event, null);
        }

//        ((TextView) view.findViewById(R.id.list_item_event_textview)).setText(event.name);

        Bitmap image = null;
        if (event.image_url != null)
        {
            image = event.getCacheImage(Event.getImageNameFromUrl(event.image_url), context);
        }
        if (image == null)
        {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.esyalogo);
        }

        ((ImageView) view.findViewById(R.id.event_card_image)).setImageBitmap(image);

        return view;
    }
}
