package com.moufee.boilerfit.ui;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.adapter.CorecAdapter;
import com.moufee.boilerfit.adapter.ProductGridItemDecoration;
import com.moufee.boilerfit.corec.CorecFacility;
import com.moufee.boilerfit.ui.corec.CoRecHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CorecFacilityFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private  ArrayList<CorecFacility> fac;
    private String exercise;
    private TextView tv;
    private RecyclerView recyclerView;
    private List<CorecFacility> corec_data;
    private Menu menu;
    private MenuInflater mi;
    public ActionMode actionMode = null;

    // TODO: Rename and change types of parameters
    private static String mParam1;
    private ActionMode.Callback mActionModeCallbacks;

    private String mParam2;
    private CorecViewModel mViewModel;
    ViewModelProvider.Factory mViewModelFactory;

    public void setSubmitClick(Runnable submitClick) {
        this.submitClick = submitClick;
    }

    Runnable submitClick;

    @Override
    public void onAttach(Context context) {
        //   AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity) context,mViewModelFactory ).get(CorecViewModel.class);
        }
    }

    public static CorecFacilityFragment newInstance(String fac, Runnable submitClick) {
        Bundle args = new Bundle();
        args.putString(mParam1, fac);
        CorecFacilityFragment fragment = new CorecFacilityFragment();
        fragment.setSubmitClick(submitClick);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercise = getArguments().getString(mParam1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dining_fragment, container, false);
        tv = rootView.findViewById(R.id.text);
        tv.setText(exercise);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        int largePadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
        fac = new ArrayList<>();
        mActionModeCallbacks = new ActionMode.Callback() {


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.corec_action_menu, menu);
                mode.setTitle("Activity Completed");
                actionMode = mode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                if (item.getItemId() == R.id.menu_action_submit) {
                    submitClick.run();
                    mode.finish();
                    //add data to firebase database
                    return true;
                }
                return false;

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };
        ((AppCompatActivity) getContext()).startSupportActionMode(mActionModeCallbacks);
        setListeners();
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (actionMode!=null)
            actionMode.finish();

    }

    private void setListeners() {
        mViewModel.getCurrentUsage().observe(this, listResource -> {
            if (listResource == null) {
                return;
            }
            switch (listResource.status) {
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    corec_data = listResource.data;

                    List<String> locationNames = getLocationNames(exercise);
                    for (CorecFacility facility:corec_data) {
                        if (locationNames.contains(facility.getLocationName())) {
                            fac.add(facility);
                        }
                    }
                    for (CorecFacility pal:fac) {
                        Log.d(pal.getLocationName(),"palina");
                    }
                    CorecAdapter adapter = new CorecAdapter(fac);
                    recyclerView.setAdapter(adapter);


            }
        });
    }


    public static List<String> getLocationNames (String activity) {
        Map<String, List<String>> map = CoRecHelper.getActivitiesMap();
        return map.get(activity);
    }
}
