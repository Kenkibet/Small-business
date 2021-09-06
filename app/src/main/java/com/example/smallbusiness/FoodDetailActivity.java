package com.example.smallbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.smallbusiness.api.APIConstants;
import com.example.smallbusiness.models.food.Food;
import com.example.smallbusiness.api.IRestaurantAPI;
import com.example.smallbusiness.api.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class FoodDetailActivity extends AppCompatActivity {
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

    IRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
                startActivity(new Intent(FoodDetailActivity.this, CartActivity.class));
            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Food cartItem = new Food();
                cartItem.setID(foodSelected.getID());
                cartItem.setName(foodSelected.getName());
                cartItem.setPrice(foodSelected.getPrice());
                cartItem.setImage(foodSelected.getImage());
                cartItem.setSize(false);

            }
        });
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(APIConstants.API_ENDPOINT).create(IRestaurantAPI.class);
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


    private void calculatePrice() {
        Double extraPrice = 0.0;
        double newPrice;
        extraPrice += sizePrice;
        extraPrice += addonPrice;
        newPrice = foodSelected.getPrice() + extraPrice;
        tvMoney.setText(String.valueOf(newPrice));
    }
}
