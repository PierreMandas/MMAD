package com.bignerdranch.android.tingle.Controller;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pierre on 02-04-2016.
 */
public class OutpanFetcher {
    private static final String TAG = "OutpanFetcher"; //Used to log for debugging.
    private static final String API_KEY = "61a1932ef1fcb73556da8d66902ba939"; //Outpan API key.

    //Makes a connection to the specific url, checks if the connection is okay
    //and begins fetching. Closes connection when done. Timeout has been set to ensure
    //that the user won't wait for way too long time.
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(10000 /* milliseconds */);
            connection.setRequestMethod("GET");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    //Calls getUrlBytes and returns a string of the request.
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    //Method being used to create the JSON Object tree from what we have fetched.
    //Get information from JSON Object tree afterwards.
    public String fetchItem(String barCode) throws JSONException, IOException{
        String itemName = null;

        String url = "https://api.outpan.com/v2/products/" + barCode + "?apikey=" + API_KEY;
        String jsonString = getUrlString(url);
        Log.i(TAG, "Received JSON: " + jsonString);
        JSONObject jsonBody = new JSONObject(jsonString);
        if(jsonBody.getString("name") != null && !jsonBody.getString("name").isEmpty() && !jsonBody.getString("name").equals("null"))
        {
            itemName = jsonBody.getString("name");
        }

        return itemName;
    }
}
