package com.example.smallbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smallbusiness.R;
import com.example.smallbusiness.adapter.CartAdapter;
import com.example.smallbusiness.database.CartDataSource;
import com.example.smallbusiness.database.CartDatabase;
import com.example.smallbusiness.database.LocalCartDataSource;
import com.example.smallbusiness.model.eventbus.CalculateNewPriceEvent;
import com.example.smallbusiness.model.eventbus.SendTotalPriceEvent;
import com.example.smallbusiness.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartList extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_carts)
    RecyclerView recyclerCart;
    @BindView(R.id.btn_order)
    Button btnOrder;
    @BindView(R.id.tv_final_price)
    TextView tvFinalPrice;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        ButterKnife.bind(this);

        init();
        getAllCart();
        
    }

    private void getAllCart() {
        compositeDisposable.add(
                cartDataSource.getAllCart(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            cartItems -> {
                                if (cartItems.isEmpty()) {
                                    btnOrder.setText("Cart is empty");
                                    btnOrder.setEnabled(false);
                                } else {
                                    btnOrder.setText("Order");
                                    btnOrder.setEnabled(true);
                                    CartAdapter adapter = new CartAdapter(CartList.this, cartItems);
                                    recyclerCart.setAdapter(adapter);
                                    calculateTotalPrice();
                                }
                            },
                            throwable -> { Log.d("ERROR", throwable.getMessage());})
        );
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPrice(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        if(aLong <= 0) {
                            btnOrder.setText("Cart is empty");
                            btnOrder.setEnabled(false);
                        } else {
                            btnOrder.setText("Order");
                            btnOrder.setEnabled(true);
                        }
                        tvFinalPrice.setText(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvFinalPrice.setText("0");
                    }
                });
    }

    private void init() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
        toolbar.setTitle("Cart");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerCart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btnOrder.setOnClickListener(view -> {
            String totalPrice = tvFinalPrice.getText().toString();
            EventBus.getDefault().postSticky(new SendTotalPriceEvent(totalPrice));
            startActivity(new Intent(CartList.this, OrderActivity.class));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
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
    public void calculatePrice(CalculateNewPriceEvent event) {
        if(event != null) calculateTotalPrice();
    }
}
