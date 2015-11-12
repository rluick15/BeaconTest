package com.richluick.beacontest;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private ArrayList<Beacon> mBeacons = new ArrayList<>();
    private BeaconListAdapter mAdapter;

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo: check for activated bluetooth, as well as permission

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new BeaconListAdapter(mBeacons);
        RecyclerView beaconList = (RecyclerView) findViewById(R.id.beaconList);
        beaconList.setLayoutManager(new LinearLayoutManager(this));
        beaconList.setAdapter(mAdapter);
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
        Region region = new Region("ibeacon-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
        //todo: might be good to create custom beacon class and use a list of that

        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.updateBeaconList((ArrayList<Beacon>) beacons);
            }
        });
    }
}