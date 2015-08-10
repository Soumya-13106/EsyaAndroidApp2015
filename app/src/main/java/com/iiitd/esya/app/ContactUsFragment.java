package com.iiitd.esya.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Soumya on 24-06-2015.
 */
public class ContactUsFragment extends Fragment{

    MapView mMapView;
    private GoogleMap googleMap;
    static final LatLng IIITD = new LatLng(28.5472915, 77.2732008);
    public ContactUsFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

//        Log.v("LOG","Entered onCreateView");
        mMapView.onResume();
//        Log.v("LOG", "onResume Called");
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
//            Log.v("LOG","MapsInitialized");
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // create marker
        Marker iiitd = googleMap.addMarker(new MarkerOptions().position(IIITD)
                .title("IIIT Delhi")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://goo.gl/maps/CqjtG"));
                startActivity(intent);
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IIITD, 15));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void startIntent(String url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((ImageView)view.findViewById(R.id.click_facebook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("https://www.facebook.com/EsyaIIITD");
            }
        });
        ((ImageView)view.findViewById(R.id.click_blogger)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("http://esya.iiitd.edu.in/blog/");
            }
        });
        ((ImageView)view.findViewById(R.id.click_instagram)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("https://instagram.com/esya_iiitd");
            }
        });
        ((ImageView)view.findViewById(R.id.click_twitter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent("https://twitter.com/EsyaIIITD");
            }
        });
    }
}
