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

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Beacon> mBeacons = new ArrayList<>();
    private BeaconListAdapter mAdapter;

    MessageListener mMessageListener = new MessageListener() {
        // Called each time a new message is discovered nearby.
        @Override
        public void onFound(Message message) {
            Log.i("BeaconFound", "Found message: " + message);

            Beacon beacon = new Beacon();
            beacon.setBeaconName(message.getNamespace());
            mAdapter.add(beacon);
        }

        // Called when a message is no longer nearby.
        @Override
        public void onLost(Message message) {
            Log.i("BeaconLost", "Lost message: " + message);

            Beacon beacon = new Beacon();
            beacon.setBeaconName(message.getNamespace());
            mAdapter.remove(beacon);
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new BeaconListAdapter(mBeacons);
        RecyclerView beaconList = (RecyclerView) findViewById(R.id.beaconList);
        beaconList.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
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
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        } else {
            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, Strategy.BLE_ONLY)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}