package com.bignerdranch.android.tingle;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pierre on 02-04-2016.
 */
public class OutpanFetcher {
    private static final String TAG = "OutpanFetcher";
    private static final String API_KEY = "61a1932ef1fcb73556da8d66902ba939";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
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

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public String fetchItem(String barCode) {
        String itemName = null;

        try {
            String url = "https://api.outpan.com/v2/products/" + barCode + "?apikey=" + API_KEY;
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItem(itemName, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe){
            Log.e(TAG, "Failed to fetch item", ioe);
        }

        return itemName;
    }

    private void parseItem(String itemName, JSONObject jsonBody) throws IOException, JSONException{
        if(jsonBody.getString("name") != null && !jsonBody.getString("name").isEmpty() && !jsonBody.getString("name").equals("null"))
        {
            itemName = jsonBody.getString("name");
        }
    }
}
