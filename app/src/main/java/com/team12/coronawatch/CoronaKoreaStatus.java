package com.team12.coronawatch;

import android.util.Log;

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


class DailyInfo {
    private long decideCnt;     //확진자 수
    private long examCnt;       //검사진행 수
    private long clearCnt;      //격리해제 수
    private long deathCnt;      //사망자 수
    private String createDt;    //등록일시

    public long getDecideCnt() {
        return decideCnt;
    }

    public void setDecideCnt(long decideCnt) {
        this.decideCnt = decideCnt;
    }

    public long getExamCnt() {
        return examCnt;
    }

    public void setExamCnt(long examCnt) {
        this.examCnt = examCnt;
    }

    public long getClearCnt() {
        return clearCnt;
    }

    public void setClearCnt(long clearCnt) {
        this.clearCnt = clearCnt;
    }

    public long getDeathCnt() {
        return deathCnt;
    }

    public void setDeathCnt(long deathCnt) {
        this.deathCnt = deathCnt;
    }

    public String getCreateDt() {
        return createDt;
    }

    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }
}

class CoronaKoreaStatus {
    final static double WEEKDAY_NUMBER = 7.0;
    //URL 관련 변수
    private String urlBuilder;
    private String UTF;
    private String SERVICE_URL;
    private String SERVICE_KEY;

    //포맷 변수
    private DecimalFormat formatter;
    public SimpleDateFormat dateFormatForComp, dateFormat_year, dateFormat_month, dateFormat_day, dateFormat_hour;

    //날짜 및 시간관련 변수
    public Date time;
    public String sStateDt = "-";
    public String sYear, sMonth, sDay, sHour, sToday, sYesterday, sTwoDayAgo;
    private String stdYestFromServer, stdTodayFromServer;
    private int[] days;
    public int nYear, nMonth, nDay, nHour;

    //정보 변수(다른 곳에 활용할때는 이 변수들을 활용하면 됨)
    /*
        todayStateDate: 등록 기준일시
        createDtList: 일자별 등록일자가 들어 있는 리스트 (ex. 2020-11-19)
        createDtTimeList: 일자별 등록일시가 들어 있는 리스트 (ex. 2020-11-19 09)
        $(x)CntList: 일자별로 x 항목의 값(수치)들을 저장해놓은 리스트(인덱스가 낮을수록 최신 일자의 데이터)
        $(x)CntToTal: 일주일
        $(x)IncCnt: 금일 기준 전일 대비 x 항목의 증가 값
        $(x)IncCntList: 일자별로 전일대비 증가 값들을 저장해놓은 리스트_총 (day-1)개의 값이 저장되야함 -> ex)7일기준 6개
        $(x)TotIncCntForAWeek: 한 주 동안의 x 항목의 증가 값들의 총합
        $(x)AvgIncCntForAWeek: 한 주 동안의 x 항목의 증가 값들의 평균 값
    */
    public String todayStateDate;
    public ArrayList<String> createDtList, createDtTimeList;
    public ArrayList<Long> decideCntList, examCntList, clearCntList, deathCntList;
    //     long
    public long decideIncCnt, examIncCnt, clearIncCnt, deathIncCnt;
    public ArrayList<Long> decideIncCntList, examIncCntList, clearIncCntList, deathIncCntList;
    public long decideTotIncCntForAWeek, examTotIncCntForAWeek, clearTotIncCntForAWeek, deathTotIncCntForAWeek;
    public double decideAvgIncCntForAWeek, examAvgIncCntForAWeek, clearAvgIncCntForAWeek, deathAvgIncCntForAWeek;
    public String newFmt_todayDecideCnt, newFmt_decideIncCnt, newFmt_decideTotIncCntForAWeek, newFmt_decideAvgIncCntForAWeek;
    public String newFmt_todayExamCnt, newFmt_examIncCnt, newFmt_examTotIncCntForAWeek, newFmt_examAvgIncCntForAWeek;
    public String newFmt_todayClearCnt, newFmt_clearIncCnt, newFmt_clearTotIncCntForAWeek, newFmt_clearAvgIncCntForAWeek;
    public String newFmt_todayDeathCnt, newFmt_deathIncCnt, newFmt_deathTotIncCntForAWeek, newFmt_deathAvgIncCntForAWeek;


    //파싱 관련 변수
    Element body, items, item;
    Node decideCnt, examCnt, clearCnt, deathCnt, createDt, stdDt;
    private ArrayList<DailyInfo> dailyInfoList;

