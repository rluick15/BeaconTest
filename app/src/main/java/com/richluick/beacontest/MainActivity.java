package com.richluick.beacontest;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private ArrayList<Beacon> mFoundBeacons = new ArrayList<>();
    private BeaconListAdapter mAdapter;
    private BeaconManager mBeaconManager;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo: check for activated bluetooth, as well as permission

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAdapter = new BeaconListAdapter();
        RecyclerView beaconList = (RecyclerView) findViewById(R.id.beaconList);
        beaconList.setLayoutManager(new LinearLayoutManager(this));
        beaconList.setAdapter(mAdapter);

        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        mBeaconManager.setBackgroundScanPeriod(5000);
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
//        mBeaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        mProgressBar.setVisibility(View.VISIBLE);
        findBeaconsInRegion();
    }

    /*
     * Defines the beacon region to search and then starts searching in that region
     */
    private void findBeaconsInRegion() {
        //todo:should have a swipe refresh or toolbar refresh button to call this method again

        mAdapter.clearBeacons();
        mFoundBeacons.clear();

        final Region region = new Region("ibeacon-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);

        //stop ranging after 5s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);

                try {
                    mBeaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
        for(final Beacon beacon : beacons) {
            if(!isBeaconAlreadyFound(beacon, mFoundBeacons)) {
                mFoundBeacons.add(beacon);

                runOnUiThread(new Runnable() {
                    public void run() {
                        //todo: here we will make the api call for each new beacon found
                        //todo: a new "Customer" object will be created and added to the adapter
                        //todo: adapter will be switched to take customer objects
                        mProgressBar.setVisibility(View.GONE);
                        mAdapter.add(beacon);
                    }
                });
            }
        }
    }

    /*
     * Checks if the beacon has already been found during the scan
     *
     * @param beacon the beacon we are checking
     * @param foundBeacons the list of beacons which have been found
     * @return the boolean represeting if the beacon has been found
     */
    private boolean isBeaconAlreadyFound(Beacon beacon, ArrayList<Beacon> foundBeacons) {
        boolean isFound = false;

        for(int i = 0; i < foundBeacons.size(); i++) {
            if(beacon.getId1().equals(foundBeacons.get(i).getId1())) {
                isFound = true;
                break;
            }
        }

        return isFound;
    }
}