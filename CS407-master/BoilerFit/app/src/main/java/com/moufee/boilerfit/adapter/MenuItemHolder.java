package com.moufee.boilerfit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moufee.boilerfit.R;

public class MenuItemHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public View itemView;
    public ImageView dislikeIcon;


    public MenuItemHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        name = itemView.findViewById(R.id.item_name_text);
        dislikeIcon = itemView.findViewById(R.id.dislike_icon);


    }
}
