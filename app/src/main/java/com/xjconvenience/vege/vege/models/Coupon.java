package com.xjconvenience.vege.vege.models;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

public class Coupon {
    private int Id;
    private String Code;
    private String Begin;
    private String End;
    private String QR_Path;
    private String State;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getBegin() {
        return Begin;
    }

    public void setBegin(String begin) {
        Begin = begin;
    }

    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        End = end;
    }

    public String getQR_Path() {
        return QR_Path;
    }

    public void setQR_Path(String QR_Path) {
        this.QR_Path = QR_Path;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
