package com.richluick.beacontest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    MessageListener mMessageListener = new MessageListener() {
        // Called each time a new message is discovered nearby.
        @Override
        public void onFound(Message message) {
            Log.i("BeaconFound", "Found message: " + message);
        }

        // Called when a message is no longer nearby.
        @Override
        public void onLost(Message message) {
            Log.i("BeaconLost", "Lost message: " + message);
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        //populate this list with beacons
        ArrayList<Beacon> beacons = new ArrayList<>();

        BeaconListAdapter adapter = new BeaconListAdapter(beacons);
        RecyclerView beaconList = (RecyclerView) findViewById(R.id.beaconList);
        beaconList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, Strategy.BLE_ONLY)
                .setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}