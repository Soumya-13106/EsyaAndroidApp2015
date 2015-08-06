package com.iiitd.esya.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by darkryder on 28/6/15.
 */
public class Event {
    public String name;
    public Category[] categories;
    public String image_url;
    public int id;

    // Let's create everything public. !!

    public String contact;
    public boolean registered;
    public boolean team_event;
    public String team_id;
    Date event_date_time;
    Date updated_at;

    public String eligibility;
    public String judging;
    public String prizes;
    public String rules;
    public int team_size;
    public String venue;
    // Datetime registration_deadline
    public String description;

    public static Date parseStringToDate(String date) {
        /*SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
        Date d = format.parse(date);*/
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return serverFormat.parse(date);
        } catch (ParseException e) {
            Log.v("Date parsed", e.toString());
            return new Date();
        }
    }

    public static boolean isDBStale(Event networkEvent, Event dbEvent)
    {
        return networkEvent.updated_at.after(dbEvent.updated_at);
    }

    public static boolean updateEventInDB(Event staleEvent, Event fresh_event, Context context)
    {
        staleEvent.copyFrom(fresh_event);
        DBHelper dbHelper = new DBHelper(context);
        return dbHelper.updateEvent(staleEvent);
    }

    public static String parseDateToString(Date date) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return serverFormat.format(date);
    }

    public boolean isImageInCache(String uri, Context context)
    {
        return Arrays.asList(context.fileList()).contains(uri);
    }

    public static String getImageNameFromUrl(String url)
    {
        // http://esya.iiitd.edu.in/uploads/event/photo/10/Hackon.png
        // 0    1 2                  3        4    5    6   7
        String[] split = url.split("/");
        return split[7];
    }

    public void setCacheImage(Bitmap bmp, String uri, Context context)
    {
        DataHolder.TOTAL_DOWNLOADED_IMAGE_SIZE += bmp.getByteCount();

        try
        {
            if (DataHolder.COMPRESS_IMAGES_BEFORE_SAVING)
            {
                WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int screen_width = size.x;

                float image_original_aspect = (float)bmp.getWidth()/(float)bmp.getHeight();

                bmp = Bitmap.createScaledBitmap(bmp,
                        screen_width, Math.round(screen_width*(1/image_original_aspect)), true);
            }

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

    public Event(int id, String name, Category[] categories, String image_url, String updated_at)
    {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.image_url = image_url;
        this.updated_at = parseStringToDate(updated_at);

        this.contact = DataHolder.CONTACT_DEFAULT;
        this.registered = DataHolder.REGISTERED_DEFAULT != 0;
        this.team_event = DataHolder.TEAM_EVENT_DEFAULT;
        this.team_id = DataHolder.TEAM_ID_DEFAULT;
        this.eligibility = DataHolder.ELIGIBILITY_DEFAULT;
        this.judging = DataHolder.JUDGING_DEFAULT;
        this.prizes = DataHolder.PRIZES_DEFAULT;
        this.rules = DataHolder.RULES_DEFAULT;
        this.team_size = DataHolder.TEAM_SIZE_DEFAULT;
        this.venue = DataHolder.VENUE_DEFAULT;
        this.description = DataHolder.DESCRIPTION_DEFAULT;
        this.event_date_time = DataHolder.EVENT_DATE_TIME_DEFAULT;
    }

    public String toString(){
        return this.id + ": " + this.name + " -> " + Arrays.toString(this.categories);
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
        id = f.id; name = f.name; categories = f.categories; image_url = f.image_url;
        contact = f.contact; eligibility = f.eligibility; judging = f.judging;
        prizes = f.prizes; rules = f.rules; team_size = f.team_size;
        venue = f.venue; description = f.description; registered = f.registered;
        team_event = f.team_event; team_id = f.team_id; updated_at = f.updated_at;
        event_date_time = f.event_date_time;
    }
}
