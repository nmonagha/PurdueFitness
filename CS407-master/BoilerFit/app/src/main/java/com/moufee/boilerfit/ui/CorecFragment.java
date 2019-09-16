package com.moufee.boilerfit.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.User;
import com.moufee.boilerfit.adapter.ProductCardRecyclerViewAdapter;
import com.moufee.boilerfit.adapter.ProductGridItemDecoration;
import com.moufee.boilerfit.corec.CorecFacility;
import com.moufee.boilerfit.repository.UserRepository;
import com.moufee.boilerfit.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class CorecFragment extends Fragment {

    private CorecViewModel mViewModel;
    private View rootView;
    private List<CorecFacility> corec_data;
    public static final String TAG = "CorecFragment";
    private TextView tv;
    private RecyclerView recyclerView;
    private RecyclerView favoriteView;


    public static CorecFragment newInstance() {
        return new CorecFragment();
    }

    private RecyclerView mRecyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    UserRepository mUserRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.corec_fragment, container, false);

        tv = rootView.findViewById(R.id.corec);
        recyclerView = rootView.findViewById(R.id.recycler_view_corec);
        favoriteView = rootView.findViewById(R.id.recycler_view_corec_favo);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        int largePadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));


        favoriteView.setHasFixedSize(true);
        favoriteView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

        favoriteView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));


        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> favorites = new ArrayList<>();
        AtomicReference<Boolean> flag = new AtomicReference<>(false);


        mUserRepository.getUserCallback(user -> {
            List<String> favs = user.getFavoriteActivities();
            for (String act : favs) {
                Log.d("Iamhere", act);
                favorites.add(act);
            }
            flag.set(true);


            Log.d("Sizeof", favorites.size() + "");


            list.add("Badminton");
            list.add("Volleyball");
            list.add("Basketball");
            list.add("Racquetball");
            list.add("Soccer");
            list.add("Climbing");
            list.add("Table Tennis");
            list.add("Wrestling");
            list.add("Group Exercise");
            list.add("Water Sports");
            list.add("Jogging");
            final ProductCardRecyclerViewAdapter adapter = new ProductCardRecyclerViewAdapter(list);
            final ProductCardRecyclerViewAdapter fav_adapter = new ProductCardRecyclerViewAdapter(favorites);
            recyclerView.setAdapter(adapter);
            favoriteView.setAdapter(fav_adapter);
            tv.setText("Select an Activity");
            adapter.onSetItemClickListener(new ProductCardRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {

                    String activity = list.get(pos);
                    //ArrayList fac contains eveything
                    Fragment newFragment = CorecFacilityFragment.newInstance(list.get(pos), new Runnable() {
                        @Override
                        public void run() {
                            mUserRepository.getUserCallback(new Callback<User>() {
                                @Override
                                public void accept(@javax.annotation.Nullable User user) {
                                    user.updateActivityStreak();
                                    mUserRepository.updateUser(user);
                                }
                            });
                        }
                    });
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newFragment)
                            .addToBackStack(null)
                            .commit();

                }
            });

            fav_adapter.onSetItemClickListener(new ProductCardRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {

                    String activity = favorites.get(pos);
                    Fragment newFragment = CorecFacilityFragment.newInstance(favorites.get(pos), new Runnable() {
                        @Override
                        public void run() {
                            mUserRepository.getUserCallback(new Callback<User>() {
                                @Override
                                public void accept(@javax.annotation.Nullable User user) {
                                    user.updateActivityStreak();
                                    mUserRepository.updateUser(user);
                                }
                            });
                        }
                    });
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newFragment)
                            .addToBackStack(null)
                            .commit();


                }
            });
            setListeners();

        });
        return rootView;

    }



    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity) context, mViewModelFactory).get(CorecViewModel.class);
        }
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


            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


}
