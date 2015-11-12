package com.richluick.beacontest;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

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

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //populate this list with beacons
        ArrayList<eBeacon> beacons = new ArrayList<>();

        BeaconListAdapter adapter = new BeaconListAdapter(beacons);
        mAdapter = new BeaconListAdapter(mBeacons);
        RecyclerView beaconList = (RecyclerView) findViewById(R.id.beaconList);
        beaconList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon: beacons) {
            //if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
            // This is a Eddystone-UID frame
            Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();
            Log.d("RangingActivity", "I see a beacon transmitting namespace id: " + namespaceId +
                    " and instance id: " + instanceId +
                    " approximately " + beacon.getBluetoothName() + " meters away.");
            runOnUiThread(new Runnable() {
                public void run() {
                    ((TextView) MainActivity.this.findViewById(R.id.message)).setText("Hello world, and welcome to Eddystone!");
                }
            });
            //}
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