package com.example.smallbusiness.ui.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.smallbusiness.R;
import com.example.smallbusiness.common.dialog.ProgressLoading;
import com.example.smallbusiness.database.CartDataSource;
import com.example.smallbusiness.database.CartDatabase;
import com.example.smallbusiness.database.LocalCartDataSource;
import com.example.smallbusiness.model.brainTree.BrainTreeToken;
import com.example.smallbusiness.model.eventbus.SendTotalPriceEvent;
import com.example.smallbusiness.model.order.Order;
import com.example.smallbusiness.retrofit.BrainTreeClient;
import com.example.smallbusiness.retrofit.IBrainTreeAPI;
import com.example.smallbusiness.retrofit.IMyRestaurantAPI;
import com.example.smallbusiness.retrofit.RetrofitClient;
import com.example.smallbusiness.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OrderActivity extends AppCompatActivity {

    @BindView(R.id.tv_money) TextView tvTotalPrice;
    @BindView(R.id.tv_phone) TextView tvUserPhone;
    @BindView(R.id.tv_address) TextView tvAddress;
    @BindView(R.id.chb_address) CheckBox chbAddress;
    @BindView(R.id.btn_order) Button btnOrder;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rdi_cod) RadioButton rdiCod;
    @BindView(R.id.rdi_online_payment) RadioButton rdiOnlinePayment;

    IMyRestaurantAPI myRestaurantAPI;
    IBrainTreeAPI brainTreeAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;
    boolean isAddNewAddress;

    //private static final int RESULT_OK = 1;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        init();
        Log.d("HUHU", RESULT_OK +"-----------" );
        initView();
    }


    @SuppressLint("CheckResult")
    private void getNumberOrder(boolean isOnlinePayment) {
        String address = tvAddress.getText().toString();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d("DAY", currentDate);
        if(!isOnlinePayment) {
            compositeDisposable.add(
                    cartDataSource.getAllCart(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    cartItems -> {
                                        //Get order number
                                        myRestaurantAPI.createOrder(
                                                "Bearer "+Utils.currentUser.getToken(),
                                                Utils.currentUser.getEmail(),
                                                Utils.currentUser.getUserPhone(),
                                                Utils.currentUser.getName(),
                                                address,
                                                currentDate,
                                                Utils.currentRestaurant.getId(),
                                                "NONE",
                                                true,
                                                Double.parseDouble(tvTotalPrice.getText().toString()),
                                                cartItems.size()
                                        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(createOrderModel -> {
                                                    if(createOrderModel.isSuccess()) {
                                                        // After create order, clear cart
                                                        cartDataSource.cleanCart(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new SingleObserver<Integer>() {
                                                                    @Override
                                                                    public void onSubscribe(Disposable d) {

                                                                    }

                                                                    @Override
                                                                    public void onSuccess(Integer integer) {
                                                                        Toast.makeText(OrderActivity.this, "Order successful!", Toast.LENGTH_LONG).show();
                                                                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        Log.d("ERROR", e.getMessage());
                                                                    }
                                                                });
                                                    }
                                                }, throwable -> {
                                                    Log.d("ERROR", throwable.getMessage());
                                                });
                                    },
                                    throwable -> { Log.d("ERROR", throwable.getMessage());})
            );
        }
        else {
            //First, get token
            ProgressLoading.show(this);
            compositeDisposable.add(brainTreeAPI.getToken()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(brainTreeToken -> {
                        ProgressLoading.dismiss();
                        Log.d("OKOK", brainTreeToken.toString());
                        if(brainTreeToken.isSuccess()) {
                            DropInRequest dropInRequest = new DropInRequest().clientToken(brainTreeToken.getClientToken());
                            startActivityForResult(dropInRequest.getIntent(OrderActivity.this), Utils.DROP_IN_REQUEST);
                        } else {
                            Log.d("TOKEN", "Can not get token");
                        }
                    }, throwable -> {
                        ProgressLoading.dismiss();
                        Log.d("ERROR", throwable.getMessage());
                    })
            );
        }

    }

    private void initView() {
        tvUserPhone.setText(Utils.currentUser.getUserPhone());
        tvAddress.setText(Utils.currentUser.getAddress());
        toolbar.setTitle("Order Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAddNewAddress = true;
                chbAddress.setChecked(false);
                View layout_add_new_address = LayoutInflater.from(OrderActivity.this).inflate(R.layout.item_add_new_address, null);
                EditText edtNewAddress = layout_add_new_address.findViewById(R.id.edt_new_address);
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this)
                        .setTitle("Add new address")
                        .setView(layout_add_new_address)
                        .setNegativeButton("CANCEL", (((dialog, which) -> dialog.dismiss())))
                        .setPositiveButton("ADD", (((dialog, which) -> tvAddress.setText(edtNewAddress.getText().toString()))));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!chbAddress.isChecked()) {
                    Toast.makeText(OrderActivity.this, "Please choose default address or set new address!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(rdiCod.isChecked()) {
                    getNumberOrder(false);
                } else if(rdiOnlinePayment.isChecked()) {
                    getNumberOrder(true);
                }
            }
        });
    }

    private void init() {
        myRestaurantAPI = RetrofitClient.getInstance(Utils.API_ENDPOINT).create(IMyRestaurantAPI.class);
        brainTreeAPI = BrainTreeClient.getInstance(Utils.currentRestaurant.getPaymentUrl()).create(IBrainTreeAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Utils.DROP_IN_REQUEST) {
            if(resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                // After have nonce, made a payment with API
                String amount = tvTotalPrice.getText().toString();
                String address = tvAddress.getText().toString();
                ProgressLoading.show(OrderActivity.this);
                compositeDisposable.add(brainTreeAPI.submitPayment(amount, nonce.getNonce())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(brainTreeTransaction -> {
                                    ProgressLoading.dismiss();
                                    Log.d("OKOKOK", brainTreeTransaction.toString());
                                    if(brainTreeTransaction.isSuccess()) {
                                        // After have transaction, make an order
                                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                        compositeDisposable.add(
                                                cartDataSource.getAllCart(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                cartItems -> {
                                                                    //Get order number
                                                                    myRestaurantAPI.createOrder(
                                                                            "Bearer "+Utils.currentUser.getToken(),
                                                                            Utils.currentUser.getEmail(),
                                                                            Utils.currentUser.getUserPhone(),
                                                                            Utils.currentUser.getName(),
                                                                            address,
                                                                            currentDate,
                                                                            Utils.currentRestaurant.getId(),
                                                                            brainTreeTransaction.getTransaction().getId(),
                                                                            false,
                                                                            Double.parseDouble(amount),
                                                                            cartItems.size()
                                                                    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                                                            .subscribe(createOrderModel -> {
                                                                                Log.d("BUG", createOrderModel.toString());
                                                                                if(createOrderModel.isSuccess()) {
                                                                                    // After create order, clear cart
                                                                                    cartDataSource.cleanCart(Utils.currentUser.getEmail(), Utils.currentRestaurant.getId())
                                                                                            .subscribeOn(Schedulers.io())
                                                                                            .observeOn(AndroidSchedulers.mainThread())
                                                                                            .subscribe(new SingleObserver<Integer>() {
                                                                                                @Override
                                                                                                public void onSubscribe(Disposable d) {

                                                                                                }

                                                                                                @Override
                                                                                                public void onSuccess(Integer integer) {
                                                                                                    Toast.makeText(OrderActivity.this, "Order successful!", Toast.LENGTH_LONG).show();
                                                                                                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                                    startActivity(intent);
                                                                                                    finish();
                                                                                                }

                                                                                                @Override
                                                                                                public void onError(Throwable e) {
                                                                                                    Log.d("ERROR", e.getMessage());
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }, throwable -> {
                                                                                Log.d("ERROR", throwable.getMessage());
                                                                            });
                                                                },
                                                                throwable -> { Log.d("ERROR", throwable.getMessage());})
                                        );
                                    } else {

                                        Log.d("TRANSACTION", "Transaction failed");
                                    }
                                },
                                throwable -> {
                                    ProgressLoading.dismiss();
                                    Log.d("ERROR", throwable.getMessage());
                                })
                );
            } else if (resultCode == RESULT_CANCELED) {
                // canceled
                Log.d("HUHU", "vao day a");
            } else {
                // an error occurred, checked the returned exception
                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
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
    public void SendTotalPrice(SendTotalPriceEvent event) {
        tvTotalPrice.setText(String.valueOf(event.getTotalPrice()));
    }
}
