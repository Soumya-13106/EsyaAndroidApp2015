package com.iiitd.esya.app;

/**
 * Created by darkryder on 28/6/15.
 */
public class Event {
    public String name;
    public Category category;
    public String image;
    public int id;

    // Let's create everything public. !!

    // Format is name:email:phone
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
}
