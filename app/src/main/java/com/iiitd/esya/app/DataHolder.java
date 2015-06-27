package com.iiitd.esya.app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by darkryder on 27/6/15.
 * As of now, this is a temporary class to hold the category/event/details data
 */
public class DataHolder {

    public static final String[] CATEGORIES = {
            "CSE",
            "ECE",
            "Workshop",
            "School",
            "All"
    };
    public static final HashMap<String, String[]> CATEGORY_TO_EVENTS = new HashMap<>();
    public static final HashMap<String, String[]> EVENT_TO_DETAILS = new HashMap<>();
    // in the form of image_name, description, person_to_contact;

    private static boolean initialised = false;

    public static void init(){
        if (initialised) return;
        else initialised = true;

        String[] CSE_EVENTS = {"Hackon", "Hack.IIIT", "Prosort", "Toast To Code"};
        String[] ECE_EVENTS = {"Aeronuts", "Hardware Hackathon"};
        String[] WORKSHOP_EVENTS = {"Open Source", "GO language"};
        String[] SCHOOL_EVENTS = {"Teach CSS", "Teach Python"};
        String[][] _collections = {CSE_EVENTS, ECE_EVENTS, WORKSHOP_EVENTS, SCHOOL_EVENTS};
        ArrayList<String> temp_all_events = new ArrayList<>();

        for (String[] cat: _collections){
            for(String ev: cat){
                temp_all_events.add(ev);
            }
        }
        String[] ALL_EVENTS = (String[]) temp_all_events.toArray(new String[temp_all_events.size()]);

        CATEGORY_TO_EVENTS.put("CSE", CSE_EVENTS);
        CATEGORY_TO_EVENTS.put("ECE", ECE_EVENTS);
        CATEGORY_TO_EVENTS.put("Workshop", WORKSHOP_EVENTS);
        CATEGORY_TO_EVENTS.put("School", SCHOOL_EVENTS);
        CATEGORY_TO_EVENTS.put("All", ALL_EVENTS);

        String[] temp_details = {"image1.jpg", "Coming Soon", "a@b.com"};
        for(String ev: ALL_EVENTS){
            EVENT_TO_DETAILS.put(ev, temp_details);
        }
    };
}
