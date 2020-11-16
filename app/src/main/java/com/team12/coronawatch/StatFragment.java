package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StatFragment extends Fragment {
    String UTF, urlBuilder;
    String SERVICE_URL, SERVICE_KEY;
    String decideCnt, examCnt, clearCnt, deathCnt;
    String decideIncrease, examIncrease, clearIncrease, deathIncrease;

    Button btnKorea, btnWorld;

    SimpleDateFormat dateFormat_year, dateFormat_month, dateFormat_day;
    Date time;
    String sYear, sMonth, sDay, dayAgo, today;
    int nYear, nMonth, nDay;
    int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private TextView tv_decideCnt, tv_examCnt, tv_clearCnt, tv_deathCnt;
    private TextView tv_decideIncrease, tv_examIncrease, tv_clearIncrease, tv_deathIncrease;

    private void viewInit(View v) {
        tv_decideCnt = v.findViewById(R.id.tv_decideCnt);
        tv_examCnt = v.findViewById(R.id.tv_examCnt);
        tv_clearCnt = v.findViewById(R.id.tv_clearCnt);
        tv_deathCnt = v.findViewById(R.id.tv_deathCnt);
        tv_decideIncrease = v.findViewById(R.id.tv_decideIncrease);
        tv_examIncrease = v.findViewById(R.id.tv_examIncrease);
        tv_clearIncrease = v.findViewById(R.id.tv_clearIncrease);
        tv_deathIncrease = v.findViewById(R.id.tv_deathIncrease);
        btnKorea = v.findViewById(R.id.btn_korea);
        btnWorld = v.findViewById(R.id.btn_world);
    }

    public StatFragment() {
        // Required empty public constructor
        UTF = "UTF-8";
        SERVICE_URL = "http://openapi.data.go.kr/openapi/service/" +
                "rest/Covid19/getCovid19InfStateJson";
        SERVICE_KEY = "=1S8z1o0Mg6QxYGxG5z3Efb87G2YqofNJcnFv4L47ru7gPncj2MRdlVu" +
                "%2BK6uitzbqYnf6BSl19%2FXCXMuqtrXx8w%3D%3D";
        dateFormat_year = new SimpleDateFormat("yyyy", Locale.getDefault());
        dateFormat_month = new SimpleDateFormat("MM", Locale.getDefault());
        dateFormat_day = new SimpleDateFormat("dd", Locale.getDefault());
        time = new Date();

        sYear = dateFormat_year.format(time);
        sMonth = dateFormat_month.format(time);
        sDay = dateFormat_day.format(time);

        nYear = Integer.parseInt(sYear);
        nMonth = Integer.parseInt(sMonth);
        nDay = Integer.parseInt(sDay);

        today = sYear + sMonth + sDay;
        dayAgo = calDate(nYear, nMonth, nDay);
    }

    private String calDate(int year, int month, int day) {   //n일 전의 date 반환하는 함수
        String date;

        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {    //윤년 계산
            days[1] = 29;
        } else {
            days[1] = 28;
        }

        if (CoronaXMLParser.SUBTRACT_DAY >= day) {
            if (month != 1) {
                day += days[month - 1];
                day -= CoronaXMLParser.SUBTRACT_DAY;
                month--;
            } else {
                day += days[12];
                day -= CoronaXMLParser.SUBTRACT_DAY;
                month = 12;
                year--;
            }
        } else {
            day -= CoronaXMLParser.SUBTRACT_DAY;
        }

        date = year + Integer.toString(month);
        if (day < 10) {
            date += 0 + "" + day;
        } else {
            date += day;
        }

        return date;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        viewInit(view);
        if (savedInstanceState == null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences("tvInfo", 0);

            decideCnt = prefs.getString("decideCnt", "");
            examCnt = prefs.getString("examCnt", "");
            clearCnt = prefs.getString("clearCnt", "");
            deathCnt = prefs.getString("deathCnt", "");
            decideIncrease = prefs.getString("decideIncrease", "");
            examIncrease = prefs.getString("examIncrease", "");
            clearIncrease = prefs.getString("clearIncrease", "");
            deathIncrease = prefs.getString("deathIncrease", "");

            tv_decideCnt.setText(decideCnt);
            tv_examCnt.setText(examCnt);
            tv_clearCnt.setText(clearCnt);
            tv_deathCnt.setText(deathCnt);
            tv_decideIncrease.setText(decideIncrease);
            tv_examIncrease.setText(examIncrease);
            tv_clearIncrease.setText(clearIncrease);
            tv_deathIncrease.setText(deathIncrease);
        }
        if (tv_decideCnt.getText().toString().equals("")) {
            XMLParse();
        }

        final Drawable eraseBg = getResources().getDrawable(R.drawable.btn_stroke);
        final Drawable drawBg = getResources().getDrawable(R.drawable.btn_stroke_checked);
        btnKorea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(drawBg);
                btnWorld.setBackground(eraseBg);
            }
        });

        btnWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(eraseBg);
                btnWorld.setBackground(drawBg);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences prefs = this.getActivity().getSharedPreferences("tvInfo", 0);

        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = prefs.edit();

        decideCnt = tv_decideCnt.getText().toString();
        examCnt = tv_examCnt.getText().toString();
        clearCnt = tv_clearCnt.getText().toString();
        deathCnt = tv_deathCnt.getText().toString();
        decideIncrease = tv_decideIncrease.getText().toString();
        examIncrease = tv_examIncrease.getText().toString();
        clearIncrease = tv_clearIncrease.getText().toString();
        deathIncrease = tv_deathIncrease.getText().toString();

        editor.putString("decideCnt", decideCnt);
        editor.putString("examCnt", examCnt);
        editor.putString("clearCnt", clearCnt);
        editor.putString("deathCnt", deathCnt);
        editor.putString("decideIncrease", decideIncrease);
        editor.putString("examIncrease", examIncrease);
        editor.putString("clearIncrease", clearIncrease);
        editor.putString("deathIncrease", deathIncrease);
        editor.apply();
    }

    private void XMLParse() {
        try {
            urlBuilder = SERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + SERVICE_KEY + /*Service Key*/
                    "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                    "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                    "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo, UTF) + /*검색할 생성일 범위의 시작*/
                    "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(today, UTF);/*URL*//*검색할 생성일 범위의 종료*/
            Log.i("INFO_URL", "URL:" + urlBuilder);
            Thread thread = new CoronaXMLParser();
            thread.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class CoronaXMLParser extends Thread {
        protected final static int SUBTRACT_DAY = 7;   //금일 기준으로 n일 전까지의 데이터를 얻기 위한 빼기 연산에 사용하는 상수 값
        String[] stateDateArr = new String[SUBTRACT_DAY];
        int[] decideCntArr = new int[SUBTRACT_DAY];
        int[] examCntArr = new int[SUBTRACT_DAY];
        int[] clearCntArr = new int[SUBTRACT_DAY];
        int[] deathCntArr = new int[SUBTRACT_DAY];
        int increaseDecideCnt, increaseExamCnt, increaseClearCnt, increaseDeathCnt;

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            Log.i("INFO_IS_JOIN", "is in doInBackground? => yes");
            Log.i("INFO_PARAMETER", "strings[0] => " + urlBuilder);
            Document doc = null;
            try {
                URL url = new URL(urlBuilder);
                Log.i("INFO_URL", "URL: " + url);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
                Log.d("doInBackground()", "e : " + e);
            }

            final DecimalFormat formatter = new DecimalFormat("###,###");

            Element body = (Element) doc.getElementsByTagName("body").item(0);
            Element items = (Element) body.getElementsByTagName("items").item(0);

            Node stateDate, decideCnt, examCnt, clearCnt, deathCnt;
            for (int i = 0; i < SUBTRACT_DAY; i++) {
                Element item = (Element) items.getElementsByTagName("item").item(i);
                stateDate = item.getElementsByTagName("stateDt").item(0);    //확진자 수
                decideCnt = item.getElementsByTagName("decideCnt").item(0);    //확진자 수
                examCnt = item.getElementsByTagName("examCnt").item(0);    //검사자 수
                clearCnt = item.getElementsByTagName("clearCnt").item(0);  //격리해제 수
                deathCnt = item.getElementsByTagName("deathCnt").item(0);  //사망자 수

                stateDateArr[i] = stateDate.getChildNodes().item(0).getNodeValue();
                decideCntArr[i] = Integer.parseInt(decideCnt.getChildNodes().item(0).getNodeValue());
                examCntArr[i] = Integer.parseInt(examCnt.getChildNodes().item(0).getNodeValue());
                clearCntArr[i] = Integer.parseInt(clearCnt.getChildNodes().item(0).getNodeValue());
                deathCntArr[i] = Integer.parseInt(deathCnt.getChildNodes().item(0).getNodeValue());
            }

            increaseDecideCnt = decideCntArr[0] - decideCntArr[1];
            increaseExamCnt = examCntArr[0] - examCntArr[1];
            increaseClearCnt = clearCntArr[0] - clearCntArr[1];
            increaseDeathCnt = deathCntArr[0] - deathCntArr[1];

            for (int i = 0; i < SUBTRACT_DAY; i++) {
                Log.i("STAT_", "--- [ " + stateDateArr[i] + " ] ---");
                Log.i("STAT_", "확진환자: " + decideCntArr[i]);
                Log.i("STAT_", "검사중: " + examCntArr[i]);
                Log.i("STAT_", "격리해제: " + clearCntArr[i]);
                Log.i("STAT_", "사망자: " + deathCntArr[i]);
                Log.i("STAT_", "-----------------------------------");
            }
            Log.i("STAT_", "확진환자 증가수: " + increaseDecideCnt);
            Log.i("STAT_", "검사중 증가수: " + increaseExamCnt);
            Log.i("STAT_", "격리해제 증가수: " + increaseClearCnt);
            Log.i("STAT_", "사망자 증가수: " + increaseDeathCnt);

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_decideCnt.setText("" + formatter.format(decideCntArr[0]));
                        tv_examCnt.setText("" + formatter.format(examCntArr[0]));
                        tv_clearCnt.setText("" + formatter.format(clearCntArr[0]));
                        tv_deathCnt.setText("" + formatter.format(deathCntArr[0]));
                        tv_decideIncrease.setText(formatter.format(increaseDecideCnt) + " ▲");
                        tv_examIncrease.setText(formatter.format(increaseExamCnt) + " ▲");
                        tv_clearIncrease.setText(formatter.format(increaseClearCnt) + " ▲");
                        tv_deathIncrease.setText(formatter.format(increaseDeathCnt) + " ▲");
                    }
                });
            }
        }
    }
}