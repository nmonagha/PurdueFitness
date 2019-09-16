package com.moufee.boilerfit.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.menus.DiningCourtMenu.Station;
import com.moufee.boilerfit.menus.DiningMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Adapter used to show a simple grid of products.
 */
public class DiningRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Station> mStations = new ArrayList<>();

    private int mTotalItems = 0;
    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_ITEM = 1;
    private Set<DiningMenuItem> mSelectedItems = new HashSet<>();
    private Set<String> mBlacklistedItemIds = new HashSet<>();
    private ActionMode.Callback mActionModeCallbacks;
    private OnItemClickListener<DiningMenuItem> mOnItemClickListener;
    private HashMap<String, Boolean> hm = new HashMap<>();

    public DiningRecyclerViewAdapter(OnItemClickListener<DiningMenuItem> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setStations(List<DiningCourtMenu.Station> stations) {
        mStations = stations;
        int total = 0;
        Iterator<Station> iterator = stations.iterator();
        while (iterator.hasNext()) {
            DiningCourtMenu.Station station = iterator.next();
            if (station.getNumItems() == 0) {
                iterator.remove();
            } else {
                total += station.getNumItems();
            }
        }
        total += stations.size();
        mTotalItems = total;
        notifyDataSetChanged();
    }

    public void setSelectedItems(Set<DiningMenuItem> selectedItems) {
        mSelectedItems = selectedItems;
        notifyDataSetChanged();
    }

    public void setHealthy(HashMap<String, Boolean> hm ){
        this.hm =hm;
    }

    public void setBlacklistedItems(Set<String> blacklistedItemIds) {
        mBlacklistedItemIds = blacklistedItemIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dining_station_header, parent, false);

            return new StationHeaderHolder(layoutView);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_holder, parent, false);
            return new MenuItemHolder(layoutView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER && holder instanceof StationHeaderHolder) {
            ((StationHeaderHolder) holder).name.setText(mStations.get(getSectionIndex(position)).getName());
        } else {
            DiningMenuItem item = getMenuItemForPosition(holder.getAdapterPosition());


            if (hm != null && hm.containsKey(item.getId()) && hm.get(item.getId()) == false) {
                ((MenuItemHolder) holder).name.setText(item.getName());
                ((MenuItemHolder) holder).name.setTextColor(Color.BLACK);

            }
            else {
                ((MenuItemHolder) holder).name.setText(item.getName());
                ((MenuItemHolder) holder).name.setTextColor(Color.GREEN);
            }

            if (mBlacklistedItemIds.contains(item.getId())) {
                ((MenuItemHolder) holder).dislikeIcon.setVisibility(View.VISIBLE);
            } else {
                ((MenuItemHolder) holder).dislikeIcon.setVisibility(View.INVISIBLE);
            }

            ((MenuItemHolder) holder).itemView.setOnClickListener(v -> {
                mOnItemClickListener.onItemClick(item);
                notifyDataSetChanged();
            });

            if (mSelectedItems.contains(item)) {
                ((MenuItemHolder) holder).itemView.setBackgroundColor(((MenuItemHolder) holder).itemView.getContext().getColor(R.color.colorPrimaryDark));
            } else {
                ((MenuItemHolder) holder).itemView.setBackgroundColor(((MenuItemHolder) holder).itemView.getContext().getColor(R.color.backgroundColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTotalItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (getPositionInSection(position) == 0)
            return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    private DiningMenuItem getMenuItemForPosition(int position) {
        return mStations.get(getSectionIndex(position)).getItem(getPositionInSection(position) - 1);
    }

    private int getPositionInSection(int position) {
        int currentStationStartIndex = 0;
        for (DiningCourtMenu.Station station : mStations) {
            if (position >= currentStationStartIndex && position <= currentStationStartIndex + station.getNumItems()) {
                return position - currentStationStartIndex;
            }
            currentStationStartIndex += station.getNumItems() + 1;
        }
        throw new IndexOutOfBoundsException("Position is outside the allowed range.");
    }

    private int getSectionIndex(int position) {
        int currentStationStartIndex = 0;
        for (int i = 0; i < mStations.size(); i++) {
            DiningCourtMenu.Station station = mStations.get(i);
            if (position >= currentStationStartIndex && position <= currentStationStartIndex + station.getNumItems()) {
                return i;
            }
            currentStationStartIndex += station.getNumItems() + 1;
        }
        throw new IndexOutOfBoundsException("Position is outside the allowed range.");
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }
}

