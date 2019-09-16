package com.moufee.boilerfit.ui.dining;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.User;
import com.moufee.boilerfit.adapter.DiningRecyclerViewAdapter;
import com.moufee.boilerfit.adapter.ProductGridItemDecoration;
import com.moufee.boilerfit.menus.DiningMenuItem;
import com.moufee.boilerfit.repository.UserRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class MealFragment extends Fragment implements DiningRecyclerViewAdapter.OnItemClickListener<DiningMenuItem> {


    public MealFragment() {
        // Required empty public constructor
    }

    public static final String KEY_DC_INDEX = "dining_court_index";
    public static final String KEY_MEAL_INDEX = "mealIndex";
    private DiningRecyclerViewAdapter mAdapter;
    private DiningViewModel mDiningViewModel;
    private int mMealIndex;
    private ActionMode.Callback mActionModeCallbacks;
    public HashMap<String, Boolean> hm_healthy;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    UserRepository mUserRepository;

    public static MealFragment newInstance(int mealIndex) {
        MealFragment fragment = new MealFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_MEAL_INDEX, mealIndex);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMealIndex = getArguments().getInt(KEY_MEAL_INDEX, 0);

    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        mDiningViewModel = ViewModelProviders.of((FragmentActivity) context, mViewModelFactory).get(DiningViewModel.class);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_dining_meal, container, false);

        // Set up the toolba

        // Set up the RecyclerView
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        mActionModeCallbacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.dining_action_menu, menu);
                mDiningViewModel.setActionMode(mode);
                mode.setTitle("Select Foods");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.menu_action_submit) {
                    recordMenuitems(mDiningViewModel.getSelectedItems().getValue());
                    updateFoodStreak(mDiningViewModel.getSelectedItems().getValue());
                    mode.finish();
                    return true;
                } else if (item.getItemId() == R.id.menu_action_dislike) {
                    blacklistMenuItems(mDiningViewModel.getSelectedItems().getValue());
                    mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mDiningViewModel.setActionMode(null);
                mDiningViewModel.clearSelectedItems();

            }
        };

        mAdapter = new DiningRecyclerViewAdapter(this);
        mAdapter.setHealthy(mDiningViewModel.isHealthy_res());
        recyclerView.setAdapter(mAdapter);




        int largePadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);

        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
        setListeners();
        return view;
    }

    private void recordMenuitems(Set<DiningMenuItem> items) {
        //todo: take some action with selected items
        User user = mDiningViewModel.getUser().getValue();
        if (user == null) return;
        // record stuff for streak purposes
        Map<String, Integer> diningCourtCounts = user.getDiningCourtCounts();
        if (diningCourtCounts == null) diningCourtCounts = new HashMap<>();
        int diningCourtIndex = mDiningViewModel.getSelectedDiningCourtIndex().getValue();
        String diningCourtName = mDiningViewModel.getCurrentMenu().getValue().data.getMenu(diningCourtIndex).getLocation();
        if (diningCourtCounts.containsKey(diningCourtName)) {
            diningCourtCounts.put(diningCourtName, diningCourtCounts.get(diningCourtName) + 1);
        } else {
            diningCourtCounts.put(diningCourtName, 1);
        }
    }



    private void blacklistMenuItems(Set<DiningMenuItem> items) {
        User user = mDiningViewModel.getUser().getValue();
        if (user == null) return;
        for (DiningMenuItem item :
                items) {
            if (user.getBlacklistedItems() == null) user.setBlacklistedItems(new HashMap<>());
            user.getBlacklistedItems().put(item.getId(), true);
        }
        mUserRepository.updateUser(user);
    }

    public void updateFoodStreak(Set<DiningMenuItem> items){

        if(items.size() > 0){
            User user = mDiningViewModel.getUser().getValue();
            if (user == null) return;
            user.updateFoodStreak();
            mUserRepository.updateUser(user);
        }
    }

    @Override
    public void onItemClick(DiningMenuItem item) {
        if (mDiningViewModel.getSelectedItems().getValue().isEmpty() && mDiningViewModel.getActionMode() == null) {
            ((AppCompatActivity) getContext()).startSupportActionMode(mActionModeCallbacks);
        }
        if (mDiningViewModel.getSelectedItems().getValue().contains(item)) {
            mDiningViewModel.removeSelectedItem(item);
        } else {
            mDiningViewModel.addSelectedItem(item);
        }
        mDiningViewModel.getActionMode().setSubtitle(String.format(Locale.getDefault(), "%d selected", mDiningViewModel.getSelectedItems().getValue().size()));
        if (mDiningViewModel.getSelectedItems().getValue().isEmpty() && mDiningViewModel.getActionMode() != null) {
            mDiningViewModel.getActionMode().finish();
        }
    }

    private void setListeners() {
        mDiningViewModel.getCurrentMenu().observe(this, fullDayMenuResource -> {
            if (fullDayMenuResource != null) {
                switch (fullDayMenuResource.status) {
                    case SUCCESS:
                        mAdapter.setStations(fullDayMenuResource.data.getMenu(mDiningViewModel.getSelectedDiningCourtIndex().getValue()).getMeals().get(mMealIndex).getStations());
                }
            }
        });
        mDiningViewModel.getSelectedItems().observe(this, items -> mAdapter.setSelectedItems(items));
        mDiningViewModel.getUser().observe(this, user -> mAdapter.setBlacklistedItems(user.getBlacklistedItems().keySet()));

    }

}
