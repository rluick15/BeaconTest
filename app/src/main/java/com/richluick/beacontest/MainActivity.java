package com.richluick.beacontest;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //populate this list with beacons
        ArrayList<eBeacon> beacons = new ArrayList<>();

        BeaconListAdapter adapter = new BeaconListAdapter(beacons);
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
    }
}