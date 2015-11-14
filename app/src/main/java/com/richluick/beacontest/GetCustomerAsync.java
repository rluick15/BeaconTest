package com.richluick.beacontest;

import android.os.AsyncTask;

import org.altbeacon.beacon.Beacon;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rluick on 11/14/2015.
 *
 * Handles the request to the server for the customer data and returns it to the calling activity
 */
public class GetCustomerAsync extends AsyncTask<Void, Void, JSONObject> {

    private Beacon mBeacon;
    private OnTaskCompletedListener mListener;

    private static final String BASE_URL = ""; //todo: put base url here

    public GetCustomerAsync(Beacon beacon, OnTaskCompletedListener listener) {
        this.mBeacon = beacon;
        this.mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        String customerId = String.valueOf(Integer.decode(String.valueOf(mBeacon.getId1())));

        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        JSONObject jsonObject = null;

        try {
            URL url = new URL(BASE_URL); //todo: append customerId
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            jsonObject = new JSONObject(result.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        //todo: Parse JSON here

        Customer customer = new Customer();
        customer.setDistance(String.format("%.2f", mBeacon.getDistance()));

        mListener.onTaskCompleted(customer);
    }

    public interface OnTaskCompletedListener {
        void onTaskCompleted(Customer customer);
    }
}
