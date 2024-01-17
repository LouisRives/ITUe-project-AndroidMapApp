package com.example.petproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomAppBar);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.settings:
                    return true;

                case R.id.map:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_left_opposite, R.anim.slide_right_opposite);

                    finish();
                    return true;
                case R.id.list:
                    startActivity(new Intent(getApplicationContext(), ListOfCountriesActivity.class));
                    overridePendingTransition(R.anim.slide_left_opposite, R.anim.slide_right_opposite);

                    finish();
                    return true;
            }
            return false;
        });

    }
}
