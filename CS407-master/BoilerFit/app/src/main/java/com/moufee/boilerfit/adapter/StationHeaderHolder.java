package com.moufee.boilerfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.moufee.boilerfit.R;

public class StationHeaderHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public RecyclerView rv;

    public StationHeaderHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.station_name_text_view);
    }
}
