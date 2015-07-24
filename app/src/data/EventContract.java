package com.iiitd.esya.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.lang.Integer;
import java.lang.String;

/**
 * Created by soummyaah on 24/7/15.
 */

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.iiitd.esya.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EVENT = "event";

    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class EventEntry extends BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT ;

        public static final String TABLE_NAME = "event";
        public static final Integer COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_CONTACT = "contact";
        public static final Integer COLUMN_IS_REGISTERED = "is_registered";
        public static final Integer COLUMN_IS_TEAM_EVENT = "is_team_event";
        public static final Integer COLUMN_TEAM_ID = "team_id";
        public static final String COLUMN_EVENT_ELIBILITY = "event_eligibility";
        public static final String COLUMN_EVENT_JUDGING = "event_judging";
        public static final String COLUMN_EVENT_PRIZES = "event_prizes";
        public static final String COLUMN_EVENT_RULES = "event_rules";
        public static final Integer COLUMN_TEAM_SIZE = "team_size";
        public static final String COLUMN_VENUE = "venue";
        public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    }
}
