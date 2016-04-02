package com.bignerdranch.android.tingle;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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

import java.util.UUID;

/**
 * Created by Pierre on 27-03-2016.
 */
public class ThingAddFragment extends Fragment {
    private ThingsDB sThingsDB;
    private EditText mWhat;
    private EditText mWhere;
    private TextView mBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_thing, container, false);

        mWhat = (EditText) v.findViewById(R.id.thing_what);
        mWhere = (EditText) v.findViewById(R.id.thing_where);
        mBarcode = (TextView) v.findViewById(R.id.Bar_code);

        sThingsDB = ThingsDB.get(getActivity());

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
        inflater.inflate(R.menu.thing_add_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_thing:
                if ((mWhat.getText().length() > 0) && (mWhere.getText().length() > 0)) {
                    Thing thing = new Thing(UUID.randomUUID(), mWhat.getText().toString().trim(), mWhere.getText().toString().trim());

                    if(mBarcode.getText().length() > 0) {
                        thing.setBarcode(mBarcode.getText().toString());
                    }

                    sThingsDB.addThing(thing);

                    getActivity().finish();
                } else {
                    Toast toast = Toast.makeText(getActivity(), "What and Where must contain words", Toast.LENGTH_LONG);
                    toast.show();
                }
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

                //Toast toast = Toast.makeText(getActivity(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                //toast.show();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
