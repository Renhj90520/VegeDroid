package com.xjconvenience.vege.vege.models;

/**
 * Created by Ren Haojie on 2017/7/25.
 */

public class RefundWrapper {
    private int TotalCost;
    private int RefundCost;
    private String RefundNote;

    public int getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(int totalCost) {
        TotalCost = totalCost;
    }

    public int getRefundCost() {
        return RefundCost;
    }

    public void setRefundCost(int refundCost) {
        RefundCost = refundCost;
    }

    public String getRefundNote() {
        return RefundNote;
    }

    public void setRefundNote(String refundNote) {
        RefundNote = refundNote;
    }
}
