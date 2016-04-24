package com.bignerdranch.android.tingle.Controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.tingle.Model.Thing;
import com.bignerdranch.android.tingle.Model.ThingsDB;
import com.bignerdranch.android.tingle.R;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class ListFragment extends Fragment implements Observer {
    //RecyclerView and its adapter.
    private RecyclerView mRecyclerView;
    private ThingAdapter mAdapter;

    //String being used for the search functionality.
    private String mSearchThings = null;

    //Interface to make sure that the activity is doing the calls to new activity
    //and not the fragment itself.
    public interface toActivity {
        public void startViewPagerActivity(UUID uuid, String query); //Start activity for ViewPager.
        public void startSettingsActivity();
    }

    //Whenever ThingsDB changes, update content shown on screen.
    @Override
    public void update(Observable observable, Object data) {
        updateUI();
    }

    //Method to update the content of the adapter.
    private void updateUI() {
        ThingsDB thingsDB = ThingsDB.get(getActivity());
        List<Thing> things = thingsDB.getThingsDB();

        if (mAdapter == null) {
            mAdapter = new ThingAdapter(things);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setThings(things);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Add observer on the ThingsDB.
        ThingsDB.get(getActivity()).addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflater loads the view.
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //Initialize RecyclerView.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Sets data of the adapter to show in the RecyclerView.
        updateUI();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_fragment_menu, menu);

        //Search functionality, with a SearchView.
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_search_things));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }

            //If there is being searched for anything specific, get these specific items from
            //the database else load the whole list into the adapter. Search happens after
            //each keystroke.
            @Override
            public boolean onQueryTextChange(String searchThings) {
                System.out.println(searchThings);

                if (!searchThings.equals("")) {
                    mSearchThings = searchThings;

                    ThingsDB thingsDB = ThingsDB.get(getActivity());
                    List<Thing> things = thingsDB.getThings(searchThings);

                    mAdapter.setThings(things);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mSearchThings = null;

                    ThingsDB thingsDB = ThingsDB.get(getActivity());
                    List<Thing> things = thingsDB.getThingsDB();

                    mAdapter.setThings(things);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    /**
     * Handling click on menu items. Click on search is being handled in onCreateOptionsMenu
     * method.
     *
     * @param item - Menu item to be called.
     * @return - Boolean of if the item menu was called successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Create new thing and add to our database. Start ThingPagerActivity to display
            //the new created thing.
            case R.id.menu_item_new_thing:
                Thing thing = new Thing(UUID.randomUUID(), "", "");
                ThingsDB.get(getActivity()).addThing(thing);

                Intent intent = ThingPagerActivity.newIntent(getActivity(), thing.getId(), "");
                startActivity(intent);
                return true;

            //Start activity from parent activity to change settings.
            case R.id.menu_item_settings:
                ((toActivity) getActivity()).startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Inner class for RecyclerView.
    private class ThingHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Thing mThing;
        private TextView mWhat;
        private TextView mWhere;
        private TextView mDate;

        //Constructor of our thing holder
        public ThingHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mWhat = (TextView) itemView.findViewById(R.id.list_item_thing_what);
            mWhere = (TextView) itemView.findViewById(R.id.list_item_thing_where);
            mDate = (TextView) itemView.findViewById(R.id.list_item_thing_date);
        }

        /**
         * Creates a thing and sets its values. This thing will be displayed in our RecyclerView
         * in its own little view.
         *
         * @param thing - The thing to be displayed in a view.
         */
        public void bindThing(Thing thing) {
            mThing = thing;
            mWhat.setText("What: " + mThing.getWhat());
            mWhere.setText("Where: " + mThing.getWhere());
            mDate.setText("Date added: " + mThing.getDate());
        }

        //Get specific item on click. If there has been searched for specific items,
        //open the item choosen from the specific items.
        @Override
        public void onClick(View v) {
            ((toActivity) getActivity()).startViewPagerActivity(mThing.getId(), mSearchThings);
        }
    }

    //Adapter class for RecyclerView.
    private class ThingAdapter extends RecyclerView.Adapter<ThingHolder> {
        private List<Thing> mThingList;

        public ThingAdapter(List<Thing> things) {
            mThingList = things;
        }

        /**
         * Creates the ViewHolder, that will be used to show our thing.
         *
         * @return - ViewHolder for our thing.
         */
        @Override
        public ThingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);

            return new ThingHolder(view);
        }

        /**
         * Gets the thing at the given position and binds this thing to a ViewHolder in our
         * RecyclerView.
         *
         * @param holder - Holder that we will bind our thing to.
         * @param position - Position of our thing from our collection of things.
         */
        @Override
        public void onBindViewHolder(ThingHolder holder, int position) {
            Thing thing = mThingList.get(position);
            holder.bindThing(thing);
        }

        @Override
        public int getItemCount() {
            return mThingList.size();
        }

        /**
         * Takes a list of things as argument and makes the RecyclerView show only these things.
         * Being used for searching and updating the adapter.
         *
         * @param things - The list of things that we will show in our RecyclerView
         */
        public void setThings(List<Thing> things) {
            mThingList = things;
        }
    }
}
