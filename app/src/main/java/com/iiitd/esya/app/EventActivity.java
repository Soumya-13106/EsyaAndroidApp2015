package com.iiitd.esya.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class EventActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    private Event mEvent;

    private String EVENT_SHARE_PART1 = "Hey! Go checkout ";
    private String EVENT_SHARE_PART2 = " at http://esya.iiitd.edu.in/ ";
    private String EVENT_SHARE_HASHTAG = "#Esya2015";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
//        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
//        setSupportActionBar(toolbar);
//        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


//        ImageView header = (ImageView) findViewById(R.id.header);

        mEvent = DataHolder.EVENTS.get(getIntent().getIntExtra(Intent.EXTRA_UID, 1));
        Bitmap bitmap = null;



//        collapsingToolbar.setTitle(mEvent.name);

        if (mEvent.image_url != null)
        {
            bitmap = mEvent.getCacheImage(Event.getImageNameFromUrl(mEvent.image_url), this);
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.esyalogo);
        }

//        ((ImageView)findViewById(R.id.header)).setImageBitmap(bitmap);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
//                collapsingToolbar.setContentScrimColor(mutedColor);
            }
        });
    }

    // ask about team
    // update event
    // update on db
    // update FAB
    public void register_team(View v) {
        if (v.getId() != R.id.register_button) return;

        if (mEvent.registered) {
            Toast.makeText(this, "Already registered.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean completed_profile = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_profile_complete), false);

        if (!completed_profile) {
            Toast.makeText(this, "Please update your profile before registering",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class).
                    putExtra("UpdateProfile", true));
            return;
        }

        final Activity activity = this;
        final FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.register_button);

        if (mEvent.team_event) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.help_text_team_event_initial_dialog_box_title))
                    .setMessage(getString(R.string.help_text_team_event_initial_dialog_box_message))
                    .setPositiveButton(getText(R.string.help_text_team_event_new_team), new DialogInterface.OnClickListener() {

                        // CREATE A NEW TEAM
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlertDialog.Builder ask = new AlertDialog.Builder(activity);
                            ask.setTitle(getString(R.string.help_text_new_team_title));
                            ask.setMessage(getString(R.string.help_text_new_team_message));
                            ask.setCancelable(true);

                            final EditText input = new EditText(activity);
                            ask.setView(input);

                            ask.setPositiveButton(getString(R.string.help_text_new_team_button), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Make API call
                                    RegisterForEventTeam task = new RegisterForEventTeam(
                                            activity, mEvent, input.getText().toString(), true) {
                                        @Override
                                        protected void onPostExecute(String team_code) {
                                            Log.v("RegisterNewTeam", "Team code: " + team_code);
                                            if (team_code.equals("Failed")) {
                                                Toast.makeText(activity, "Unable to register team",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                event.registered = true;
                                                event.team_id = team_code;
                                                new AlertDialog.Builder(activity)
                                                        .setTitle("Successfully Registered")
                                                        .setMessage("Your team code is: " + team_code + "\nDo you want to add event to calender?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                addEvent(event.name, event.venue, event.event_date_time, null);
                                                                return;
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                return;
                                                            }
                                                        })
                                                        .show();
                                                DataHolder.CURRENT_EVENT_FRAGMENT.show_new = false;
                                                Log.v("Bleh", event.name + event.event_date_time + "Registered");
                                                Event.updateEventInDB(event, event, activity);
                                                fab.setRippleColor(Color.RED);
                                            }
                                        }
                                    };
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            });
                            ask.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            ask.show();
                        }
                    })
                    .setNegativeButton(getText(R.string.help_text_team_event_join_team), new DialogInterface.OnClickListener() {

                        // JOIN A TEAM
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlertDialog.Builder ask = new AlertDialog.Builder(activity);
                            ask.setTitle(getString(R.string.help_text_join_team_title));
                            ask.setMessage(getString(R.string.help_text_join_team_message));
                            ask.setCancelable(true);

                            final EditText input = new EditText(activity);
                            ask.setView(input);

                            ask.setPositiveButton(getString(R.string.help_text_join_team_button), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Make API call
                                    RegisterForEventTeam task = new RegisterForEventTeam(
                                            activity, mEvent, input.getText().toString(), false) {

                                        @Override
                                        protected void onPostExecute(String team_code) {
                                            Log.v("RegisterJoinTeam", "Team code: " + team_code);
                                            if (team_code.equals("Failed")) {
                                                Toast.makeText(activity, "Unable to join team",
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                event.registered = true;
                                                event.team_id = team_code;
                                                new AlertDialog.Builder(activity)
                                                        .setTitle("Joined Team")
                                                        .setMessage("You have successfully joined the team." + "\nDo you want to add event to calender?")
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                addEvent(event.name, event.venue, event.event_date_time, null);
                                                                return;
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                return;
                                                            }
                                                        })
                                                        .show();
                                                DataHolder.CURRENT_EVENT_FRAGMENT.show_new = false;
                                                Event.updateEventInDB(event, event, activity);
                                                fab.setRippleColor(Color.RED);
                                            }
                                        }
                                    };
                                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            });
                            ask.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            });
                            ask.show();
                        }
                    })
//                .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(true)
                    .show();
        } else {
            RegisterForEventIndividual task = new RegisterForEventIndividual(this, mEvent) {
                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    Log.v("RegisterIndividual", success + "");
                    if (success) {
                        event.registered = true;
                        new AlertDialog.Builder(activity)
                                .setTitle("Registered.")
                                .setMessage("You have been successfully registered." + "\nDo you want to add event to calender?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        addEvent(event.name, event.venue, event.event_date_time, null);
                                        return;
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                                .show();
                        Event.updateEventInDB(event, event, activity);
                        fab.setRippleColor(Color.RED);
                    } else {
                        Toast.makeText(activity,
                                "Could not register you for the event", Toast.LENGTH_LONG).show();
                    }
                }
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
    public void addEvent(String title, String location, Date begin, Date end) {
        Log.v("Bleh", title + location + begin.getTime() + "Registered");
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin.getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_event, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_share_event);
//        ShareActionProvider mShareActionProvider =
//                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//
//        if (mShareActionProvider != null ) {
//            mShareActionProvider.setShareIntent(createShareEsyaIntent(mEvent));
//        } else {
//            Log.d("LOG", "Share Action Provider is null?");
//        }
        return true;
    }

    private Intent createShareEsyaIntent(Event event) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                EVENT_SHARE_PART1 + event.name + EVENT_SHARE_PART2 + EVENT_SHARE_HASHTAG);
        return shareIntent;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }
}
