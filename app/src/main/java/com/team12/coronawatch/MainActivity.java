package com.team12.coronawatch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.naver.maps.map.NaverMapSdk;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    private Fragment selectedFragment;
    int beforeId;

    private void init() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nv);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.statFragment);
        selectedFragment = new StatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                selectedFragment).commit();
    }

    private void AppFinish() {
        moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
        finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        long FINISH_INTERVAL_TIME = 3000;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            AppFinish();
        } else {
            backPressedTime = tempTime;
            Snackbar.make(findViewById(R.id.layout_fragment), R.string.sbar_again_back_press_msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX); //태블릿 네트워크
        boolean bwimax = false;

        if (wimax != null)
            bwimax = wimax.isConnected();
        if (mobile != null) {
            return !mobile.isConnected() && !wifi.isConnected() && !bwimax;
        } else {
            return !wifi.isConnected() && !bwimax;
        }
    }

    private void networkCheck() {
        if (isNetworkConnected(getApplicationContext())) {
            Log.i("Network connection: ", "disconnected");
            Toast.makeText(getApplicationContext(), R.string.toast_check_network_msg,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkCheck(); //사용자에게 네트워크 연결을 요청 -> 미 연결시 앱 종료
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
                    if (selectedFragment != null && id != beforeId) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                                selectedFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        beforeId = id;
                    } else {
                        return false;
                    }
                    return true;
                }
            };
}
