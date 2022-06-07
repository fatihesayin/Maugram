package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.maugramsocial.Activity.PostActivity;
import com.example.maugramsocial.Fragment.ClubsFragment;
import com.example.maugramsocial.Fragment.HomeFragment;
import com.example.maugramsocial.Fragment.ProfileFragment;
import com.example.maugramsocial.Fragment.SearchFragment;
import com.example.maugramsocial.R;
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
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String sender = intent.getString("senderID");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileID",sender);
            getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,new ProfileFragment()).commit();
        }
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.timeLineFrameLayout,new HomeFragment()).commit();

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
                    case R.id.pagePost:
                        Intent intentToPost = new Intent(getApplicationContext(), PostActivity.class);
                        startActivity(intentToPost);
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