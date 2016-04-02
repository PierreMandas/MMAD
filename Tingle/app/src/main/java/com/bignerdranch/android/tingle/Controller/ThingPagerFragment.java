package com.bignerdranch.android.tingle.Controller;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.tingle.Model.Thing;
import com.bignerdranch.android.tingle.Model.ThingsDB;
import com.bignerdranch.android.tingle.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingPagerFragment extends Fragment {
    private static final String TAG = "ThingPagerFragment";
    private static final String ARG_THING_ID = "thing_id";

    private Thing mThing;
    private EditText mWhat;
    private EditText mWhere;
    private TextView mBarcode;

    public static ThingPagerFragment newInstance(UUID thingId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_THING_ID, thingId);

        ThingPagerFragment fragment = new ThingPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID thingId = (UUID) getArguments().getSerializable(ARG_THING_ID);
        mThing = ThingsDB.get(getContext()).get(thingId);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_thing, container, false);

        mWhat = (EditText) v.findViewById(R.id.thing_what);
        mWhat.setText(mThing.getWhat());
        mWhere = (EditText) v.findViewById(R.id.thing_where);
        mWhere.setText(mThing.getWhere());
        mBarcode = (TextView) v.findViewById(R.id.Bar_code);
        mBarcode.setText(mThing.getBarcode());

        try {
            Button scanner = (Button) v.findViewById(R.id.scanner_BAR);
            scanner.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                }

            });

        } catch (ActivityNotFoundException anfe) {
            Log.e("onCreateView", "Scanner Not Found", anfe);
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.thing_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_update_thing:
                if ((mWhat.getText().length() > 0) && (mWhere.getText().length() > 0)) {
                    mThing.setWhat(mWhat.getText().toString());
                    mThing.setWhere(mWhere.getText().toString());

                    if (mBarcode.getText().length() > 0) {
                        mThing.setBarcode(mBarcode.getText().toString());
                    }

                    ThingsDB.get(getActivity()).update(mThing);

                    Toast toast = Toast.makeText(getActivity(), "Item has been updated!", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "What and where must contain something!", Toast.LENGTH_LONG);
                    toast.show();
                }

                return true;
            case R.id.menu_item_delete_thing:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Deletion");
                   alert.setMessage("Are you sure you want to delete this item?");

                   alert.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           ThingsDB.get(getActivity()).remove(mThing.getId());
                           getActivity().finish();
                           dialog.dismiss();
                       }
                   });

                    alert.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan
                //If barcode, set text of textview to content of barcode
                mBarcode.setText(contents);

                if(isOnline()) {
                    new FetchOutpanTask().execute(mBarcode.getText().toString());
                } else {
                    Toast toast = Toast.makeText(getActivity(), "No network connection available!", Toast.LENGTH_LONG);
                    toast.show();
                }

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    //Check if WIFI or mobile connection is active.
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class FetchOutpanTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String barCode = params[0];
            String name = null;
            String exception = null;

            try {
                name = new OutpanFetcher().fetchItem(barCode);
            } catch (JSONException je) {
                exception = "FailedJSON";
                Log.e(TAG, "Failed to parse JSON", je);
            } catch (IOException ioe){
                exception = "FailedIO";
                Log.e(TAG, "Failed to fetch item", ioe);
            }

            return new String[]{name, exception};
        }

        @Override
        protected void onPostExecute(String[] args) {
            String name = args[0];
            String exception = args[1];

            if(exception != null && !exception.isEmpty() && exception.equals("FailedJSON")) {
                Toast toast = Toast.makeText(getActivity(), "Failed to parse as JSON.", Toast.LENGTH_LONG);
                toast.show();
                return;
            } else if(exception != null && !exception.isEmpty() && exception.equals("FailedIO") ) {
                Toast toast = Toast.makeText(getActivity(), "Unable to retrieve web page. URL may be invalid!", Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if(name != null && !name.isEmpty() && !name.equals("null")) {
                mWhat.setText(name);
            } else {
                Toast toast = Toast.makeText(getActivity(), "No name found for the product. Please provide a name.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
