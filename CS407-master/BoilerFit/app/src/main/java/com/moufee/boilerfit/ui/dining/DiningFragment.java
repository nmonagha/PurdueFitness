package com.moufee.boilerfit.ui.dining;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moufee.boilerfit.R;
import com.moufee.boilerfit.adapter.ProductCardRecyclerViewAdapter;
import com.moufee.boilerfit.adapter.ProductGridItemDecoration;
import com.moufee.boilerfit.menus.DiningCourtMenu;
import com.moufee.boilerfit.menus.DiningMenuItem;
import com.moufee.boilerfit.menus.FullDayMenu;
import com.moufee.boilerfit.menus.nutrition.NutritionResponse;
import com.moufee.boilerfit.repository.UserRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiningFragment extends Fragment {
    private DiningViewModel mViewModel;


    @Inject
    ViewModelProvider.Factory mViewModelFactory;
    @Inject
    UserRepository mUserRepository;


    public static DiningFragment newInstance() {
        return new DiningFragment();
    }

    private static final String TAG = "DiningFragment";

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity) context, mViewModelFactory).get(DiningViewModel.class);
        }
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.dining_fragment, container, false);


        // Set up the RecyclerView
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        ArrayList<String> s = new ArrayList<>();
        s.add("Windsor");
        s.add("Wiley");
        s.add("Hillenbrand");
        s.add("Ford");
        s.add("Earhart");
        final ProductCardRecyclerViewAdapter adapter = new ProductCardRecyclerViewAdapter(s);
        recyclerView.setAdapter(adapter);
        //food id
        unHealthy("6c883ba0-e283-4086-ab01-e181a6615435");

        int largePadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);


        recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));
        adapter.onSetItemClickListener(pos -> {

            //todo: render this unnecessary
            for (int i = 0; i < mViewModel.getCurrentMenu().getValue().data.getNumMenus(); i++) {
                DiningCourtMenu diningMenu = mViewModel.getCurrentMenu().getValue().data.getMenu(i);


                if ((diningMenu.getLocation()).trim().equals(s.get(pos))) {
                    Fragment newFragment = DiningCourtMenuFragment.newInstance(i);
                    mViewModel.setSelectedDiningCourtIndex(i);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, newFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }


        });
        return view;
    }

    private void setListeners() {

    }

    private FullDayMenu healthyFoodOptions(final FullDayMenu menu) throws IOException {

        mUserRepository.getUserCallback(user -> {
            Map<String, Boolean> blacklistedItems = user.getBlacklistedItems();
            for (int i = 0; i < menu.getNumMenus(); i++) {
                DiningCourtMenu diningMenu = menu.getMenu(i);
                for (DiningCourtMenu.Meal meal : diningMenu.getMeals()) {
                    for (DiningCourtMenu.Station station : meal.getStations()) {
                        Iterator<DiningMenuItem> iterator = station.getItems().iterator();
                        while (iterator.hasNext()) {

                            DiningMenuItem menuItem = iterator.next();
                            if (!menuItem.isVegan() && user.mustHaveVegan()) {
                                iterator.remove();
                                continue;
                            }
                            //for vegetarian
                            if (!menuItem.isVegetarian() && user.mustHaveVegetarian()) {
                                iterator.remove();
                                continue;
                            }

                            if (blacklistedItems != null && blacklistedItems.containsKey(menuItem.getId())) {
                                iterator.remove();
                                continue;
                            }

                            //assuming that only allergies are in the set
                            if (menuItem.getAllergens() != null) {
                                for (String allergen : menuItem.getAllergens().getAllergenSet()) {
                                    if (user.isAllergicTo(allergen)) {
                                        iterator.remove();
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });
        return menu;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
    }

    private void unHealthy(String s) {
        ConnectivityManager ConnectivityManager = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return;
        }
        final ArrayList<NutritionResponse> list = new ArrayList<>();

        String id = s;
        Call<NutritionResponse> food_call = mViewModel.getNutrition(id);
        List<NutritionResponse> result = new ArrayList<>();
        food_call.enqueue(new Callback<NutritionResponse>() {
            @Override
            public void onResponse(Call<NutritionResponse> call, Response<NutritionResponse> response) {
                list.add(response.body());
            }

            @Override
            public void onFailure(Call<NutritionResponse> call, Throwable t) {
                System.out.println("Error");
            }
        });



        System.out.println(list);

    }



       /* String res = "";
        List<NutritionResponse.NutritionItem> t = result.get(0).getNutrition();
        for (int i = 0; i < t.size(); i++) {
            res += ", " + t.get(i);
        }

        int p = 0; */


}


