package com.iiitd.esya.app;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by darkryder on 28/6/15.
 */
public class Event {
    public String name;
    public Category category;
    public String image;
    public int id;

    // Let's create everything public. !!

    public String[] contact;

    public String eligibility;
    // Datetime date_time;
    public String judging;
    public String prizes;
    public String rules;
    public int team_size;
    public String venue;
    // Datetime registration_deadline
    public String description;


    public Event(int id, String name, Category category, String image)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public String toString(){
        return this.id + ": " + this.name + " -> " + this.category;
    }

    public String debuggableToString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getName());
        stringBuilder.append(": ");
        for (Field f: getClass().getDeclaredFields() ){
            stringBuilder.append(f.getName());
            stringBuilder.append("=");
            try {
                stringBuilder.append(f.get(this));
            } catch (IllegalAccessException e) {
                Log.e("Error printing event", this.toString());
            }
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }
}
