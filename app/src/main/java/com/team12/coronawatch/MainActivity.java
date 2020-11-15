package com.team12.coronawatch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    private Fragment selectedFragment;

    private void init() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nv);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.statFragment);
        selectedFragment = new StatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                selectedFragment).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        long FINISH_INTERVAL_TIME = 3000;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
            finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료
        } else {
            backPressedTime = tempTime;
            Snackbar.make(findViewById(R.id.layout_fragment), R.string.sbar_again_back_press_msg, Snackbar.LENGTH_SHORT).show();
        }
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
