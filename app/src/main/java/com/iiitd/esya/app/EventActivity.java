package com.iiitd.esya.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;


public class EventActivity extends ActionBarActivity {


    private void tempMethod(){

        class FetchBasicEventDetails extends AsyncTask<Void, Void, Event[]>{
            @Override
            protected void onPostExecute(Event[] events) {
//                super.onPostExecute(events);
                Log.v("EVENTS FETCHED: ", Arrays.deepToString(events));
            }

            @Override
            protected Event[] doInBackground(Void... voids) {
                Log.v("Task", "Fetching task started");
                return APIDataFetcher.fetchBasicAllEvents();
            }
        }

        FetchBasicEventDetails fetchBasicEventDetails = new FetchBasicEventDetails();
        fetchBasicEventDetails.execute();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        this.tempMethod();
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
