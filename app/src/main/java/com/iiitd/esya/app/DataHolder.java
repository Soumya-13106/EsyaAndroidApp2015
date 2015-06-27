package com.iiitd.esya.app;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by darkryder on 27/6/15.
 * As of now, this is a temporary class to hold the category/event/details data
 */
public class DataHolder {

    public static String[] CATEGORIES = {
            "CSE",
            "ECE",
            "Workshop",
            "School",
            "All"
    };

    public static final String ELIGIBILITY_RESPONSE = "eligibilty";
    public static final String JUDGING_RESPONSE =  "judging";
    public static final String PRIZES_RESPONSE =  "prizes";
    public static final String RULES_RESPONSE =  "rules";
    public static final String VENUE_RESPONSE =  "venue";
    public static final String DESCRIPTION_RESPONSE =  "description";
    public static final String TEAM_SIZE_RESPONSE =  "team_size";
    public static final String CONTACT_RESPONSE =  "contact";

    public static final String ELIGIBILITY_DEFAULT = "No specific eligibility criteria";
    public static final String JUDGING_DEFAULT =  "Judging criteria not decided yet.";
    public static final String PRIZES_DEFAULT =  "Prizes have not been decided yet.";
    public static final String RULES_DEFAULT =  "Rules have not been decided yet.";
    public static final String VENUE_DEFAULT =  "IIIT - D";
    public static final String DESCRIPTION_DEFAULT =  null;
    public static final int TEAM_SIZE_DEFAULT =  1;
    public static final String CONTACT_DEFAULT =  "events.esya@iiitd.ac.in";

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

    public static HashMap<Integer, Event> EVENTS;
}
