package com.bignerdranch.android.tingle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingAddActivity extends AppCompatActivity {
    private Fragment addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_add);

        //FragmentManager is being used for dynamically loading fragments.
        FragmentManager fm = getSupportFragmentManager();
        addFragment = fm.findFragmentById(R.id.activity_thing_add_container);

        //If Fragment hasn't been initialized, create a new fragment and
        //load it into the fragment container.
        if (addFragment == null) {
            addFragment = new ThingAddFragment();
            fm.beginTransaction().add(R.id.activity_thing_add_container, addFragment).commit();
        }

        //Only show keyboard whenver user clicks on textview in the fragment
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
