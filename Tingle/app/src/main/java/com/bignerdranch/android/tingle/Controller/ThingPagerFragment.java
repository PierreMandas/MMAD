package com.bignerdranch.android.tingle.Controller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.tingle.Model.Thing;
import com.bignerdranch.android.tingle.Model.ThingsDB;
import com.bignerdranch.android.tingle.R;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingPagerFragment extends Fragment {
    //Tag for debugging and id of item to be loaded into this fragment.
    private static final String TAG = "ThingPagerFragment";
    private static final String ARG_THING_ID = "thing_id";
    private static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_BAR = 1;

    //Information about the current connection, according to preferences of the user.
    private static final String WIFI = "Wi-Fi";
    private static final String ANY = "AnyConnection";
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    private static String sPref = null;

    //Widget and other fields.
    private Thing mThing;
    private File mPhotoFile;
    private EditText mWhat;
    private EditText mWhere;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private TextView mBarcode;

    //Creates a new fragment and returns the fragment.
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

        //Get thing with the thingId given as argument to this fragment.
        UUID thingId = (UUID) getArguments().getSerializable(ARG_THING_ID);
        mThing = ThingsDB.get(getContext()).get(thingId);
        mPhotoFile = ThingsDB.get(getActivity()).getPhotoFile(mThing);

        setHasOptionsMenu(true);

        //Being used for the network preferences of the user.
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sPref = sharedPrefs.getString("listPreference", "Wi-Fi");
        updateConnectedFlags();
    }

    //Method to update the flags used to check connection type.
    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    //Method to update the photoview.
    private void updatePhotoview() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_thing, container, false);

        PackageManager packageManager = getActivity().getPackageManager();

        mWhat = (EditText) v.findViewById(R.id.thing_what);
        mWhat.setText(mThing.getWhat());
        mWhere = (EditText) v.findViewById(R.id.thing_where);
        mWhere.setText(mThing.getWhere());


        mPhotoButton = (ImageButton) v.findViewById(R.id.thing_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = (mPhotoFile != null && captureImage.resolveActivity(packageManager) != null);
        mPhotoButton.setEnabled(canTakePhoto);

        if(canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = (ImageView) v.findViewById(R.id.thing_photo);
        updatePhotoview();

        mBarcode = (TextView) v.findViewById(R.id.Bar_code);
        mBarcode.setText(mThing.getBarcode());

        //Scanner. This scanner is using the Barcode Scanner from the ZXing team.
        try {
            Button scanner = (Button) v.findViewById(R.id.scanner_BAR);
            scanner.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, REQUEST_BAR);
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

    //Handles click on menu items.
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

    //Being used to return the result of using our barcode scanner.
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_PHOTO) {
            if(resultCode == getActivity().RESULT_OK) {
                updatePhotoview();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Picture taking was cancelled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        if(requestCode == REQUEST_BAR) {
            if(resultCode == getActivity().RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan
                //If barcode, set text of textview to content of barcode
                mBarcode.setText(contents);

                if(((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) || ((sPref.equals(WIFI)) && (wifiConnected))) {
                    new FetchOutpanTask().execute(mBarcode.getText().toString());
                } else {
                    Toast toast = Toast.makeText(getActivity(), "No network connection available!", Toast.LENGTH_LONG);
                    toast.show();
                }

            } else if(resultCode == getActivity().RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was cancelled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    //Ensures that the screen is locked, whenever we are fetching from the internet.
    //Without this method, rotation on fetching of data would result in a crash.
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    //Unlocks the screen when we are done fetching data
    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    //This inner class uses our OutpanFetcher to fetch data.
    private class FetchOutpanTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog;

        //Before execute of the background thread, make ProgressDialog to show
        //the user that work is being done.
        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Receiving data from Outpan!");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        //Execute background thread
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

        //After executing background thread, handle exceptions if any, else do the necessary
        //work to get done.
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

            dialog.dismiss();
            unlockScreenOrientation();
        }
    }
}