    void init() {
        UTF = "UTF-8";
        SERVICE_URL = "http://openapi.data.go.kr/openapi/service/rest/Covid19/" +
                "getCovid19InfStateJson";
        SERVICE_KEY = "=1S8z1o0Mg6QxYGxG5z3Efb87G2YqofNJcnFv4L47ru7gPncj2MRdl" +
                "Vu%2BK6uitzbqYnf6BSl19%2FXCXMuqtrXx8w%3D%3D";  //보건복지부_코로나19_국내_발생현황_일반인증키(UTF-8)

        dateFormatForComp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat_year = new SimpleDateFormat("yyyy", Locale.getDefault());
        dateFormat_month = new SimpleDateFormat("MM", Locale.getDefault());
        dateFormat_day = new SimpleDateFormat("dd", Locale.getDefault());
        dateFormat_hour = new SimpleDateFormat("HH", Locale.getDefault());
        time = new Date();

        formatter = new DecimalFormat("###,###.#");

        days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        sYear = dateFormat_year.format(time);
        sMonth = dateFormat_month.format(time);
        sDay = dateFormat_day.format(time);
        sHour = dateFormat_hour.format(time);

        nYear = Integer.parseInt(sYear);
        nMonth = Integer.parseInt(sMonth);
        nDay = Integer.parseInt(sDay);
        nHour = Integer.parseInt(sHour);

        sToday = dayAgo(0);
        sToday = sToday.substring(0, 4) + '-'
                + sToday.substring(4, 6) + '-' + sToday.substring(6, 8);
        sYesterday = dayAgo(1);
        sYesterday = sYesterday.substring(0, 4) + '-'
                + sYesterday.substring(4, 6) + '-' + sYesterday.substring(6, 8);
        sTwoDayAgo = dayAgo(2);
        sTwoDayAgo = sTwoDayAgo.substring(0, 4) + '-'
                + sTwoDayAgo.substring(4, 6) + '-' + sTwoDayAgo.substring(6, 8);

        createDtList = new ArrayList<>();
        createDtTimeList = new ArrayList<>();
        decideCntList = new ArrayList<>();
        examCntList = new ArrayList<>();
        clearCntList = new ArrayList<>();
        deathCntList = new ArrayList<>();
        dailyInfoList = new ArrayList<>();
        decideIncCntList = new ArrayList<>();
        examIncCntList = new ArrayList<>();
        clearIncCntList = new ArrayList<>();
        deathIncCntList = new ArrayList<>();

        decideTotIncCntForAWeek = examTotIncCntForAWeek = clearTotIncCntForAWeek = deathTotIncCntForAWeek = 0;
    }

    public String dayAgo(int subNum) {
        return calDate(nYear, nMonth, nDay, subNum);
    }

