package com.example.smallbusiness.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.smallbusiness.R;
import com.example.smallbusiness.model.user.User;
import com.example.smallbusiness.retrofit.IMyRestaurantAPI;
import com.example.smallbusiness.retrofit.RetrofitClient;
import com.example.smallbusiness.ui.fragment.AccountFragment;
import com.example.smallbusiness.ui.fragment.OrderHistoryFragment;
import com.example.smallbusiness.ui.fragment.FavoritesFragment;
import com.example.smallbusiness.ui.fragment.HomeFragment;
import com.example.smallbusiness.ui.fragment.SettingsFragment;
import com.example.smallbusiness.receiver.NetworkChangeReceiver;
import com.example.smallbusiness.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private BroadcastReceiver receiver;
    private static LinearLayout noInternetLayout;
    private static FrameLayout frameLayout;

    IMyRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRestaurantAPI = RetrofitClient.getInstance(Utils.API_ENDPOINT).create(IMyRestaurantAPI.class);
        initView();
        setFragment(HomeFragment.newInstance());

        //Broadcast
        receiver = new NetworkChangeReceiver();
        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
        requestPermission();

        checkUserIsAlreadyLogin();

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setFragment(HomeFragment.newInstance());
                        return true;
                    case R.id.nav_history:
                        if(Utils.currentUser != null) setFragment(OrderHistoryFragment.newInstance());
                        else setFragment(SettingsFragment.newInstance());
                        return true;
                    case R.id.nav_favorites:
                        if(Utils.currentUser != null) setFragment(FavoritesFragment.newInstance());
                        else setFragment(SettingsFragment.newInstance());
                        return true;
                    case R.id.nav_account:
                        if(Utils.currentUser != null) setFragment(AccountFragment.newInstance());
                        else setFragment(SettingsFragment.newInstance());
                        return true;
                    default: return false;
                }
            }
        });

    }

    private void checkUserIsAlreadyLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString(Utils.EMAIL, "");
        String token = sharedPreferences.getString(Utils.TOKEN, "");
        compositeDisposable.add(myRestaurantAPI.getUser(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                            if(userModel.isSuccess()) {
                                User user = new User();
                                user.setName(userModel.getResult().get(0).getName());
                                user.setAddress(userModel.getResult().get(0).getAddress());
                                user.setEmail(userModel.getResult().get(0).getEmail());
                                user.setUserPhone(userModel.getResult().get(0).getUserPhone());
                                user.setToken(token);
                                Utils.currentUser = user;
                            } else Utils.currentUser = null;
                        },
                        throwable -> {})
        );
    }

    private void requestPermission() {
        Log.d("AAA", "vao ne");
        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Log.d("AAA", "OK");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "You must accept this permission to use this app!", Toast.LENGTH_LONG).show();
                        Log.d("AAA", "DENIED");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Log.d("AAA", "AAAAAAA");
                    }
                });
        Log.d("AAA", "Co vao");
    }

    public static void notificationNetwork(boolean value) {
        if(value == true) {
            noInternetLayout.setVisibility(View.INVISIBLE);
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
        }

    }

    private void updateUI() {
        /*if (mAuth.getCurrentUser() != null){
            Log.i("MainActivity", "fAuth != null");
        } else {
            Intent startIntent = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(startIntent);
            finish();
            Log.i("MainActivity", "fAuth == null");
        }*/
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_main, fragment);
        fragmentTransaction.commit();
    }

    private void initView() {
        mMainNav = findViewById(R.id.nav_main);
        noInternetLayout = findViewById(R.id.no_internet);
        frameLayout = findViewById(R.id.frame_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.i("EXCEPTION", e.getMessage());
        }
    }
}
