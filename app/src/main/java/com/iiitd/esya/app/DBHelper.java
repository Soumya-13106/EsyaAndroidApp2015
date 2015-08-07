package com.iiitd.esya.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Soumya on 31-07-2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABSE_VERSION = 1;

    public static final String DATABASE_NAME = "esya.db";
    public static final String TABLE_NAME = "event";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_EVENT_NAME = "event_name";
    public static final String COLUMN_CATEGORY_IDS = "category_ids";
    public static final String COLUMN_LAST_UPDATED = "last_updated";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_IS_REGISTERED = "is_registered";
    public static final String COLUMN_IS_TEAM_EVENT = "is_team_event";
    public static final String COLUMN_TEAM_ID = "team_id";
    public static final String COLUMN_EVENT_ELIGIBILITY = "event_eligibility";
    public static final String COLUMN_EVENT_JUDGING = "event_judging";
    public static final String COLUMN_EVENT_PRIZES = "event_prizes";
    public static final String COLUMN_EVENT_RULES = "event_rules";
    public static final String COLUMN_TEAM_SIZE = "team_size";
    public static final String COLUMN_VENUE = "venue";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_EVENT_DATE_TIME = "event_date_time";

    private static final String[] projection = {
            COLUMN_EVENT_ID,
            COLUMN_EVENT_NAME,
            COLUMN_CATEGORY_IDS,
            COLUMN_LAST_UPDATED,
            COLUMN_IMAGE_URL,
            COLUMN_CONTACT,
            COLUMN_IS_REGISTERED,
            COLUMN_IS_TEAM_EVENT,
            COLUMN_TEAM_ID,
            COLUMN_EVENT_ELIGIBILITY,
            COLUMN_EVENT_JUDGING,
            COLUMN_EVENT_PRIZES,
            COLUMN_EVENT_RULES,
            COLUMN_TEAM_SIZE,
            COLUMN_VENUE,
            COLUMN_EVENT_DESCRIPTION,
            COLUMN_EVENT_DATE_TIME
    };

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    };

    public static void deleteDatabase(Context context)
    {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_EVENT_ID + " INT NOT NULL PRIMARY KEY, " +
                        COLUMN_EVENT_NAME + " VARCHAR(255) NOT NULL, " +
                        COLUMN_CATEGORY_IDS + " VARCHAR(255) NOT NULL, " +
                        COLUMN_LAST_UPDATED + " VARCHAR(255) NOT NULL, " +
                        COLUMN_IMAGE_URL + " VARCHAR(255), " +
                        COLUMN_CONTACT + " VARCHAR(2048) NOT NULL, " +
                        COLUMN_IS_REGISTERED + " INT NOT NULL DEFAULT 0, " +
                        COLUMN_IS_TEAM_EVENT + " INT NOT NULL DEFAULT 0, " +
                        COLUMN_TEAM_ID + " VARCHAR(127) NOT NULL, " +
                        COLUMN_EVENT_ELIGIBILITY + " VARCHAR(5000) NOT NULL, " +
                        COLUMN_EVENT_JUDGING + " VARCHAR(5000) NOT NULL, " +
                        COLUMN_EVENT_PRIZES + " VARCHAR(5000) NOT NULL, " +
                        COLUMN_EVENT_RULES + " VARCHAR(5000) NOT NULL, " +
                        COLUMN_TEAM_SIZE + " INT NOT NULL, " +
                        COLUMN_VENUE + " VARCHAR(1024) NOT NULL, " +
                        COLUMN_EVENT_DESCRIPTION + " VARCHAR(10000) NOT NULL, " +
                        COLUMN_EVENT_DATE_TIME + " VARCHAR(1024)" +
                        ");"
        );
    }

    private static String getCommaSeperatedCategories(Category[] category) {
        StringBuilder result = new StringBuilder();
        for(Category c: category) {
            result.append(c.id).append(",");
        }
        return result.toString();
    }

    private static Category[] getCategoriesFromCSVString(String string) {
        if ((string == null) || (string.trim().isEmpty())) return null;
        String[] ids = string.split(",");
        ArrayList<Category> categories = new ArrayList<>();
        for(String id: ids) {
            if (id == null || id.trim().isEmpty()) continue;
            categories.add(Category.resolveToCategory(Integer.parseInt(id)));
        }
        return categories.toArray(new Category[categories.size()]);
    }

    private int returnIntFromBoolean(boolean value) {
        if(value) {
            return 1;
        } return 0;
    }

    private boolean returnBooleanFromInt(int value) {
        if(value==1) {
            return true;
        } return false;
    }

    public boolean insertEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EVENT_ID, event.id);
        contentValues.put(COLUMN_EVENT_NAME, event.name);
        contentValues.put(COLUMN_CATEGORY_IDS, getCommaSeperatedCategories(event.categories));
        contentValues.put(COLUMN_LAST_UPDATED, Event.parseDateToString(event.updated_at));
        contentValues.put(COLUMN_IMAGE_URL, event.image_url);
        contentValues.put(COLUMN_CONTACT, event.contact);
        contentValues.put(COLUMN_IS_REGISTERED, event.registered);
        contentValues.put(COLUMN_IS_TEAM_EVENT, event.team_event);
        contentValues.put(COLUMN_TEAM_ID, event.team_id);
        contentValues.put(COLUMN_EVENT_ELIGIBILITY, event.eligibility);
        contentValues.put(COLUMN_EVENT_JUDGING, event.judging);
        contentValues.put(COLUMN_EVENT_PRIZES, event.prizes);
        contentValues.put(COLUMN_EVENT_RULES, event.rules);
        contentValues.put(COLUMN_TEAM_SIZE, event.team_size);
        contentValues.put(COLUMN_VENUE, event.venue);
        contentValues.put(COLUMN_EVENT_DESCRIPTION, event.description);
        contentValues.put(COLUMN_EVENT_DATE_TIME, Event.parseDateToString(event.event_date_time));
        long check = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        if(check>0) {
            return true;
        } return false;
    }

    public Event[] getAllEvents() {
        ArrayList<Event> eventArrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//        c.moveToFirst();
        while(c.moveToNext()) {
            eventArrayList.add(getEventFromCursor(c));
//            c.moveToNext();
        }
        c.close();
        db.close();
        return eventArrayList.toArray(new Event[eventArrayList.size()]);
    }

    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME,
                projection,
                COLUMN_EVENT_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        if(c==null) {
            db.close();
            return null;
        }
        c.moveToFirst();
        Event event = getEventFromCursor(c);
        c.close();
        db.close();
        return event;
    }

    private Event getEventFromCursor(Cursor c) {
        if(c==null) {
            return null;
        }
//        if(!c.moveToFirst()){
//            return null;
//        }
        int eventId = c.getInt(c.getColumnIndexOrThrow(COLUMN_EVENT_ID));
        String eventName = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_NAME));
        Category[] eventCategories = getCategoriesFromCSVString(c.getString(c.getColumnIndexOrThrow(COLUMN_CATEGORY_IDS)));
