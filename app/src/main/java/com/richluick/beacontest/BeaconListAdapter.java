package com.richluick.beacontest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

/**
 * Created by q1410049 on 11/9/15.
 */
public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.BeaconViewHolder> {

    private ArrayList<Beacon> mBeacons = new ArrayList<>();

    @Override
    public BeaconListAdapter.BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list_adapter, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BeaconListAdapter.BeaconViewHolder holder, int position) {
        String dist = String.format("%.2f", mBeacons.get(position).getDistance());

        //int instanceId = Integer.decode(String.valueOf(mBeacons.get(position).getId2()));

        holder.beaconName.setText(mBeacons.get(position).getBluetoothName());
        holder.beaconDist.setText(dist + " m");
    }

    public void clearBeacons() {
        mBeacons.clear();
        notifyDataSetChanged();
    }

    public void add(Beacon beacon) {
        mBeacons.add(beacon);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }

    public class BeaconViewHolder extends RecyclerView.ViewHolder {

        public TextView beaconName;
        public TextView beaconDist;

        public BeaconViewHolder(View itemView) {
            super(itemView);

            beaconName = (TextView) itemView.findViewById(R.id.nameText);
            beaconDist = (TextView) itemView.findViewById(R.id.distanceText);
        }
    }
}
