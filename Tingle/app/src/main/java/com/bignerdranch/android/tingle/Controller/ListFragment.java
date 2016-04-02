package com.bignerdranch.android.tingle.Controller;

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
import android.widget.TextView;

import com.bignerdranch.android.tingle.Model.Thing;
import com.bignerdranch.android.tingle.Model.ThingsDB;
import com.bignerdranch.android.tingle.R;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by Pierre on 21-02-2016.
 */
public class ListFragment extends Fragment implements Observer {
    //Database and adapter for loading in data, being used for the RecyclerView.
    private RecyclerView mRecyclerView;
    private ThingAdapter mAdapter;

    private String mSearchThings = null;

    @Override
    public void update(Observable observable, Object data) {
        updateUI();
    }

    public interface toActivity {
        public void startAddActivity(); //Start activity from TingleFragment
        public void startViewPagerActivity(UUID uuid, String query); //Start activity for ViewPager
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ThingsDB.get(getActivity()).addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflater loads the view.
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //Initialize RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_fragment_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_item_search_things));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchThings) {
                if (!searchThings.equals("")) {
                    ThingsDB thingsDB = ThingsDB.get(getActivity());
                    List<Thing> things = thingsDB.getThings(searchThings);

                    mSearchThings = searchThings;

                    mAdapter.setThings(things);
                    mAdapter.notifyDataSetChanged();
                } else {
                    updateUI();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_thing:
                ((toActivity) getActivity()).startAddActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

    //Holder class for RecyclerView
    private class ThingHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Thing mThing;
        private TextView mWhat;
        private TextView mWhere;
        private TextView mDate;

        public ThingHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mWhat = (TextView) itemView.findViewById(R.id.list_item_thing_what);
            mWhere = (TextView) itemView.findViewById(R.id.list_item_thing_where);
            mDate = (TextView) itemView.findViewById(R.id.list_item_thing_date);
        }

        public void bindThing(Thing thing) {
            mThing = thing;
            mWhat.setText("What: " + mThing.getWhat());
            mWhere.setText("Where: " + mThing.getWhere());
            mDate.setText("Date added: " + mThing.getDate());
        }

        @Override
        public void onClick(View v) {
            ((toActivity) getActivity()).startViewPagerActivity(mThing.getId(), mSearchThings);
        }
    }

    //Adapter class for RecyclerView
    private class ThingAdapter extends RecyclerView.Adapter<ThingHolder> {
        private List<Thing> mThingList;

        public ThingAdapter(List<Thing> things) {
            mThingList = things;
        }

        @Override
        public ThingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);

            return new ThingHolder(view);
        }

        @Override
        public void onBindViewHolder(ThingHolder holder, int position) {
            Thing thing = mThingList.get(position);
            holder.bindThing(thing);
        }

        @Override
        public int getItemCount() {
            return mThingList.size();
        }

        public void setThings(List<Thing> things) {
            mThingList = things;
        }
    }
}
