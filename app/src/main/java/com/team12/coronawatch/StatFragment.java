package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class StatFragment extends Fragment {
    ConstraintLayout conLayoutKorea, conLayoutWorld;

    Button btnKorea, btnWorld;

    TextView tv_defCnt, tv_examCnt, tv_clearCnt, tv_deathCnt;
    TextView tv_defIncrease, tv_examIncrease, tv_clearIncrease, tv_deathIncrease;
    TextView tv_defCnt_world, tv_deathCnt_world;
    TextView tv_defIncrease_world, tv_deathIncrease_world;
    TextView tv_state_date_kr_label, tv_state_date_nat_label;
    TextView cv_region, cv_isol, cv_def, cv_clear, cv_death;

    ListView listView;
    CustomAdapter adapter;

    CoronaKoreaStatus coronaKRStatus;
    CoronaRegionalStatus coronaRegStatus;
    CoronaNationalStatus coronaNatStatus;

    KoreaXMLParser threadKr;
    NationalXMLParser threadNt;
    RegionalXMLParser threadRg;

    ContentLoadingProgressBar pb_defCnt_world, pb_deathCnt_world;


    public void viewInit(View v) {
        tv_defCnt = v.findViewById(R.id.tv_decideCnt_kr);
        tv_examCnt = v.findViewById(R.id.tv_examCnt_kr);
        tv_clearCnt = v.findViewById(R.id.tv_clearCnt_kr);
        tv_deathCnt = v.findViewById(R.id.tv_deathCnt_kr);
        tv_defIncrease = v.findViewById(R.id.tv_decideIncrease_kr);
        tv_examIncrease = v.findViewById(R.id.tv_examIncrease_kr);
        tv_clearIncrease = v.findViewById(R.id.tv_clearIncrease_kr);
        tv_deathIncrease = v.findViewById(R.id.tv_deathIncrease_kr);

        tv_defCnt_world = v.findViewById(R.id.tv_decideCnt_world);
        tv_deathCnt_world = v.findViewById(R.id.tv_deathCnt_world);
        tv_defIncrease_world = v.findViewById(R.id.tv_decideIncrease_world);
        tv_deathIncrease_world = v.findViewById(R.id.tv_deathIncrease_world);

        cv_region = v.findViewById(R.id.cv_region);
        cv_isol = v.findViewById(R.id.cv_isol);
        cv_def = v.findViewById(R.id.cv_def);
        cv_clear = v.findViewById(R.id.cv_clear);
        cv_death = v.findViewById(R.id.cv_death);

        tv_state_date_kr_label = v.findViewById(R.id.tv_state_date_kr_label);
        tv_state_date_nat_label = v.findViewById(R.id.tv_state_date_nat_label);

        listView = v.findViewById(R.id.listView);

        btnKorea = v.findViewById(R.id.btn_korea);
        btnWorld = v.findViewById(R.id.btn_world);
        conLayoutKorea = v.findViewById(R.id.constraintLayout_kr);
        conLayoutWorld = v.findViewById(R.id.constraintLayout_world);

        pb_defCnt_world = v.findViewById(R.id.pb_decideCnt_world);
        pb_deathCnt_world = v.findViewById(R.id.pb_deathCnt_world);
    }

    public StatFragment() {
        coronaKRStatus = new CoronaKoreaStatus();
        coronaRegStatus = new CoronaRegionalStatus();
        coronaNatStatus = new CoronaNationalStatus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        threadKr = new KoreaXMLParser();
        threadRg = new RegionalXMLParser();
        threadNt = new NationalXMLParser();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        viewInit(view);
        threadKr.start();
        threadNt.start();
        threadRg.start();

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

    // 스크롤뷰 내부의 리스트뷰 Height 지정
    private static void listViewSetHeight(BaseAdapter adapter, ListView listView) {
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(0, 0);
            totalHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //국내 현황 XML 파싱 클래스
    @SuppressLint("FieldLeak")
    class KoreaXMLParser extends Thread {
        @Override
        public void run() {
            boolean chk = coronaKRStatus.loadXML();
            if (chk) {
                coronaKRStatus.parseXML();
            } else {
                return;
            }

            //테스트할때만 로그 출력을 위해 주석해제
            //coronaKRStatus.printInfo();

            //UI 제어
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        tv_defCnt.setText(coronaKRStatus.newFmt_todayDecideCnt);
                        tv_defIncrease.setText(coronaKRStatus.newFmt_decideIncCnt + " ▲");
                        tv_examCnt.setText(coronaKRStatus.newFmt_todayExamCnt);
                        tv_examIncrease.setText(coronaKRStatus.newFmt_examIncCnt + " ▲");
                        tv_clearCnt.setText(coronaKRStatus.newFmt_todayClearCnt);
                        tv_clearIncrease.setText(coronaKRStatus.newFmt_clearIncCnt + " ▲");
                        tv_deathCnt.setText(coronaKRStatus.newFmt_todayDeathCnt);
                        tv_deathIncrease.setText(coronaKRStatus.newFmt_deathIncCnt + " ▲");

                        tv_state_date_kr_label.setText(coronaKRStatus.todayStateDate);
                    }
                });
            }
        }
    }


    //시 도별(국내) 현황 XML 파싱 클래스
    class RegionalXMLParser extends Thread {
        @Override
        public void run() {
            boolean chk = coronaRegStatus.loadXML();
            if (chk) {
                coronaRegStatus.parseXML();
            } else {
                return;
            }
            //UI 제어
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        // 통계 탭 하단 리스트뷰 동작 관련
                        ArrayList<CustomList> regions = new ArrayList<>();

                        // 데이터 하드코딩으로 삽입하는 부분
                        for (int i = 0; i < coronaRegStatus.gubunList.size(); i++) {

                            regions.add(new CustomList(coronaRegStatus.gubunList.get(i),
                                    coronaRegStatus.isolIngCntList.get(i),
                                    coronaRegStatus.defCntList.get(i),
                                    coronaRegStatus.defIncList.get(i),
                                    coronaRegStatus.isolClearCntList.get(i),
                                    coronaRegStatus.deathCntList.get(i)));
                        }
                        adapter = new CustomAdapter(regions);
                        listView.setAdapter(adapter);
                        listViewSetHeight(adapter, listView); // 높이 지정 함수 호출
                    }
                });
            }

        }
    }

    //세계 현황 XML 파싱 클래스
    class NationalXMLParser extends Thread {
        @Override
        public void run() {
            pb_defCnt_world.show();
            pb_deathCnt_world.show();
            boolean chk = coronaNatStatus.loadXML();
            if (chk) {
                coronaNatStatus.parseXML();
            } else {
                return;
            }

            //테스트할때만 로그 출력을 위해 주석해제
            //coronaNatStatus.printInfo();

            //UI 제어
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        pb_defCnt_world.hide();
                        tv_defCnt_world.setText("" + coronaNatStatus.newFmt_todayTotNatDefCnt);
                        tv_defIncrease_world.setText(coronaNatStatus.newFmt_natDefIncCnt + " ▲");
                        pb_deathCnt_world.hide();
                        tv_deathCnt_world.setText("" + coronaNatStatus.newFmt_todayTotNatDeathCnt);
                        tv_deathIncrease_world.setText(coronaNatStatus.newFmt_natDeathIncCnt + " ▲");

                        tv_state_date_nat_label.setText(coronaNatStatus.newFormatStateDate);
                    }
                });
            }
        }
    }
}