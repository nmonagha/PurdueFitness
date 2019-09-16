package com.moufee.boilerfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moufee.boilerfit.R;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {

    public TextView productTitle;
    public ImageView image;

    public ProductCardViewHolder(View itemView, final ProductCardRecyclerViewAdapter.OnItemClickListener lis) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.product_title);
        image = itemView.findViewById(R.id.product_image);

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
