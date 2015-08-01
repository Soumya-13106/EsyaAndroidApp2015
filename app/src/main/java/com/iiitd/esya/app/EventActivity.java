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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class EventActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    private Event mEvent;

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
                    R.drawable.logo);
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
    public void register_team(View v)
    {
        if(v.getId() != R.id.register_button) return;

        if(mEvent.registered)
        {
            Toast.makeText(this, "Already registered.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Activity activity = this;
        final FloatingActionButton fab = (FloatingActionButton)activity.findViewById(R.id.register_button);

        if(mEvent.team_event)
        {
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
                                        if (team_code.equals("Failed"))
                                        {
                                            Toast.makeText(activity, "Unable to register team",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(activity)
                                                    .setTitle("Team Registered")
                                                    .setMessage("Your team code is: " + team_code)
                                                    .setCancelable(true)
                                                    .show();
                                            event.registered = true;
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
                                        if (team_code.equals("Failed"))
                                        {
                                            Toast.makeText(activity, "Unable to join team",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(activity)
                                                    .setTitle("Joined Team")
                                                    .setMessage("You have successfully joined the team.")
                                                    .setCancelable(true)
                                                    .show();
                                            event.registered = true;
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
        }
        else
        {
            RegisterForEventIndividual task = new RegisterForEventIndividual(this, mEvent) {
                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    Log.v("RegisterIndividual", success + "");
                    if(success)
                    {
                        new AlertDialog.Builder(activity)
                                .setTitle("Registered.")
                                .setMessage("You have been successfully registered.")
                                .setCancelable(true)
                                .show();
                        event.registered = true;
                        Event.updateEventInDB(event, event, activity);
                        fab.setRippleColor(Color.RED);
                    }
                    else
                    {
                        Toast.makeText(activity,
                                "Could not register you for the event", Toast.LENGTH_LONG).show();
                    }
                }
            };
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }
}
