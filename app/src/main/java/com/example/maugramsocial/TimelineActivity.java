package com.example.maugramsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TimelineActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        BottomNavigationView bottomNavigationView;

        HomeFragment homeFragment = new HomeFragment();
        SearchFragment searchFragment = new SearchFragment();
        ClubsFragment likesFragment = new ClubsFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.pageHome:
                        getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,homeFragment).commit();
                        return true;
                    case R.id.pageSearch:
                        getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,searchFragment).commit();
                        return true;
                    case R.id.pageLikes:
                        getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,likesFragment).commit();
                        return true;
                    case R.id.pageProfile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,profileFragment).commit();
                        return true;
                    default:
                        return false;
                }

            }

        });
    }
}