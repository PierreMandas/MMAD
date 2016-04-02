package com.bignerdranch.android.tingle.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.tingle.R;

import java.util.UUID;

/**
 * Created by Pierre on 21-02-2016.
 */
public class ListActivity extends AppCompatActivity implements ListFragment.toActivity {
    private Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //FragmentManager is being used for dynamically loading fragments.
        FragmentManager fm = getSupportFragmentManager();
        listFragment = fm.findFragmentById(R.id.activity_list_container);

        //If Fragment hasn't been initialized, create a new fragment and
        //load it into the fragment container.
        if (listFragment == null) {
            listFragment = new ListFragment();
            fm.beginTransaction().add(R.id.activity_list_container, listFragment).commit();
        }
    }

    @Override
    public void startAddActivity() {
        Intent i = new Intent(this, ThingAddActivity.class);
        startActivity(i);
    }

    @Override
    public void startViewPagerActivity(UUID uuid, String searchThings) {
        Intent i = ThingPagerActivity.newIntent(this, uuid, searchThings);
        startActivity(i);
    }
}
