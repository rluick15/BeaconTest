package com.richluick.beacontest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q1410049 on 11/9/15.
 */
public class BeaconListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Beacon> mBeacons;

    public BeaconListAdapter(ArrayList<Beacon> beaconList) {
        this.mBeacons = beaconList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list_adapter, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BeaconViewHolder beaconViewHolder = (BeaconViewHolder) holder;

        beaconViewHolder.beaconName.setText(mBeacons.get(position).getBeaconName());
        beaconViewHolder.beaconDist.setText(mBeacons.get(position).getBeaconDistance());
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }

    public void add(Beacon beacon) {
        if(!mBeacons.contains(beacon)) {
            mBeacons.add(beacon);
            notifyDataSetChanged();
        }
    }

    public void remove(Beacon beacon) {
        if(mBeacons.contains(beacon)) {
            mBeacons.remove(beacon);
            notifyDataSetChanged();
        }
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
