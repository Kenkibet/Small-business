package com.example.smallbusiness;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smallbusiness.api.APIConstants;
import com.example.smallbusiness.api.IRestaurantAPI;
import com.example.smallbusiness.api.RetrofitClient;
import com.example.smallbusiness.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mMainNav;
    private static LinearLayout noInternetLayout;
    private static FrameLayout frameLayout;

    IRestaurantAPI myRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRestaurantAPI = RetrofitClient.getInstance(APIConstants.API_ENDPOINT).create(IRestaurantAPI.class);
        initView();
        setFragment(HomeFragment.newInstance());       


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        setFragment(HomeFragment.newInstance());
                        return true;
                    default: return false;
                }
            }
        });

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
    }
}
