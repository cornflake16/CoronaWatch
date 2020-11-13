package com.team12.coronawatch;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.mapsFragment:
                            selectedFragment = new MapsFragment();
                            break;
                        case R.id.statFragment:
                            selectedFragment = new StatFragment();
                            break;
                        case R.id.graphFragment:
                            selectedFragment = new GraphFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                            selectedFragment).commit();

                    return true;
                }
            };

}