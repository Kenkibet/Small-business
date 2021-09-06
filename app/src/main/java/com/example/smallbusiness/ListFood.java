package com.example.smallbusiness.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.smallbusiness.R;
import com.example.smallbusiness.adapter.FoodAdapter;
import com.example.smallbusiness.common.dialog.ProgressLoading;
import com.example.smallbusiness.model.eventbus.FoodListEvent;
import com.example.smallbusiness.retrofit.IMyRestaurantAPI;
import com.example.smallbusiness.retrofit.RetrofitClient;
import com.example.smallbusiness.utils.Utils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ListFood extends AppCompatActivity {
    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FoodAdapter foodAdapter, searchAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_restaurant)
    ImageView imgRestaurant;
    @BindView(R.id.recycler_foods)
    RecyclerView recyclerFoods;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if(foodAdapter != null) {
            foodAdapter.onStop();
        }
        if(searchAdapter != null) {
            searchAdapter.onStop();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food);
        ButterKnife.bind(this);
        init();
        initView();

    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerFoods.setLayoutManager(layoutManager);
        recyclerFoods.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(Utils.API_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.search_item, menu);
        MenuItem menuItem = menu.findItem(R.id.search_home);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchFood(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Restore to original adapter
                recyclerFoods.setAdapter(foodAdapter);
                return false;
            }
        });
        return true;
    }

    private void startSearchFood(String query) {
        ProgressLoading.show(this);
        compositeDisposable.add(
                myRestaurantAPI.getFoodByName(Utils.API_KEY, query, "Bearer " + Utils.currentUser.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(foodModel -> {
                                    if(foodModel.isSuccess()) {
                                        searchAdapter = new FoodAdapter(this, foodModel.getResult());
                                        recyclerFoods.setAdapter(searchAdapter);
                                    } else {
                                        Log.d("SEARCH FOOD", foodModel.getMessage());
                                    }
                                    ProgressLoading.dismiss();
                                },
                                throwable -> {
                                    ProgressLoading.dismiss();
                                })
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //EventBus
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadFoodsByCategory(FoodListEvent event) {
        ProgressLoading.show(this);
        if(event.isSuccess()) {
            Picasso.get().load(event.getCategory().getImage()).into(imgRestaurant);
            toolbar.setTitle(event.getCategory().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            compositeDisposable.add(
                    myRestaurantAPI.getFoodByMenuId(Utils.API_KEY, event.getCategory().getID(), "Bearer " + Utils.currentUser.getToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(foodModel -> {
                                        if(foodModel.isSuccess()) {
                                            Log.d("AAA", "vafo day" + foodModel.getResult().size());
                                            foodAdapter = new FoodAdapter(this, foodModel.getResult());
                                            recyclerFoods.setAdapter(foodAdapter);
                                        } else {
                                            Log.d("GET FOOD", foodModel.getMessage());
                                        }
                                    },
                                    throwable -> {

                                    })
            );
        } else {

        }
        ProgressLoading.dismiss();
    }

}
