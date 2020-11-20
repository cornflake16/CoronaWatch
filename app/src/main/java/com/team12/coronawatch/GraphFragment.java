package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    Spinner spinner;
    BarChart defBarChart, examBarChart, clearBarChart, deathBarChart;
    CoronaKoreaStatus coronaKRStatus;
    KoreaXMLParser koreaXMLParser;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coronaKRStatus = new CoronaKoreaStatus();
        koreaXMLParser = new KoreaXMLParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        koreaXMLParser.start();
        spinner = v.findViewById(R.id.spinner);
        defBarChart = v.findViewById(R.id.defBarChart);
        examBarChart = v.findViewById(R.id.examBarChart);
        clearBarChart = v.findViewById(R.id.clearBarChart);
        deathBarChart = v.findViewById(R.id.deathBarChart);

        String[] items = getResources().getStringArray(R.array.graph_tab);
        final ArrayAdapter aa_items = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(aa_items);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        defBarChart.setVisibility(View.VISIBLE);
                        examBarChart.setVisibility(View.INVISIBLE);
                        clearBarChart.setVisibility(View.INVISIBLE);
                        deathBarChart.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        defBarChart.setVisibility(View.INVISIBLE);
                        examBarChart.setVisibility(View.VISIBLE);
                        clearBarChart.setVisibility(View.INVISIBLE);
                        deathBarChart.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        defBarChart.setVisibility(View.INVISIBLE);
                        examBarChart.setVisibility(View.INVISIBLE);
                        clearBarChart.setVisibility(View.VISIBLE);
                        deathBarChart.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        defBarChart.setVisibility(View.INVISIBLE);
                        examBarChart.setVisibility(View.INVISIBLE);
                        clearBarChart.setVisibility(View.INVISIBLE);
                        deathBarChart.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            // Inflate the layout for this fragment
        });
        return v;
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
                        ArrayList<BarEntry> decideCntBE = new ArrayList<>();
                        ArrayList<BarEntry> examCntBE = new ArrayList<>();
                        ArrayList<BarEntry> clearCntBE = new ArrayList<>();
                        ArrayList<BarEntry> deathCntBE = new ArrayList<>();

                        ArrayList<String> createDtList = coronaKRStatus.createDtList;
                        ArrayList<Long> decideCntList = coronaKRStatus.decideCntList;
                        ArrayList<Long> examCntList = coronaKRStatus.examCntList;
                        ArrayList<Long> clearCntList = coronaKRStatus.clearCntList;
                        ArrayList<Long> deathCntList = coronaKRStatus.deathCntList;

                        for (int i = 0; i < 7; i++) {
                            decideCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), decideCntList.get(i)));
                            examCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), examCntList.get(i)));
                            clearCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), clearCntList.get(i)));
                            deathCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), deathCntList.get(i)));
                        }

                        BarDataSet defBDS = new BarDataSet(decideCntBE, "확진자 수(명)");
                        defBDS.setColor(Color.parseColor("#CCE65100"));
                        defBDS.setValueTextColor(Color.BLACK);
                        defBDS.setValueTextSize(10f);
                        defBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet examBDS = new BarDataSet(examCntBE, "검사자 수(명)");
                        examBDS.setColor(Color.parseColor("#CCE65100"));
                        examBDS.setValueTextColor(Color.BLACK);
                        examBDS.setValueTextSize(10f);
                        examBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet clearBDS = new BarDataSet(clearCntBE, "격리해제 수(명)");
                        clearBDS.setColor(Color.parseColor("#CCE65100"));
                        clearBDS.setValueTextColor(Color.BLACK);
                        clearBDS.setValueTextSize(10f);
                        clearBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet deathBDS = new BarDataSet(deathCntBE, "사망자 수(명)");
                        deathBDS.setColor(Color.parseColor("#CCE65100"));
                        deathBDS.setValueTextColor(Color.BLACK);
                        deathBDS.setValueTextSize(10f);
                        deathBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarData defBD = new BarData(defBDS);
                        BarData examBD = new BarData(examBDS);
                        BarData clearBD = new BarData(clearBDS);
                        BarData deathBD = new BarData(deathBDS);

                        defBarChart.setFitBars(true);
                        defBarChart.setData(defBD);
                        defBarChart.getDescription().setText("일주일간 확진환자 수 그래프");
                        defBarChart.animateX(2000);
                        defBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        defBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        examBarChart.setFitBars(true);
                        examBarChart.setData(examBD);
                        examBarChart.getDescription().setText("일주일간 검사자 수 그래프");
                        examBarChart.animateX(2000);
                        examBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        examBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));


                        clearBarChart.setFitBars(true);
                        clearBarChart.setData(clearBD);
                        clearBarChart.getDescription().setText("일주일간 격리해제 수 그래프");
                        clearBarChart.animateX(2000);
                        clearBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        clearBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        deathBarChart.setFitBars(true);
                        deathBarChart.setData(deathBD);
                        deathBarChart.getDescription().setText("일주일간 사망자 수 그래프");
                        deathBarChart.animateX(2000);
                        deathBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        deathBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));
                    }
                });
            }
        }
    }
}
