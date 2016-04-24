package com.bignerdranch.android.tingle.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.tingle.Network.SettingsActivity;
import com.bignerdranch.android.tingle.R;

import java.util.UUID;

public class ListActivity extends AppCompatActivity implements ListFragment.toActivity {
    private Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //FragmentManager is being used to dynamically load fragments.
        FragmentManager fm = getSupportFragmentManager();
        listFragment = fm.findFragmentById(R.id.activity_list_container);

        //If the fragment hasn't been initialized, create a new fragment and
        //load it into the fragment container.
        if (listFragment == null) {
            listFragment = new ListFragment();
            fm.beginTransaction().add(R.id.activity_list_container, listFragment).commit();
        }
    }

    /**
     * Starts the ViewPager activity. The ListFragment makes a call to this method.
     * This ensures that the creation of our ThingPagerActivity happens through the parent
     * activity and not through the fragment.
     *
     * @param uuid - UUID of the thing to be shown through our ThingPagerActivity.
     * @param searchThings - String used from search to specify which items that ThingPagerActivity
     *                     will display.
     */
    @Override
    public void startViewPagerActivity(UUID uuid, String searchThings) {
        Intent i = ThingPagerActivity.newIntent(this, uuid, searchThings);
        startActivity(i);
    }

    /**
     * Starts the settings activity. The ListFragment makes a call to this method.
     * Same purpose as with the startViewPagerActivity method.
     */
    @Override
    public void startSettingsActivity() {
        Intent i = SettingsActivity.newIntent(this);
        startActivity(i);
    }
}
