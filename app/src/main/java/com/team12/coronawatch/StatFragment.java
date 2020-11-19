package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

public class StatFragment extends Fragment {
    ConstraintLayout conLayoutKorea, conLayoutWorld;
    Button btnKorea, btnWorld;
    TextView tv_decideCnt, tv_examCnt, tv_clearCnt, tv_deathCnt;
    TextView tv_decideIncrease, tv_examIncrease, tv_clearIncrease, tv_deathIncrease;
    TextView tv_decideCnt_world, tv_deathCnt_world;
    TextView tv_decideIncrease_world, tv_deathIncrease_world;
    TextView tv_state_date_kr_label, tv_state_date_nat_label;
    TableLayout tableLayout;
    CoronaKoreaStatus coronaKoreaStatus;
    CoronaNationalStatus coronaNationalStatus;
    KoreaXMLParser threadKr;
    NationalXMLParser threadNt;
    ContentLoadingProgressBar pb_decideCnt_world, pb_deathCnt_world;

    public void viewInit(View v) {
        tv_decideCnt = v.findViewById(R.id.tv_decideCnt_kr);
        tv_examCnt = v.findViewById(R.id.tv_examCnt_kr);
        tv_clearCnt = v.findViewById(R.id.tv_clearCnt_kr);
        tv_deathCnt = v.findViewById(R.id.tv_deathCnt_kr);
        tv_decideIncrease = v.findViewById(R.id.tv_decideIncrease_kr);
        tv_examIncrease = v.findViewById(R.id.tv_examIncrease_kr);
        tv_clearIncrease = v.findViewById(R.id.tv_clearIncrease_kr);
        tv_deathIncrease = v.findViewById(R.id.tv_deathIncrease_kr);

        tv_decideCnt_world = v.findViewById(R.id.tv_decideCnt_world);
        tv_deathCnt_world = v.findViewById(R.id.tv_deathCnt_world);
        tv_decideIncrease_world = v.findViewById(R.id.tv_decideIncrease_world);
        tv_deathIncrease_world = v.findViewById(R.id.tv_deathIncrease_world);

        tv_state_date_kr_label = v.findViewById(R.id.tv_state_date_kr_label);
        tv_state_date_nat_label = v.findViewById(R.id.tv_state_date_nat_label);

        btnKorea = v.findViewById(R.id.btn_korea);
        btnWorld = v.findViewById(R.id.btn_world);
        conLayoutKorea = v.findViewById(R.id.constraintLayout_kr);
        conLayoutWorld = v.findViewById(R.id.constraintLayout_world);
        tableLayout = v.findViewById(R.id.tableLayout);

        pb_decideCnt_world = v.findViewById(R.id.pb_decideCnt_world);
        pb_deathCnt_world = v.findViewById(R.id.pb_deathCnt_world);
    }

    public StatFragment() {
        coronaKoreaStatus = new CoronaKoreaStatus();
        coronaKoreaStatus.init();
        coronaNationalStatus = new CoronaNationalStatus();
        coronaNationalStatus.init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        threadKr = new KoreaXMLParser();
        threadNt = new NationalXMLParser();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        viewInit(view);
        threadKr.start();
        threadNt.start();

        final Drawable eraseBg = StatFragment.this.getResources().getDrawable(R.drawable.btn_stroke);
        final Drawable drawBg = StatFragment.this.getResources().getDrawable(R.drawable.btn_stroke_checked);
        btnKorea.setSelected(true);
        btnWorld.setSelected(false);

        btnKorea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(drawBg);
                btnWorld.setBackground(eraseBg);
                conLayoutWorld.setVisibility(View.INVISIBLE);
                conLayoutKorea.setVisibility(View.VISIBLE);
                tv_state_date_nat_label.setVisibility(View.INVISIBLE);
                tv_state_date_kr_label.setVisibility(View.VISIBLE);
            }
        });

        btnWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(eraseBg);
                btnWorld.setBackground(drawBg);
                conLayoutKorea.setVisibility(View.INVISIBLE);
                conLayoutWorld.setVisibility(View.VISIBLE);
                tv_state_date_kr_label.setVisibility(View.INVISIBLE);
                tv_state_date_nat_label.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    @SuppressLint("FieldLeak")
    class KoreaXMLParser extends Thread {
        @Override
        public void run() {
            coronaKoreaStatus.loadXML();
            coronaKoreaStatus.parseXML();
            coronaKoreaStatus.printInfo();

            //UI 제어
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        tv_decideCnt.setText(coronaKoreaStatus.newFmt_todayDecideCnt);
                        tv_decideIncrease.setText(coronaKoreaStatus.newFmt_decideIncCnt + " ▲");
                        tv_examCnt.setText(coronaKoreaStatus.newFmt_todayExamCnt);
                        tv_examIncrease.setText(coronaKoreaStatus.newFmt_examIncCnt + " ▲");
                        tv_clearCnt.setText(coronaKoreaStatus.newFmt_todayClearCnt);
                        tv_clearIncrease.setText(coronaKoreaStatus.newFmt_clearIncCnt + " ▲");
                        tv_deathCnt.setText(coronaKoreaStatus.newFmt_todayDeathCnt);
                        tv_deathIncrease.setText(coronaKoreaStatus.newFmt_deathIncCnt + " ▲");

                        tv_state_date_kr_label.setText(coronaKoreaStatus.todayStateDate);
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class NationalXMLParser extends Thread {

        @Override
        public void run() {
            pb_decideCnt_world.show();
            pb_deathCnt_world.show();
            coronaNationalStatus.loadXML();
            coronaNationalStatus.parseXML();
            coronaNationalStatus.printInfo();

            //UI 제어
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        pb_decideCnt_world.hide();
                        tv_decideCnt_world.setText("" + coronaNationalStatus.newFmt_todayTotNatDefCnt);
                        tv_decideIncrease_world.setText(coronaNationalStatus.newFmt_natDefIncCnt + " ▲");
                        pb_deathCnt_world.hide();
                        tv_deathCnt_world.setText("" + coronaNationalStatus.newFmt_todayTotNatDefCnt);
                        tv_deathIncrease_world.setText(coronaNationalStatus.newFmt_natDeathIncCnt + " ▲");

                        tv_state_date_nat_label.setText(coronaNationalStatus.newFormatStateDate);
                    }
                });
            }
        }
    }
}
