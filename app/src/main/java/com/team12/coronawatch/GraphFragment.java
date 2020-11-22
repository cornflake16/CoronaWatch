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
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    Spinner spinner;
    BarChart defBarChart, examBarChart, clearBarChart, deathBarChart,
            defIncBarChart, examIncBarChart, clearIncBarChart, deathIncBarChart;
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
        defIncBarChart = v.findViewById(R.id.defIncBarChart);
        examBarChart = v.findViewById(R.id.examBarChart);
        examIncBarChart = v.findViewById(R.id.examIncBarChart);
        clearBarChart = v.findViewById(R.id.clearBarChart);
        clearIncBarChart = v.findViewById(R.id.clearIncBarChart);
        deathBarChart = v.findViewById(R.id.deathBarChart);
        deathIncBarChart = v.findViewById(R.id.deathIncBarChart);

        String[] items = getResources().getStringArray(R.array.graph_tab);
        final ArrayAdapter aa_items = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(aa_items);

        final ArrayList<BarChart> bcList = new ArrayList<>();
        bcList.add(defBarChart);
        bcList.add(defIncBarChart);
        bcList.add(examBarChart);
        bcList.add(examIncBarChart);
        bcList.add(clearBarChart);
        bcList.add(clearIncBarChart);
        bcList.add(deathBarChart);
        bcList.add(deathIncBarChart);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (BarChart bc : bcList)
                    bc.setVisibility(View.INVISIBLE);
                bcList.get(position).setVisibility(View.VISIBLE);
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
                        ArrayList<BarEntry> defCntBE = new ArrayList<>();
                        ArrayList<BarEntry> defIncCntBE = new ArrayList<>();
                        ArrayList<BarEntry> examCntBE = new ArrayList<>();
                        ArrayList<BarEntry> examIncCntBE = new ArrayList<>();
                        ArrayList<BarEntry> clearCntBE = new ArrayList<>();
                        ArrayList<BarEntry> clearIncCntBE = new ArrayList<>();
                        ArrayList<BarEntry> deathCntBE = new ArrayList<>();
                        ArrayList<BarEntry> deathIncCntBE = new ArrayList<>();

                        ArrayList<String> createDtList = coronaKRStatus.createDtList;
                        ArrayList<Long> defCntList = coronaKRStatus.decideCntList;
                        ArrayList<Long> defIncCntList = coronaKRStatus.decideIncCntList;
                        ArrayList<Long> examCntList = coronaKRStatus.examCntList;
                        ArrayList<Long> examIncCntList = coronaKRStatus.examIncCntList;
                        ArrayList<Long> clearCntList = coronaKRStatus.clearCntList;
                        ArrayList<Long> clearIncCntList = coronaKRStatus.clearIncCntList;
                        ArrayList<Long> deathCntList = coronaKRStatus.deathCntList;
                        ArrayList<Long> deathIncCntList = coronaKRStatus.deathIncCntList;

                        for (int i = 0; i < 7; i++) {
                            defCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), defCntList.get(i)));
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
                        for (int i = 0; i < 6; i++) {
                            defIncCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), defIncCntList.get(i)));
                            examIncCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), examIncCntList.get(i)));
                            clearIncCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), clearIncCntList.get(i)));
                            deathIncCntBE.add(
                                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                                            createDtList.get(i).substring(8, 10)), deathIncCntList.get(i)));
                        }

                        BarDataSet defBDS = new BarDataSet(defCntBE, "확진자 수(명)");
                        defBDS.setColor(Color.parseColor("#CCE65100"));
                        defBDS.setValueTextColor(Color.BLACK);
                        defBDS.setValueTextSize(10f);
                        defBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet defIncBDS = new BarDataSet(defIncCntBE, "전일 대비 확진자 증감 수(명)");
                        defIncBDS.setColor(Color.parseColor("#CCE65100"));
                        defIncBDS.setValueTextColor(Color.BLACK);
                        defIncBDS.setValueTextSize(10f);
                        defIncBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet examBDS = new BarDataSet(examCntBE, "검사진행 수(명)");
                        examBDS.setColor(Color.parseColor("#CCE65100"));
                        examBDS.setValueTextColor(Color.BLACK);
                        examBDS.setValueTextSize(10f);
                        examBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet examIncBDS = new BarDataSet(examIncCntBE, "전일 대비 검사진행 증감 수(명)");
                        examIncBDS.setColor(Color.parseColor("#CCE65100"));
                        examIncBDS.setValueTextColor(Color.BLACK);
                        examIncBDS.setValueTextSize(10f);
                        examIncBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet clearBDS = new BarDataSet(clearCntBE, "격리해제 수(명)");
                        clearBDS.setColor(Color.parseColor("#CCE65100"));
                        clearBDS.setValueTextColor(Color.BLACK);
                        clearBDS.setValueTextSize(10f);
                        clearBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet clearIncBDS = new BarDataSet(clearIncCntBE, "전일 대비 격리해제 증감 수(명)");
                        clearIncBDS.setColor(Color.parseColor("#CCE65100"));
                        clearIncBDS.setValueTextColor(Color.BLACK);
                        clearIncBDS.setValueTextSize(10f);
                        clearIncBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet deathBDS = new BarDataSet(deathCntBE, "사망자 수(명)");
                        deathBDS.setColor(Color.parseColor("#CCE65100"));
                        deathBDS.setValueTextColor(Color.BLACK);
                        deathBDS.setValueTextSize(10f);
                        deathBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarDataSet deathIncBDS = new BarDataSet(deathIncCntBE, "전일 대비 사망자 증감 수(명)");
                        deathIncBDS.setColor(Color.parseColor("#CCE65100"));
                        deathIncBDS.setValueTextColor(Color.BLACK);
                        deathIncBDS.setValueTextSize(10f);
                        deathIncBDS.setValueTypeface(Typeface.DEFAULT_BOLD);

                        BarData defBD = new BarData(defBDS);
                        BarData defIncBD = new BarData(defIncBDS);
                        BarData examBD = new BarData(examBDS);
                        BarData examIncBD = new BarData(examIncBDS);
                        BarData clearBD = new BarData(clearBDS);
                        BarData clearIncBD = new BarData(clearIncBDS);
                        BarData deathBD = new BarData(deathBDS);
                        BarData deathIncBD = new BarData(deathIncBDS);

                        defBarChart.setFitBars(true);
                        defBarChart.setData(defBD);
                        defBarChart.getDescription().setText("일주일간 확진자 수 그래프");
                        defBarChart.getDescription().setTextSize(11f);
                        defBarChart.animateY(3000);
                        defBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        defBarChart.getXAxis().setTextSize(12f);
                        defBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        defIncBarChart.setFitBars(true);
                        defIncBarChart.setData(defIncBD);
                        defIncBarChart.getDescription().setText("일주일간 확진자 증감 그래프");
                        defIncBarChart.getDescription().setTextSize(11f);
                        defIncBarChart.animateY(3000);
                        defIncBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        defIncBarChart.getXAxis().setTextSize(12f);
                        defIncBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        examBarChart.setFitBars(true);
                        examBarChart.setData(examBD);
                        examBarChart.getDescription().setText("일주일간 검사진행 수 그래프");
                        examBarChart.animateY(3000);
                        examBarChart.getDescription().setTextSize(11f);
                        examBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        examBarChart.getXAxis().setTextSize(12f);
                        examBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        examIncBarChart.setFitBars(true);
                        examIncBarChart.setData(examIncBD);
                        examIncBarChart.getDescription().setText("일주일간 검사진행 증감 그래프");
                        examIncBarChart.getDescription().setTextSize(11f);
                        examIncBarChart.animateY(3000);
                        examIncBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        examIncBarChart.getXAxis().setTextSize(12f);
                        examIncBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        clearBarChart.setFitBars(true);
                        clearBarChart.setData(clearBD);
                        clearBarChart.getDescription().setText("일주일간 격리해제 수 그래프");
                        clearBarChart.animateY(3000);
                        clearBarChart.getDescription().setTextSize(11f);
                        clearBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        clearBarChart.getXAxis().setTextSize(12f);
                        clearBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        clearIncBarChart.setFitBars(true);
                        clearIncBarChart.setData(clearIncBD);
                        clearIncBarChart.getDescription().setText("일주일간 격리해제 증감 그래프");
                        clearIncBarChart.getDescription().setTextSize(11f);
                        clearIncBarChart.animateY(3000);
                        clearIncBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        clearIncBarChart.getXAxis().setTextSize(12f);
                        clearIncBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return super.getFormattedValue(value);
                            }
                        });

                        deathBarChart.setFitBars(true);
                        deathBarChart.setData(deathBD);
                        deathBarChart.getDescription().setText("일주일간 사망자 수 그래프");
                        deathBarChart.animateY(3000);
                        deathBarChart.getDescription().setTextSize(11f);
                        deathBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        deathBarChart.getXAxis().setTextSize(12f);
                        deathBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));

                        deathIncBarChart.setFitBars(true);
                        deathIncBarChart.setData(deathIncBD);
                        deathIncBarChart.getDescription().setText("일주일간 사망자 증감 그래프");
                        deathIncBarChart.getDescription().setTextSize(11f);
                        deathIncBarChart.animateY(3000);
                        deathIncBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        deathIncBarChart.getXAxis().setTextSize(12f);
                        deathIncBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(createDtList));
                    }
                });
            }
        }
    }
}