//        String eventLastUpdated = c.getString(c.getColumnIndexOrThrow(COLUMN_LAST_UPDATED));
        String eventImageUrl = c.getString(c.getColumnIndexOrThrow(COLUMN_IMAGE_URL));
        String eventUpdatedAt = c.getString(c.getColumnIndexOrThrow(COLUMN_LAST_UPDATED));

        Event event = new Event(eventId, eventName, eventCategories, eventImageUrl, eventUpdatedAt);

        event.contact = c.getString(c.getColumnIndexOrThrow(COLUMN_CONTACT));
        event.registered = returnBooleanFromInt(c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_REGISTERED)));
        event.team_event = returnBooleanFromInt(c.getInt(c.getColumnIndexOrThrow(COLUMN_IS_TEAM_EVENT)));
        event.team_id = c.getString(c.getColumnIndexOrThrow(COLUMN_TEAM_ID));
        event.eligibility = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_ELIGIBILITY));
        event.judging = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_JUDGING));
        event.prizes = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_PRIZES));
        event.rules = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_RULES));
        event.team_size = c.getInt(c.getColumnIndexOrThrow(COLUMN_TEAM_SIZE));
        event.venue = c.getString(c.getColumnIndexOrThrow(COLUMN_VENUE));
        event.description = c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_DESCRIPTION));
        event.event_date_time = Event.parseStringToDate(c.getString(c.getColumnIndexOrThrow(COLUMN_EVENT_DATE_TIME)));

        return event;
    }

    public boolean updateEvent(Event event) {
        boolean isDeleteSuccessful = deleteEvent(event.id);
        if(!isDeleteSuccessful) {
            return false;
        }
        boolean isInsertSuccessful = insertEvent(event);
        if(isInsertSuccessful) {
            return true;
        } return false;
    }

    public boolean deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int check = db.delete(TABLE_NAME, COLUMN_EVENT_ID + "=?", new String[] {String.valueOf(id)});
        db.close();
        if(check > 0) {
            return true;
        } return false;
    }
}
