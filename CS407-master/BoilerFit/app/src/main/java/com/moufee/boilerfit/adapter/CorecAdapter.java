package com.moufee.boilerfit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.corec.CorecFacility;

import java.util.ArrayList;


/**
 * Adapter used to show a simple grid of products.
 */
public class CorecAdapter extends RecyclerView.Adapter<CorecHolder> {
    private Context context;
    public ArrayList<String> items = new ArrayList<>();
    private ArrayList<CorecFacility>  stat;
    private CorecAdapter.OnItemClickListener mL;


    public interface OnItemClickListener {
        void onItemClick(int pos);
    }
    public void onSetItemClickListener(CorecAdapter.OnItemClickListener ls) {
        mL = ls;
    }

    public CorecAdapter(ArrayList<CorecFacility> stat) {
        this.stat = stat;
    }


    @NonNull
    @Override
    public CorecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.corec_card, parent, false);
        layoutView.setBackgroundColor(Color.WHITE);
        return new CorecHolder(layoutView,mL);
    }

    @Override
    public void onBindViewHolder(@NonNull CorecHolder holder, int position) {
        CorecFacility fac = stat.get(position);
        holder.name.setText(fac.getLocationName());

        holder.progressBar.setMax(50);
        holder.progressBar.setProgress(fac.getCount());


        holder.people.setText(fac.getCount() + "");


        if (fac.getDisplayName() != null && fac.getDisplayName().trim().equals("Open Rec")) {
            holder.image.setImageResource(R.drawable.box_corec_green);
        }

    }



    @Override
    public int getItemCount() {
        return stat.size();
    }
}
