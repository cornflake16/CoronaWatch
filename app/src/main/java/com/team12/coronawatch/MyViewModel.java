package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;

import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    public boolean btnFlag;
    @SuppressLint("StaticFieldLeak")
    public Button btnKorea, btnWorld;

    public MyViewModel() {
        Log.i("MyViewModel", "ViewModel is created!");
    }

    @Override
    public void onCleared() {
        super.onCleared();
        Log.i("MyViewModel", "ViewModel is destroyed!");
    }
}