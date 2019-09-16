package com.moufee.boilerfit.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moufee.boilerfit.R;

import java.util.List;

/**
 * Adapter used to show a simple grid of products.
 */
public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private List<String> productList;
    private OnItemClickListener mL;

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
    public void onSetItemClickListener(OnItemClickListener ls) {
        mL = ls;
    }
    public ProductCardRecyclerViewAdapter(List<String> productList) {
        this.productList = productList;

    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_box, parent, false);

        return new ProductCardViewHolder(layoutView,mL);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        if (productList != null && position < productList.size()) {

            holder.productTitle.setText(productList.get(position));
            try {
                ImageView   i = (ImageView)holder.image;

                switch (productList.get(position)) {
                    case "Badminton":
                        i.setImageResource(R.drawable.badminton);
                        break;
                    case "Soccer":
                        i.setImageResource(R.drawable.soccer);
                        break;
                    case "Jogging":
                        i.setImageResource(R.drawable.corec1);
                        break;
                    case "Water Sports":
                        i.setImageResource(R.drawable.swimming);
                        break;
                    case "Volleyball":
                        i.setImageResource(R.drawable.volleyball);
                        break;
                    case "Basketball":
                        i.setImageResource(R.drawable.basketball);
                        break;
                    case "Racquetball":
                        i.setImageResource(R.drawable.racquetball);
                        break;
                    case "Climbing":
                        i.setImageResource(R.drawable.climbing);
                        break;
                    case "Table Tennis":
                        i.setImageResource(R.drawable.table_tennis);
                        break;
                    case "Wrestling":
                        i.setImageResource(R.drawable.wrestling);
                        break;
                    case "Group Exercise":
                        i.setImageResource(R.drawable.dance);
                        break;
                    case "Windsor":
                        i.setImageResource(R.drawable.winsdor);
                        break;
                    case "Wiley":
                        i.setImageResource(R.drawable.wiley);
                        break;
                    case "Hillenbrand":
                        i.setImageResource(R.drawable.hilly);
                        break;
                    case "Ford":
                        i.setImageResource(R.drawable.ford);
                        break;
                    case "Earhart":
                        i.setImageResource(R.drawable.earhart);
                        break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void onClick() {

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
