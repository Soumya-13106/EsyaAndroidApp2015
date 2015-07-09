package com.iiitd.esya.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by darkryder on 28/6/15.
 */
public class Event {
    public String name;
    public Category category;
    public String image_url;
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
    private Bitmap image;
    // TODO: set a default image

    public boolean isImageInCache(String uri, Context context)
    {
        return Arrays.asList(context.fileList()).contains(uri);
    }

    public static String getImageNameFromUrl(String url)
    {
        // http://esya.iiitd.edu.in/uploads/event/photo/10/Hackon.png
        // 0    1 2                  3        4    5    6   7
        String[] split = url.split("/");
//        if (split.length != 8) return null;
        return split[7];
    }

    public void setCacheImage(Bitmap bmp, String uri, Context context)
    {
        try
        {
            FileOutputStream fileOutputStream = context.openFileOutput(uri,
                    Context.MODE_PRIVATE);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.v("saveImage", "Saved image of " + this.name + ": " + uri);
        } catch (IOException e)
        {
            Log.e("saveImage", "Error writing while saving image of " + this.name + ": " + e.toString());
        }
    }

    public Bitmap getCacheImage(String uri, Context context)
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(context.getFileStreamPath(uri));
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e){
            Log.e("getImage", "Could not find image in cache of" + this.name + ": " + e.toString());
            return null;
        }
    }


    public Event(int id, String name, Category category, String image_url)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image_url = image_url;
        this.image = null;
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

    public void copyFrom(Event freshEvent){
        Event f = freshEvent;
        id = f.id; name = f.name; category = f.category; image_url = f.image_url;
        contact = f.contact; eligibility = f.eligibility; judging = f.judging;
        prizes = f.prizes; rules = f.rules; team_size = f.team_size;
        venue = f.venue; description = f.description;
    }
}
