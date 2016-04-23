package com.bignerdranch.android.tingle.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bignerdranch.android.tingle.Model.Thing;
import com.bignerdranch.android.tingle.Model.ThingsDB;
import com.bignerdranch.android.tingle.R;

import java.util.List;
import java.util.UUID;

public class ThingPagerActivity extends AppCompatActivity {
    //Strings being used to get the specific item for the ThingPagerFragment and
    //to specify which items the viewpager will look at.
    private static final String EXTRA_THING_ID =
            "com.bignerdranch.android.tingle.thing_id";
    private static final String EXTRA_SEARCH_THINGS =
            "com.bignerdranch.android.tingle.search_things";

    //ViewPager and our thing list.
    private ViewPager mViewPager;
    private List<Thing> mThings;

    //Creates a new intent with extras.

    /**
     * Creates a new intent with extras, that will be used find a specific thing and
     * search for specific things.
     *
     * @param thingId - ID given to find a specific thing.
     * @param searchThings - String being used to find specific things from searching.
     * @return - Returns an intent with the extras put into it.
     */
    public static Intent newIntent(Context packageContext, UUID thingId, String searchThings) {
        Intent intent = new Intent(packageContext, ThingPagerActivity.class);
        intent.putExtra(EXTRA_THING_ID, thingId);
        intent.putExtra(EXTRA_SEARCH_THINGS, searchThings);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_pager);

        //Get the extra values from the intent.
        UUID thingId = (UUID) getIntent().getSerializableExtra(EXTRA_THING_ID);
        String searchThings = getIntent().getStringExtra(EXTRA_SEARCH_THINGS);

        //Initialize our ViewPager
        mViewPager = (ViewPager) findViewById(R.id.activity_thing_pager_view_pager);

        //If we have searched for any specific items, get these items else get every
        //item in our database, and show these.
        if(searchThings != null && !searchThings.isEmpty() && !searchThings.equals("null"))
        {
            mThings = ThingsDB.get(this).getThings(searchThings);
        } else
        {
            mThings = ThingsDB.get(this).getThingsDB();
        }

        //Fragment manager being used by the ViewPager adapter.
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Adapter of our viewpager. Uses the FragmentManager to load in a fragment displaying
        //the specific thing.
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

        //Find specific item with the thing id given to this activity and
        //set the current viewpage item to this specific item.
        for (int i = 0; i < mThings.size(); i++) {
            if (mThings.get(i).getId().equals(thingId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //Only show keyboard whenver user clicks on a TextView in the fragment.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
