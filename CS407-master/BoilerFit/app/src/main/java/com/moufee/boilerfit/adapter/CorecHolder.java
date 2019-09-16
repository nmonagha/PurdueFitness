package com.moufee.boilerfit.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moufee.boilerfit.R;

public class CorecHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public ImageView image;
    public TextView people;
    public ProgressBar progressBar;
    public CorecHolder(View itemView, final CorecAdapter.OnItemClickListener lis) {
        super(itemView);
        name = itemView.findViewById(R.id.loc);
        image = itemView.findViewById(R.id.image_busy);
        people = itemView.findViewById(R.id.people);
        progressBar = itemView.findViewById(R.id.capacity);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lis !=null){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        lis.onItemClick(pos);
                    }
                }
            }
        });

    }
}
