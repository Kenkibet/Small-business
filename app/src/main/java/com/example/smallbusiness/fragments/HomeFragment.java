package com.example.smallbusiness.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.smallbusiness.R;
import com.example.smallbusiness.adapters.RestaurantAdapter;
import com.example.smallbusiness.api.APIConstants;
import com.example.smallbusiness.api.IRestaurantAPI;
import com.example.smallbusiness.api.RetrofitClient;
import com.example.smallbusiness.models.restaurant.Restaurant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_restaurant) RecyclerView recyclerRestaurant;

    IRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRestaurant();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        refreshLayout = view.findViewById(R.id.swipe);
        /*if(Utils.isOnline) {
            ProgressLoading.show(getContext());
        }*/

        init();
        loadRestaurant();

        setOnRefreshListener();
        return view;
    }

    private void loadRestaurant() {
        compositeDisposable.add(
                myRestaurantAPI.getAllRestaurant(APIConstants.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantModel -> {
                            //use EventBus to send local event set adapter and Slider
                            EventBus.getDefault().post(restaurantModel.getResult());
                            Log.d("GET", restaurantModel.getResult().get(0).toString());
                            Log.d("GET", restaurantModel.getResult().size()+"");
                        },
                        throwable -> {
                            EventBus.getDefault().post(throwable.getMessage());
                            Log.d("GET ERROR", throwable.getMessage());
                })
        );
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(APIConstants.API_ENDPOINT).create(IRestaurantAPI.class);
    }

    private void setOnRefreshListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRestaurant();
            }
        });
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadRestaurant();
            }
        });
    }

    //Register EventBus
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    //UnRegister EventBus
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void displayRestaurant(List<Restaurant> restaurantList) {
        recyclerRestaurant.setHasFixedSize(true);
        RestaurantAdapter adapter = new RestaurantAdapter(getContext(), restaurantList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerRestaurant.setLayoutManager(layoutManager);
        recyclerRestaurant.setAdapter(adapter);
    }

}