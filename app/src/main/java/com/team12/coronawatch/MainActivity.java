package com.team12.coronawatch;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private Fragment selectedFragment;
    private View decorView;
    private int uiOption;

    private void init() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nv);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.statFragment);
        selectedFragment = new StatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                selectedFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedFragment = null;
                    int id = item.getItemId();
                    if (id == R.id.mapsFragment) {
                        selectedFragment = new MapsFragment();
                    } else if (id == R.id.statFragment) {
                        selectedFragment = new StatFragment();
                    } else if (id == R.id.graphFragment) {
                        selectedFragment = new GraphFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

}