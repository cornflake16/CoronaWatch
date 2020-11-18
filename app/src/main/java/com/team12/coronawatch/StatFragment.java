package com.team12.coronawatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StatFragment extends Fragment {
    final static int SUBTRACT_DAY = 7;
    String UTF, krUrlBuilder, natUrlBuilder;
    String natSERVICE_URL, natSERVICE_KEY,
            krSERVICE_URL, krSERVICE_KEY;
    MyViewModel vm;
    ConstraintLayout conLayoutKorea, conLayoutWorld;
    final DecimalFormat formatter;
    long totalNatDefCnt, totalNatDeathCnt;

    SimpleDateFormat dateFormat_year, dateFormat_month, dateFormat_day;
    Date time;
    String sYear, sMonth, sDay, today;
    int nYear, nMonth, nDay;
    static int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    TextView tv_decideCnt, tv_examCnt, tv_clearCnt, tv_deathCnt;
    TextView tv_decideIncrease, tv_examIncrease, tv_clearIncrease, tv_deathIncrease;
    TextView tv_decideCnt_world, tv_deathCnt_world;
    TextView tv_decideIncrease_world, tv_deathIncrease_world;

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
        tv_deathCnt_world = v.findViewById(R.id.tv_deathCnt_world);
        tv_decideIncrease_world = v.findViewById(R.id.tv_decideIncrease_world);
        tv_deathIncrease_world = v.findViewById(R.id.tv_deathIncrease_world);

        vm.btnKorea = v.findViewById(R.id.btn_korea);
        vm.btnWorld = v.findViewById(R.id.btn_world);
        conLayoutKorea = v.findViewById(R.id.constraintLayout_kr);
        conLayoutWorld = v.findViewById(R.id.constraintLayout_world);
    }

    public StatFragment() {
        // Required empty public constructor
        UTF = "UTF-8";

        //보건 복지부_코로나_국내 발생현황 - 엔드 포인트(URL) + 일반 인증키(UTF-8)
        krSERVICE_URL = "http://openapi.data.go.kr/openapi/service/" +
                "rest/Covid19/getCovid19InfStateJson";
        krSERVICE_KEY = "=1S8z1o0Mg6QxYGxG5z3Efb87G2YqofNJcnFv4L47ru7gPncj2MRdlVu" +
                "%2BK6uitzbqYnf6BSl19%2FXCXMuqtrXx8w%3D%3D";
        //보건 복지부_코로나_해외 발생현황 - 엔드 포인트(URL) + 일반 인증키(UTF-8)
        natSERVICE_URL = "http://openapi.data.go.kr/openapi/service/rest/Covid19/" +
                "getCovid19NatInfStateJson";
        natSERVICE_KEY = "=1S8z1o0Mg6QxYGxG5z3Efb87G2YqofNJcnFv4L47ru7gPncj2MRdlVu" +
                "%2BK6uitzbqYnf6BSl19%2FXCXMuqtrXx8w%3D%3D"; //보건복지부_코로나19해외발생_현황 일반 인증키(UTF-8)

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
    }

    public String dayAgo(int subNum) {
        return calDate(nYear, nMonth, nDay, subNum);
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
        vm = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
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
        XMLParse();

        final Drawable eraseBg = StatFragment.this.getResources().getDrawable(R.drawable.btn_stroke);
        final Drawable drawBg = StatFragment.this.getResources().getDrawable(R.drawable.btn_stroke_checked);
        vm.btnKorea.setSelected(true);
        vm.btnWorld.setSelected(true);

        vm.btnKorea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.btnKorea.setBackground(drawBg);
                vm.btnWorld.setBackground(eraseBg);
                conLayoutWorld.setVisibility(View.INVISIBLE);
                conLayoutKorea.setVisibility(View.VISIBLE);
                vm.btnFlag = false;

            }
        });

        vm.btnWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.btnKorea.setBackground(eraseBg);
                vm.btnWorld.setBackground(drawBg);
                conLayoutKorea.setVisibility(View.INVISIBLE);
                conLayoutWorld.setVisibility(View.VISIBLE);
                vm.btnFlag = true;
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void XMLParse() {
        try {
            krUrlBuilder = krSERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + krSERVICE_KEY + /*Service Key*/
                    "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                    "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                    "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(7), UTF) + /*검색할 생성일 범위의 시작*/
                    "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(0), UTF);/*URL*//*검색할 생성일 범위의 종료*/
            natUrlBuilder = natSERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + natSERVICE_KEY + /*Service Key*/
                    "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                    "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                    "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(1), UTF) + /*검색할 생성일 범위의 시작*/
                    "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(0), UTF);/*URL*//*검색할 생성일 범위의 종료*/

            Log.i("XMLParse()", "URL:" + natUrlBuilder);
            Thread threadKr, threadNat;
            threadKr = new KoreaXMLParser();
            threadNat = new NationalXMLParser();
            threadKr.start();
            threadNat.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    class NationalXMLParser extends Thread {
        String totalDefNum, totalDeathNum;

        @Override
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(natUrlBuilder);
                Log.i("INFO_URL", "URL: " + url);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                Log.d("KoreaXMLParser()", e.getMessage());
            }

            assert doc != null;
            Element body = (Element) doc.getElementsByTagName("body").item(0);
            Element items = (Element) body.getElementsByTagName("items").item(0);

            Node areaNm, areaNmEn, nationNm, nationNmEn, natDefCnt, natDeathCnt, natDeathRate, stdDt;
            ArrayList<NationInfo> natInfoList = new ArrayList<>();
            String standardDate = "-";
            int i = 0;
            while (true) {
                NationInfo nationInfo = new NationInfo();
                Element item = (Element) items.getElementsByTagName("item").item(i);
                if (i++ == 0) {
                    stdDt = item.getElementsByTagName("stdDay").item(0);
                    standardDate = stdDt.getChildNodes().item(0).getNodeValue();
                }
                if (item == null) {
                    break;
                }
                areaNm = item.getElementsByTagName("areaNm").item(0);    //지역명
                areaNmEn = item.getElementsByTagName("areaNmEn").item(0);    //지역명(영문)
                nationNm = item.getElementsByTagName("nationNm").item(0);    //국가명
                nationNmEn = item.getElementsByTagName("nationNmEn").item(0);    //국가명(영문)
                natDefCnt = item.getElementsByTagName("natDefCnt").item(0);    //확진자 수(국가별)
                natDeathCnt = item.getElementsByTagName("natDeathCnt").item(0);    //사망자 수(국가별
                natDeathRate = item.getElementsByTagName("natDeathRate").item(0);    //확진자 대비 사망률

                nationInfo.setAreaNm(areaNm.getChildNodes().item(0).getNodeValue());
                nationInfo.setAreaNmEn(areaNmEn.getChildNodes().item(0).getNodeValue());
                nationInfo.setNationNm(nationNm.getChildNodes().item(0).getNodeValue());
                nationInfo.setNationNmEn(nationNmEn.getChildNodes().item(0).getNodeValue());
                nationInfo.setNatDefCnt(natDefCnt.getChildNodes().item(0).getNodeValue());
                nationInfo.setNatDeathCnt(natDeathCnt.getChildNodes().item(0).getNodeValue());
                nationInfo.setNatDeathRate(natDeathRate.getChildNodes().item(0).getNodeValue());

                natInfoList.add(nationInfo);
            }
            int n = 0;
            totalNatDefCnt = totalNatDeathCnt = 0;
            for (NationInfo natInfo : natInfoList) {
                //파싱 정상적으로 안되는 경우, 주석 풀고 테스트
//                Log.i("NationalStat", "----------------------------------------");
//                Log.i("NationalStat", "#" + ++n);
//                Log.i("NationalStat", "지역명: " + natInfo.getAreaNm());
//                Log.i("NationalStat", "지역명_영문: " + natInfo.getAreaNmEn());
//                Log.i("NationalStat", "국가명: " + natInfo.getNationNm());
//                Log.i("NationalStat", "국가명_영문: " + natInfo.getNationNmEn());
//                Log.i("NationalStat", "확진자 수: " + formatter.format(natInfo.getNatDefCnt()) + "명");
//                Log.i("NationalStat", "사망자 수: " + formatter.format(natInfo.getNatDeathCnt()) + "명");
//                Log.i("NationalStat", "확진자 대비 사망률: " + Math.round(natInfo.getNatDeathRate() * 100) / 100 + "%");
                totalNatDefCnt += natInfo.getNatDefCnt();
                totalNatDeathCnt += natInfo.getNatDeathCnt();
            }
            totalDefNum = formatter.format(totalNatDefCnt);
            totalDeathNum = formatter.format(totalNatDeathCnt);
//            Log.i("NationalStat", "----------------------------------------");
//            Log.i("NationalStat", "기준 일시: " + standardDate);
//            Log.i("NationalStat", "총 확진자 수: " + totalDefNum + "명");
//            Log.i("NationalStat", "총 사망자 수: " + totalDeathNum + "명");

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        tv_decideCnt_world.setText("" + totalDefNum);
                        tv_deathCnt_world.setText("" + totalDeathNum);
                    }
                });
            }
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
            Log.i("INFO_PARAMETER", "strings[0] => " + krUrlBuilder);
            Document doc = null;
            try {
                URL url = new URL(krUrlBuilder);
                Log.i("INFO_URL", "URL: " + url);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                Log.d("KoreaXMLParser()", e.getMessage());
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

            //파싱 정상적으로 안되는 경우, 주석 풀고 테스트
//            for (int i = 0; i < SUBTRACT_DAY; i++) {
//                Log.i("STAT_", "--- [ " + stateDateArr[i] + " ] ---");
//                Log.i("STAT_", "확진환자: " + decideCntArr[i]);
//                Log.i("STAT_", "검사중: " + examCntArr[i]);
//                Log.i("STAT_", "격리해제: " + clearCntArr[i]);
//                Log.i("STAT_", "사망자: " + deathCntArr[i]);
//                Log.i("STAT_", "-----------------------------------");
//            }
//            Log.i("STAT_", "확진환자 증가수: " + increaseDecideCnt);
//            Log.i("STAT_", "검사중 증가수: " + increaseExamCnt);
//            Log.i("STAT_", "격리해제 증가수: " + increaseClearCnt);
//            Log.i("STAT_", "사망자 증가수: " + increaseDeathCnt);

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