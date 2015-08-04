package com.iiitd.esya.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */

/**
 * The cards are hardcoded as of now, we have to use RecyclerView othervise this wont work. The toolbar will not collapse while using ListView.
 * For recycleView Reference:  http://enoent.fr/blog/2015/01/18/recyclerview-basics/
 */
public class EventActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
//    pprivate RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int mProminentColor;
    private Event mEvent;

    private MaterialViewPager mViewPager;
//    private ViewPager viewPager;
    private RecyclerViewMaterialAdapter mAdapter;

    private static int getContrastColor(int color) {
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return (y >= 128 ? Color.BLACK : Color.WHITE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) view.
                findViewById(R.id.materialviewpager_pagerTitleStrip);
        pagerSlidingTabStrip.setUnderlineColor(getContrastColor(mProminentColor));

        if (pagerSlidingTabStrip!= null)
        {
            Log.v("Setting Color to", getContrastColor(mProminentColor) + "");
            pagerSlidingTabStrip.setTextColor(getContrastColor(mProminentColor));
        }

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.register_button);
        if(mEvent.registered)
        {
            fab.setRippleColor(Color.RED);
        } else {
            fab.setRippleColor(Color.BLUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        String auth_token = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(getString(R.string.api_auth_token), "nope");

        int eventPk = getActivity().getIntent().getIntExtra(Intent.EXTRA_UID, -1);
        final Event event = DataHolder.EVENTS.get(eventPk);

        mEvent = event;

        mViewPager = (MaterialViewPager) view.findViewById(R.id.materialViewPager);

        final HashMap<String, EventDetailFragment> details_to_fragments = new HashMap<>();

        details_to_fragments.put("description", EventDetailFragment.newInstance(event.description));
        details_to_fragments.put("contact", EventDetailFragment.newInstance(event.contact));
        details_to_fragments.put("eligibility", EventDetailFragment.newInstance(event.eligibility));
        details_to_fragments.put("judging", EventDetailFragment.newInstance(event.judging));
        details_to_fragments.put("rules", EventDetailFragment.newInstance(event.rules));
        details_to_fragments.put("prizes", EventDetailFragment.newInstance(event.prizes));

        mViewPager.getViewPager().setAdapter(
                new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
                    @Override
                    public Fragment getItem(int position) {
                        switch (position) {
                            case 0:
                                return details_to_fragments.get("description");
                            case 1:
                                return details_to_fragments.get("contact");
                            case 2:
                                return details_to_fragments.get("eligibility");
                            case 3:
                                return details_to_fragments.get("judging");
                            case 4:
                                return details_to_fragments.get("rules");
                            case 5:
                                return details_to_fragments.get("prizes");
                        };
                        return null;
                    }

                    @Override
                    public int getCount() {
                        return 6;
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        switch (position) {
                            case 0:
                                return "Description";
                            case 1:
                                return "Contact";
                            case 2:
                                return "Eligibility";
                            case 3:
                                return "Judging";
                            case 4:
                                return "Rules";
                            case 5:
                                return "Prizes";
                        }
                        return "What!";
                    }
                });

        Bitmap image = null;
        if (event.image_url != null)
        {
            image = event.getCacheImage(Event.getImageNameFromUrl(
                    event.image_url), getActivity());
        }
        if (image == null)
        {
            image = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.logo);
        }

        ImageView header = ((ImageView)view.findViewById(R.id.materialviewpager_imageHeader));
        header.setScaleType(ImageView.ScaleType.FIT_XY);
        header.setImageDrawable(new BitmapDrawable(getActivity().getResources(), image));

//        mProminentColor = Bitmap.createScaledBitmap(image, 1, 1, true).getPixel(0, 0);
        Bitmap lower_end = Bitmap.createBitmap(image, 0,
                image.getHeight() - 50, image.getWidth(), 50);
        mProminentColor = Bitmap.createScaledBitmap(lower_end, 1, 1, true).getPixel(0, 0);


//        Palette palette = new Palette.Builder(image).generate();
//        mProminentColor = palette.getVibrantColor(Color.BLACK);
        Log.w("Color", mProminentColor + "");

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        mViewPager.getViewPager().setCurrentItem(1);
        mViewPager.getViewPager().setCurrentItem(0);

//        mAdapter = new RecyclerViewMaterialAdapter(new EventAdapterForRecylerView(old_event.image_url));



//        mRecyclerView = (RecyclerView) view.findViewById(R.id.scrollableview);
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mRecyclerView.setAdapter(mAdapter);
//        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
//
//        PagerSlidingTabStrip strip = (PagerSlidingTabStrip) view.findViewById(R.id.materialviewpager_pagerTitleStrip);
//        strip.setViewPager(mViewPager.getViewPager());

        //TODO: You'll have to create new adapters and recyclerviews for every tab,
        // with each adapter holding the data only for 1 card with that 1 card holding
        // data only for that one specific info of the event
        //mAdapter = new EventAdapterForRecylerView(old_event.image_url);
        //mRecyclerView.setAdapter(mAdapter);


//        final TextView textView = (TextView)view.findViewById(R.id.event_text);


        MaterialViewPagerHelper.registerScrollView(getActivity(),
                (ObservableScrollView)view.findViewById(R.id.scrollView), null);

        return view;
    }
}

class EventAdapterForRecylerView extends RecyclerView.Adapter<EventAdapterForRecylerView.ViewHolder>
{
    private String info;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public View mView;
        public ViewHolder(View v)
        {
            super(v);
            mView = v;
        }
    }

    public EventAdapterForRecylerView(String info)
    {
        this.info = info;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view

        //  //  //  //  //  //  //  //  //  //  //  //  //
        // TODO: PALASH!!!                              //
        // The layout below is the card layout.         //
        // Create a new card layout and replace this    //
        //  //  //  //  //  //  //  //  //  //  //  //  //
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.changeable_list_item_event, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


        //  //  //  //  //  //  //  //  //  // //  //
        // TODO: PALASH!!!                         //
        // This is the generic text box in card    //
        // you finally make.Replace the id         //
        //  //  //  //  //  //  //  //  //  //  // //
//        ((TextView)holder.mView.findViewById(R.id.list_item_event_textview)).setText(info);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}