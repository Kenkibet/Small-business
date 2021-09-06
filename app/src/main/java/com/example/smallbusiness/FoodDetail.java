package com.example.smallbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smallbusiness.R;
import com.example.smallbusiness.adapter.AddOnAdapter;
import com.example.smallbusiness.common.dialog.ProgressLoading;
import com.example.smallbusiness.database.CartDataSource;
import com.example.smallbusiness.database.CartDatabase;
import com.example.smallbusiness.database.CartItem;
import com.example.smallbusiness.database.LocalCartDataSource;
import com.example.smallbusiness.model.eventbus.AddOnEventChange;
import com.example.smallbusiness.model.food.Food;
import com.example.smallbusiness.model.size.Size;
import com.example.smallbusiness.model.eventbus.AddOnLoadEvent;
import com.example.smallbusiness.model.eventbus.FoodDetailEvent;
import com.example.smallbusiness.model.eventbus.SizeLoadEvent;
import com.example.smallbusiness.retrofit.IMyRestaurantAPI;
import com.example.smallbusiness.retrofit.RetrofitClient;
import com.example.smallbusiness.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodDetail extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton btnAddCart;
    @BindView(R.id.btn_view_cart)
    Button btnViewCart;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.radio_size)
    RadioGroup radioSize;
    @BindView(R.id.tv_description) TextView tvDescription;
    @BindView(R.id.img_food_detail)
    ImageView imgFoodDetail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    Food foodSelected;
    private double sizePrice = 0.0;
    private double addonPrice = 0.0;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        ButterKnife.bind(this);
        init();

        btnViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodDetail.this, CartList.class));
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(foodSelected.getID());
                cartItem.setFoodName(foodSelected.getName());
                cartItem.setFoodPrice(foodSelected.getPrice());
                cartItem.setFoodImage(foodSelected.getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setEmail(Utils.currentUser.getEmail());
                cartItem.setUserPhone(Utils.currentUser.getUserPhone());
                cartItem.setRestaurantId(Utils.currentRestaurant.getId());
                cartItem.setFoodAddon("NORMAL");
                cartItem.setFoodSize("NORMAL");
                cartItem.setFoodExtraPrice(0.0);

                compositeDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(FoodDetail.this, "ADDED TO CART", Toast.LENGTH_LONG).show();
                                        },
                                        throwable -> {
                                            Log.d("ERROR CART", throwable.getMessage());
                                        })
                );
            }
        });
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(Utils.API_ENDPOINT).create(IMyRestaurantAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
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
    public void displayFoodDetail(FoodDetailEvent event) {
        if(event.isSuccess()) {
            foodSelected = event.getFood();
            toolbar.setTitle(foodSelected.getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            tvMoney.setText(String.valueOf(foodSelected.getPrice()));
            tvDescription.setText(foodSelected.getDescription());
            Picasso.get().load(foodSelected.getImage()).into(imgFoodDetail);

            if(foodSelected.isSize() && foodSelected.isAddon()) {
                // load size and add on from server
                ProgressLoading.show(FoodDetail.this);
                compositeDisposable.add(
                        myRestaurantAPI.getSizeOfFood("Bearer " + Utils.currentUser.getToken(), Utils.API_KEY, foodSelected.getID())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        sizeModel -> {
                                            ProgressLoading.dismiss();
                                            // Send local event bus
                                            EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                                            // Load add on
                                            ProgressLoading.show(FoodDetail.this);
                                            compositeDisposable.add(
                                                    myRestaurantAPI.getAddOnOfFood("Bearer " + Utils.currentUser.getToken(), Utils.API_KEY, foodSelected.getID())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(
                                                                    addOnModel -> {
                                                                        ProgressLoading.dismiss();
                                                                        EventBus.getDefault().post(new AddOnLoadEvent(true, addOnModel.getResult()));
                                                                    },
                                                                    throwable -> {ProgressLoading.dismiss();}
                                                            )
                                            );
                                        },
                                        throwable -> { ProgressLoading.dismiss(); }
                                )
                );
            }
            else {
                if(foodSelected.isSize()) {
                    //if only have size
                    ProgressLoading.show(FoodDetail.this);
                    compositeDisposable.add(
                            myRestaurantAPI.getSizeOfFood("Bearer " + Utils.currentUser.getToken(), Utils.API_KEY, foodSelected.getID())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            sizeModel -> {
                                                ProgressLoading.dismiss();
                                                // Send local event bus
                                                EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                                            },
                                            throwable -> { ProgressLoading.dismiss(); }
                                    )
                    );
                }
                if(foodSelected.isAddon()) {
                    // if only have add on
                    ProgressLoading.show(FoodDetail.this);
                    compositeDisposable.add(
                            myRestaurantAPI.getAddOnOfFood("Bearer " + Utils.currentUser.getToken(), Utils.API_KEY, foodSelected.getID())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            addOnModel -> {
                                                ProgressLoading.dismiss();
                                                EventBus.getDefault().post(new AddOnLoadEvent(true, addOnModel.getResult()));
                                            },
                                            throwable -> {ProgressLoading.dismiss();}
                                    )
                    );
                }
            }
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event) {
        if(event.isSuccess()) {
            //create radiobutton
            for(Size size: event.getSizeList()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            sizePrice += size.getExtraPrice();
                        } else sizePrice -= size.getExtraPrice();
                        calculatePrice();
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());
                radioSize.addView(radioButton);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddOn(AddOnLoadEvent event) {
        if(event.isSuccess()) {

        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void priceChange(AddOnEventChange eventChange) {
        if(eventChange.isAdd()) {
            addonPrice += eventChange.getAddOn().getExtraPrice();
        } else addonPrice -= eventChange.getAddOn().getExtraPrice();
        calculatePrice();
    }

    private void calculatePrice() {
        Double extraPrice = 0.0;
        double newPrice;
        extraPrice += sizePrice;
        extraPrice += addonPrice;
        newPrice = foodSelected.getPrice() + extraPrice;
        tvMoney.setText(String.valueOf(newPrice));
    }
}
