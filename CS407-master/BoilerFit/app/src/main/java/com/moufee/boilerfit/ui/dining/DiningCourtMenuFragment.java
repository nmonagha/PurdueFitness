package com.moufee.boilerfit.ui.dining;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.adapter.MealPagerAdapter;
import com.moufee.boilerfit.repository.UserRepository;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


public class DiningCourtMenuFragment extends Fragment {

    private static final String KEY_DC_INDEX = "dining_court_index";
    private int diningCourtIndex;
    private DiningViewModel mViewModel;
    private TextView mDiningCourtNameTextView;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    private MealPagerAdapter mMealPagerAdapter;
    ViewPager vp;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        mViewModel = ViewModelProviders.of((FragmentActivity) context, mViewModelFactory).get(DiningViewModel.class);
    }

    @Inject
    UserRepository mUserRepository;

    public static DiningCourtMenuFragment newInstance(int diningCourtIndex) {
        Bundle args = new Bundle();
        args.putInt(KEY_DC_INDEX, diningCourtIndex);
        DiningCourtMenuFragment fragment = new DiningCourtMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diningCourtIndex = getArguments().getInt(KEY_DC_INDEX, 0);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_dining_court_menu, container, false);


        vp = view.findViewById(R.id.meal_pager);
        mMealPagerAdapter = new MealPagerAdapter(getChildFragmentManager());
        vp.setAdapter(mMealPagerAdapter);
        mDiningCourtNameTextView = view.findViewById(R.id.dining_court_name_text_view);

        setListeners();
        // Set up the toolba
        return view;
    }

    @Override
    public void onDestroy() {
        ActionMode actionMode = mViewModel.getActionMode();
        if (actionMode != null) {
            actionMode.finish();
            mViewModel.setActionMode(null);
        }
        super.onDestroy();
    }

    private void setListeners() {
        mViewModel.getCurrentMenu().observe(this, fullDayMenuResource -> {
            if (fullDayMenuResource != null) {
                switch (fullDayMenuResource.status) {
                    case SUCCESS:
                        mMealPagerAdapter.setDiningCourtMenu(fullDayMenuResource.data.getMenu(diningCourtIndex));
                        mDiningCourtNameTextView.setText(fullDayMenuResource.data.getMenu(diningCourtIndex).getLocation());
                }
            }
        });
    }

}