    public String calDate(int year, int month, int day, int subNumber) {   //n일 전의 date 반환하는 함수
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

    protected void loadXML() {
        int nWeekAgo = 7,
                nToday = 0;
        for (int i = 0; i < 2; i++) {
            try {
                urlBuilder = SERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + SERVICE_KEY + /*Service Key*/
                        "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                        "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                        "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(nWeekAgo), UTF) + /*검색할 생성일 범위의 시작*/
                        "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(nToday), UTF);/*URL*//*검색할 생성일 범위의 종료*/
                if (i == 1) {
                    Log.i("CoronaKRClass: ", "INFO_URL - URL:" + urlBuilder);
                }
            } catch (Exception e) {
                Log.i("CoronaKRClass: ", "Exception: " + e.getMessage());
            }

            Document doc = null;
            URL url = null;
            try {
                url = new URL(urlBuilder);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                Log.i("CoronaKRClass: ", "CoronaNationalStatus()" + e.getMessage());
            }
            Log.i("CORONA_KR: ", "" + url);
            Log.i("CORONA_KR: ", "" + doc);
            assert doc != null;
            body = (Element) doc.getElementsByTagName("body").item(0);
            items = (Element) body.getElementsByTagName("items").item(0);
            item = (Element) items.getElementsByTagName("item").item(0);

            Node tmpCreateDt = item.getElementsByTagName("createDt").item(0);
            String sTmpCreateDt = tmpCreateDt.getChildNodes().item(0).getNodeValue();
            if (i == 0) {
                if (!sTmpCreateDt.substring(0, 10).equals(sToday)) {
                    Log.i("CoronaKRClass: ", sTmpCreateDt.substring(0, 10) + "-" + sToday);
                    nWeekAgo = 8;
                    nToday = 1;
                    stdYestFromServer = sTwoDayAgo;
                    stdTodayFromServer = sYesterday;
                } else {
                    stdYestFromServer = sYesterday;
                    stdTodayFromServer = sToday;
                    break;
                }
            }
        }
    }

    public void parseXML() {
        loadXML();
        Log.i("CoronaKRClass: ", "서버기준 오늘: " + stdTodayFromServer);
        Log.i("CoronaKRClass: ", "서버기준 어제: " + stdYestFromServer);
        int i = 0;
        while (true) {
            DailyInfo dailyInfo = new DailyInfo();
            item = (Element) items.getElementsByTagName("item").item(i);

            if (item == null) {
                break;
            }

            if (i++ == 0) {
                stdDt = item.getElementsByTagName("stateDt").item(0);
                sStateDt = stdDt.getChildNodes().item(0).getNodeValue();
            }
            //decideCnt, examCnt, clearCnt, deathCnt, createDt;
            decideCnt = item.getElementsByTagName("decideCnt").item(0);    //확진자 수(일별)
            examCnt = item.getElementsByTagName("examCnt").item(0);    //검사진행 수(일별)
            clearCnt = item.getElementsByTagName("clearCnt").item(0);    //격리해제 수(일별)
            deathCnt = item.getElementsByTagName("deathCnt").item(0);    //사망자 수(일별)
            createDt = item.getElementsByTagName("createDt").item(0);    //등록일시

            dailyInfo.setDecideCnt(Long.parseLong(decideCnt.getChildNodes().item(0).getNodeValue()));
            dailyInfo.setExamCnt(Long.parseLong(examCnt.getChildNodes().item(0).getNodeValue()));
            dailyInfo.setClearCnt(Long.parseLong(clearCnt.getChildNodes().item(0).getNodeValue()));
            dailyInfo.setDeathCnt(Long.parseLong(deathCnt.getChildNodes().item(0).getNodeValue()));
            dailyInfo.setCreateDt(createDt.getChildNodes().item(0).getNodeValue());

            dailyInfoList.add(dailyInfo);
        }

        for (DailyInfo dailyInfo : dailyInfoList) {

            Log.i("CoronaKRClass: ", "----------------------------------------");
            Log.i("CoronaKRClass: ", "등록일자: " + dailyInfo.getCreateDt().substring(0, 19) + '\n');
            Log.i("CoronaKRClass: ", "확진자 수(누적): " + formatter.format(dailyInfo.getDecideCnt()) + "명");
            Log.i("CoronaKRClass: ", "검사진행 수(누적): " + formatter.format(dailyInfo.getExamCnt()) + "명");
            Log.i("CoronaKRClass: ", "격리해제 수(누적): " + formatter.format(dailyInfo.getClearCnt()) + "명");
            Log.i("CoronaKRClass: ", "사망자 수(누적): " + formatter.format(dailyInfo.getDeathCnt()) + "명");

            createDtList.add(dailyInfo.getCreateDt().substring(0, 19));
            createDtTimeList.add(dailyInfo.getCreateDt().substring(0, 13));
            //일자별로 각 항목의 값(수치)들을 저장
            decideCntList.add(dailyInfo.getDecideCnt());
            examCntList.add(dailyInfo.getExamCnt());
            clearCntList.add(dailyInfo.getClearCnt());
            deathCntList.add(dailyInfo.getDeathCnt());

            todayStateDate = createDtTimeList.get(0) + "시 기준(국내)";
        }

        //한 주 동안의 증가 값들을 구하기 위한 반복문
        for (int j = 0; j < WEEKDAY_NUMBER - 1; j++) {
            decideIncCntList.add(decideCntList.get(j) - decideCntList.get(j + 1));
            examIncCntList.add(examCntList.get(j) - examCntList.get(j + 1));
            clearIncCntList.add(clearCntList.get(j) - clearCntList.get(j + 1));
            deathIncCntList.add(deathCntList.get(j) - deathCntList.get(j + 1));
        }

        //금일 기준 전일 대비 증가 값을 구하기 위한 연산
        decideIncCnt = decideIncCntList.get(0);
        examIncCnt = examIncCntList.get(0);
        clearIncCnt = clearIncCntList.get(0);
        deathIncCnt = deathIncCntList.get(0);

        //한 주 동안의 증가 값들의 총합을 구하기 위한 반복문
        for (int j = 0; j < WEEKDAY_NUMBER - 1; j++) {
            decideTotIncCntForAWeek += decideIncCntList.get(j);
            examTotIncCntForAWeek += examIncCntList.get(j);
            clearTotIncCntForAWeek += clearIncCntList.get(j);
            deathTotIncCntForAWeek += deathIncCntList.get(j);
        }

        //한 주 동안의 증가 값들의 평균 값을 구하기 위한 연산
        decideAvgIncCntForAWeek = decideTotIncCntForAWeek / WEEKDAY_NUMBER - 1.0;
        examAvgIncCntForAWeek = examTotIncCntForAWeek / WEEKDAY_NUMBER - 1.0;
        clearAvgIncCntForAWeek = clearTotIncCntForAWeek / WEEKDAY_NUMBER - 1.0;
        deathAvgIncCntForAWeek = deathTotIncCntForAWeek / WEEKDAY_NUMBER - 1.0;

        //앱에서의 출력을 위한 포맷 값 대입(확진자)
        newFmt_todayDecideCnt = formatter.format(decideCntList.get(0));
        newFmt_decideIncCnt = formatter.format(decideIncCnt);
        newFmt_decideTotIncCntForAWeek = formatter.format(decideTotIncCntForAWeek);
        newFmt_decideAvgIncCntForAWeek = formatter.format(decideAvgIncCntForAWeek);
        //앱에서의 출력을 위한 포맷 값 대입(검사진행)
        newFmt_todayExamCnt = formatter.format(examCntList.get(0));
        newFmt_examIncCnt = formatter.format(examIncCnt);
        newFmt_examTotIncCntForAWeek = formatter.format(examTotIncCntForAWeek);
        newFmt_examAvgIncCntForAWeek = formatter.format(examAvgIncCntForAWeek);
        //앱에서의 출력을 위한 포맷 값 대입(격리해제)
        newFmt_todayClearCnt = formatter.format(clearCntList.get(0));
        newFmt_clearIncCnt = formatter.format(clearIncCnt);
        newFmt_clearTotIncCntForAWeek = formatter.format(clearTotIncCntForAWeek);
        newFmt_clearAvgIncCntForAWeek = formatter.format(clearAvgIncCntForAWeek);
        //앱에서의 출력을 위한 포맷 값 대입(사망자)
        newFmt_todayDeathCnt = formatter.format(deathCntList.get(0));
        newFmt_deathIncCnt = formatter.format(deathIncCnt);
        newFmt_deathTotIncCntForAWeek = formatter.format(deathTotIncCntForAWeek);
        newFmt_deathAvgIncCntForAWeek = formatter.format(deathAvgIncCntForAWeek);
    }

    public void printInfo() {
        Log.i("CoronaKRClass: ", "----------------------------------------");
        Log.i("CoronaKRClass: ", "[정보 정리]");
        Log.i("CoronaKRClass: ", "\t(확진자)");
        Log.i("CoronaKRClass: ", "\t\t - 확진자 수(누적): " + formatter.format(decideCntList.get(0)) + "명");
        Log.i("CoronaKRClass: ", "\t\t - 확진자 증가 수(전일 대비): " + formatter.format(decideIncCnt) + "명");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 총합 "
                + formatter.format(decideTotIncCntForAWeek) + "명의 확진자 추가");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 평균 "
                + formatter.format(decideAvgIncCntForAWeek) + "명의 확진자 추가");
        Log.i("CoronaKRClass: ", "---");

