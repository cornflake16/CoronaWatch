package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StatFragment extends Fragment {
    protected final static int SUBTRACT_DAY = 7;   //금일 기준으로 n일 전까지의 데이터를 얻기 위한 빼기 연산에 사용하는 상수 값
    String UTF, urlBuilder;
    String SERVICE_URL, SERVICE_KEY;

    ConstraintLayout conLayoutKorea, conLayoutWorld;
    Button btnKorea, btnWorld;
    final DecimalFormat formatter;
    private SaveViewModel saveViewModel;

    SimpleDateFormat dateFormat_year, dateFormat_month, dateFormat_day;
    Date time;
    String sYear, sMonth, sDay, dayAgo, yesterday, today, where;
    int nYear, nMonth, nDay;
    int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private TextView tv_decideCnt, tv_examCnt, tv_clearCnt, tv_deathCnt;
    private TextView tv_decideIncrease, tv_examIncrease, tv_clearIncrease, tv_deathIncrease;
    TextView tv_decideCnt_world, tv_clearCnt_world, tv_deathCnt_world;
    TextView tv_decideIncrease_world, tv_clearIncrease_world, tv_deathIncrease_world;

    private void viewInit(View v) {
        tv_decideCnt = v.findViewById(R.id.tv_decideCnt_kr);
        tv_examCnt = v.findViewById(R.id.tv_examCnt_kr);
        tv_clearCnt = v.findViewById(R.id.tv_clearCnt_kr);
        tv_deathCnt = v.findViewById(R.id.tv_deathCnt_kr);
        tv_decideIncrease = v.findViewById(R.id.tv_decideIncrease_kr);
        tv_examIncrease = v.findViewById(R.id.tv_examIncrease_kr);
        tv_clearIncrease = v.findViewById(R.id.tv_clearIncrease_kr);
        tv_deathIncrease = v.findViewById(R.id.tv_deathIncrease_kr);

        tv_decideCnt_world = v.findViewById(R.id.tv_decideCnt_world);
        tv_clearCnt_world = v.findViewById(R.id.tv_clearCnt_world);
        tv_deathCnt_world = v.findViewById(R.id.tv_deathCnt_world);
        tv_decideIncrease_world = v.findViewById(R.id.tv_decideIncrease_world);
        tv_clearIncrease_world = v.findViewById(R.id.tv_clearIncrease_world);
        tv_deathIncrease_world = v.findViewById(R.id.tv_deathIncrease_world);

        btnKorea = v.findViewById(R.id.btn_korea);
        btnWorld = v.findViewById(R.id.btn_world);
        conLayoutKorea = v.findViewById(R.id.constraintLayout_kr);
        conLayoutWorld = v.findViewById(R.id.constraintLayout_world);
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

        formatter = new DecimalFormat("###,###");

        sYear = dateFormat_year.format(time);
        sMonth = dateFormat_month.format(time);
        sDay = dateFormat_day.format(time);

        nYear = Integer.parseInt(sYear);
        nMonth = Integer.parseInt(sMonth);
        nDay = Integer.parseInt(sDay);

        today = sYear + sMonth + sDay;
        dayAgo = calDate(nYear, nMonth, nDay, SUBTRACT_DAY);
    }

    private String calDate(int year, int month, int day, int subNumber) {   //n일 전의 date 반환하는 함수
        String date;

        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {    //윤년 계산
            days[1] = 29;
        } else {
            days[1] = 28;
        }

        if (subNumber >= day) {
            if (month != 1) {
                day += days[month - 1];
                day -= subNumber;
                month--;
            } else {
                day += days[12];
                day -= subNumber;
                month = 12;
                year--;
            }
        } else {
            day -= subNumber;
        }

        date = Integer.toString(year);

        if (month < 10) {
            date += "0" + month;
        } else {
            date += month;
        }

        if (day < 10) {
            date += "0" + day;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        saveViewModel = new ViewModelProvider(this).get(SaveViewModel.class);
        viewInit(view);
        XMLParse();

        final Drawable eraseBg = getResources().getDrawable(R.drawable.btn_stroke);
        final Drawable drawBg = getResources().getDrawable(R.drawable.btn_stroke_checked);
        btnKorea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(drawBg);
                btnWorld.setBackground(eraseBg);
                conLayoutWorld.setVisibility(View.INVISIBLE);
                conLayoutKorea.setVisibility(View.VISIBLE);
            }
        });

        btnWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKorea.setBackground(eraseBg);
                btnWorld.setBackground(drawBg);
                conLayoutKorea.setVisibility(View.INVISIBLE);
                conLayoutWorld.setVisibility(View.VISIBLE);
                where = "world";
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void XMLParse() {
        Log.i("TTT", "hi");
        try {
            urlBuilder = SERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + SERVICE_KEY + /*Service Key*/
                    "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                    "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                    "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo, UTF) + /*검색할 생성일 범위의 시작*/
                    "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(today, UTF);/*URL*//*검색할 생성일 범위의 종료*/
            Log.i("INFO_URL", "URL:" + urlBuilder);
            Thread threadKr, threadWorld;
            threadKr = new KoreaXMLParser();
            threadWorld = new WorldXMLParser();
            threadKr.start();
            threadWorld.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    class WorldXMLParser extends Thread {
        @Override
        public void run() {
            yesterday = calDate(nYear, nMonth, nDay, 1);
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                //웹서버 URL 지정
                url = new URL("https://disease.sh/v3/covid-19/historical/all?lastdays=2");
                //URL 접속
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), UTF));

                String line;
                StringBuilder page = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    Log.d("line:", line);
                    page.append(line);
                }

                JSONObject json = new JSONObject(page.toString());


                JSONArray cases = json.getJSONArray("cases");
                for (int i = 0; i < cases.length(); i++) {
                    json = cases.getJSONObject(i);

                    String[] casesArr = new String[2];

                    //  ((m)m/d(d)/(y)y 형식 <- 괄호는 없어질 수도 있는 부분
                    casesArr[0] = json.getString(Integer.parseInt(sMonth)
                            + "/" + Integer.parseInt(sDay) + "/" + Integer.parseInt(sYear.substring(1, 3)));
                    Log.i("WORLD", "itemArr[" + i + "] = " + casesArr[0]);
                    casesArr[1] = Integer.parseInt(yesterday.substring(4, 5))
                            + "/" + Integer.parseInt(yesterday.substring(6, 7)) + "/" + Integer.parseInt(yesterday.substring(0, 3));
                    Log.i("WORLD", "itemArr[" + i + "] = " + casesArr[1]);
                }

                JSONArray recovered = json.getJSONArray("recovered");
                for (int i = 0; i < recovered.length(); i++) {
                    json = recovered.getJSONObject(i);

                    String[] recoveredArr = new String[2];

                    //  ((m)m/d(d)/(y)y 형식 <- 괄호는 없어질 수도 있는 부분
                    recoveredArr[0] = json.getString(Integer.parseInt(sMonth)
                            + "/" + Integer.parseInt(sDay) + "/" + Integer.parseInt(sYear.substring(1, 3)));
                    Log.i("WORLD", "itemArr[" + i + "] = " + recoveredArr[0]);
                    recoveredArr[1] = Integer.parseInt(yesterday.substring(4, 5))
                            + "/" + Integer.parseInt(yesterday.substring(6, 7)) + "/" + Integer.parseInt(yesterday.substring(0, 3));
                    Log.i("WORLD", "itemArr[" + i + "] = " + recoveredArr[1]);
                }

                JSONArray deaths = json.getJSONArray("deaths");
                for (int i = 0; i < deaths.length(); i++) {
                    json = deaths.getJSONObject(i);

                    String[] deathsArr = new String[2];

                    //  ((m)m/d(d)/(y)y 형식 <- 괄호는 없어질 수도 있는 부분
                    deathsArr[0] = json.getString(Integer.parseInt(sMonth)
                            + "/" + Integer.parseInt(sDay) + "/" + Integer.parseInt(sYear.substring(1, 3)));
                    Log.i("WORLD", "itemArr[" + i + "] = " + deathsArr[0]);
                    deathsArr[1] = Integer.parseInt(yesterday.substring(4, 5))
                            + "/" + Integer.parseInt(yesterday.substring(6, 7)) + "/" + Integer.parseInt(yesterday.substring(0, 3));
                    Log.i("WORLD", "itemArr[" + i + "] = " + deathsArr[1]);
                }
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ArrayList<String> strList = new ArrayList<>();
//                            ArrayList<TextView> tvList = new ArrayList<>();
//                            tvList.add(tv_decideCnt_world);
//                            tvList.add(tv_decideIncrease_world);
//                            tvList.add(tv_clearCnt_world);
//                            tvList.add(tv_clearIncrease_world);
//                            tvList.add(tv_deathCnt_world);
//                            tvList.add(tv_deathIncrease_world);
//
//                            tvList.add(tv_decideCnt_world);
//                            for (String[] strArr : itemArrList) {
//                                strList.addAll(Arrays.asList(strArr));
//                            }
//                            for (int i = 0; i < strList.size(); i++) {
//                                tvList.get(i).setText(strList.get(i));
//                            }
//                        }
//                    });
//                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                //URL 연결 해제
                assert urlConnection != null;
                urlConnection.disconnect();
            }

            Log.i("INFO_URL", "URL: " + url);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class KoreaXMLParser extends Thread {
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
            }

            assert doc != null;
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