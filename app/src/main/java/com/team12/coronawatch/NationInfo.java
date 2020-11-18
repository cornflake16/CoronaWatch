package com.team12.coronawatch;

public class NationInfo {
    private String areaNm;
    private String areaNmEn;
    private String nationNm;
    private String nationNmEn;
    private long natDefCnt;
    private long natDeathCnt;
    private double natDeathRate;

    public String getAreaNm() {
        return areaNm;
    }

    public void setAreaNm(String areaNm) {
        this.areaNm = areaNm;
    }

    public String getAreaNmEn() {
        return areaNmEn;
    }

    public void setAreaNmEn(String areaNmEn) {
        this.areaNmEn = areaNmEn;
    }

    public String getNationNm() {
        return nationNm;
    }

    public void setNationNm(String nationNm) {
        this.nationNm = nationNm;
    }

    public String getNationNmEn() {
        return nationNmEn;
    }

    public void setNationNmEn(String nationNmEn) {
        this.nationNmEn = nationNmEn;
    }

    public long getNatDefCnt() {
        return natDefCnt;
    }

    public void setNatDefCnt(String natDefCnt) {
        this.natDefCnt = Long.parseLong(natDefCnt);
    }

    public long getNatDeathCnt() {
        return natDeathCnt;
    }

    public void setNatDeathCnt(String natDeathCnt) {
        this.natDeathCnt = Long.parseLong(natDeathCnt);
    }

    public double getNatDeathRate() {
        return natDeathRate;
    }

    public void setNatDeathRate(String natDeathRate) {
        this.natDeathRate = Double.parseDouble(natDeathRate);
    }
}
