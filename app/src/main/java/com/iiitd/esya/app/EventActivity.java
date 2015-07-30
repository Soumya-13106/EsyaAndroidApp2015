package com.iiitd.esya.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public void register_team(View v)
    {
        if(v.getId() != R.id.register_button) return;
        Toast.makeText(this, "Clicled" + v.getId(), Toast.LENGTH_SHORT).show();

        if(mEvent.team_event)
        {
            RegisterForEventTeam task = new RegisterForEventTeam(this, mEvent, "Wohoo", true) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    Log.v("RegisterTeam", aBoolean + "");
                }
            };
            task.execute();
        }
        else
        {
            RegisterForEventIndividual task = new RegisterForEventIndividual(this, mEvent) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    Log.v("RegisterIndividual", aBoolean + "");
                }
            };
            task.execute();
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
