package com.team12.coronawatch;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    //지도 관련 변수
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationSource locationSource;
    private LocationManager locationManager;
    private MapView naverMap;

    private void init(View v) {
    }

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Fragment mapFragment = getParentFragmentManager().findFragmentById(R.id.mapsFragment);
        if (mapFragment != null) {
            getParentFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializing maps object
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        NaverMapSdk.getInstance(requireActivity()).setClient(new NaverMapSdk.NaverCloudPlatformClient(
                "cwouczl691"));
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initializing maps object
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        NaverMapSdk.getInstance(getActivity()).setClient(new NaverMapSdk.NaverCloudPlatformClient(
                "cwouczl691"));
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_maps, container, false);
//        init(v);

        naverMap = v.findViewById(R.id.map_view);
        naverMap.onCreate(savedInstanceState);
        naverMap.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(33.38, 126.55),   // 위치 지정
                9,                                     // 줌 레벨
                0,                                       // 기울임 각도
                0                                     // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
//                CameraUpdate.zoomTo(5.0);
            }
        });

        // 줌 범위 제한
        naverMap.setMinZoom(6.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대용
    }
}