        Log.i("CoronaKRClass: ", "\t(검사진행)");
        Log.i("CoronaKRClass: ", "\t\t - 검사진행 수(누적): " + formatter.format(examCntList.get(0)) + "명");
        Log.i("CoronaKRClass: ", "\t\t - 검사진행 증가 수(전일 대비): " + formatter.format(examIncCnt) + "명");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 총합 "
                + formatter.format(examTotIncCntForAWeek) + "명의 검사자 추가");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 평균 "
                + formatter.format(examAvgIncCntForAWeek) + "명의 검사자 추가");
        Log.i("CoronaKRClass: ", "---");

        Log.i("CoronaKRClass: ", "\t(격리해제)");
        Log.i("CoronaKRClass: ", "\t\t - 격리해제 수(누적): " + formatter.format(clearCntList.get(0)) + "명");
        Log.i("CoronaKRClass: ", "\t\t - 격리해제 증가 수(전일 대비): " + formatter.format(clearIncCnt) + "명");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 총합 "
                + formatter.format(clearTotIncCntForAWeek) + "명의 격리해제 추가");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 평균 "
                + formatter.format(clearAvgIncCntForAWeek) + "명의 격리해제 추가");
        Log.i("CoronaKRClass: ", "---");

        Log.i("CoronaKRClass: ", "\t(사망자)");
        Log.i("CoronaKRClass: ", "\t\t - 사망자 수(누적): " + formatter.format(deathCntList.get(0)) + "명");
        Log.i("CoronaKRClass: ", "\t\t - 사망자 증가 수(전일 대비): " + formatter.format(deathIncCnt) + "명");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 총합 "
                + formatter.format(deathTotIncCntForAWeek) + "명의 사망자 추가");
        Log.i("CoronaKRClass: ", "\t\t - " + (int) WEEKDAY_NUMBER + "일 평균 "
                + formatter.format(deathAvgIncCntForAWeek) + "명의 사망자 추가");
        Log.i("CoronaKRClass: ", "---");
        Log.i("CoronaKRClass: ", "" + todayStateDate);
    }
}
