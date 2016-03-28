package com.bignerdranch.android.tingle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by Pierre on 26-03-2016.
 */
public class ThingPagerActivity extends AppCompatActivity {
    private static final String EXTRA_THING_ID =
            "com.bignerdranch.android.tingle.thing_id";

    private ViewPager mViewPager;
    private List<Thing> mThings;

    public static Intent newIntent(Context packageContext, UUID thingId) {
        Intent intent = new Intent(packageContext, ThingPagerActivity.class);
        intent.putExtra(EXTRA_THING_ID, thingId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_pager);

        UUID thingId = (UUID) getIntent().getSerializableExtra(EXTRA_THING_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_thing_pager_view_pager);

        mThings = ThingsDB.get(this).getThingsDB();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Thing thing = mThings.get(position);
                return ThingPagerFragment.newInstance(thing.getId());
            }

            @Override
            public int getCount() {
                return mThings.size();
            }
        });

        for (int i = 0; i < mThings.size(); i++) {
            if (mThings.get(i).getId().equals(thingId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //Only show keyboard whenver user clicks on textview in the fragment
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
