package com.iiitd.esya.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class EventListActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nav_contentframe, new EventListFragment());
        }
        setUpToolbar();
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
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
            //startActivity(new Intent(this, EventActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class EventListFragment extends Fragment {

        public EventListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);

            Intent intent = getActivity().getIntent();
            /*if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String eventStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.list_item_event_textview))
                        .setText(eventStr);
            }*/
            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbarTitle();
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setToolbarTitle() {
        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_UID)) {
            Category category = Category.resolveToCategory(intent.getIntExtra(Intent.EXTRA_UID, 0));
            mToolbar.setTitle(category.naturalName + " Events");
        }else{
            mToolbar.setTitle("Events");
        }
    }
}
