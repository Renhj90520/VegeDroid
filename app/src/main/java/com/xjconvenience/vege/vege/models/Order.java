package com.xjconvenience.vege.vege.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class Order {
    private int Id;
    private String OpenId;
    private String CreateTime;
    private String CancelTime;


    private String FinishTime;
    private int State;
    private ArrayList<OrderItem> Products;
    private String Phone;
    private String Street;
    private String City;
    private String Province;
    private String Name;
    private String Area;
    private double DeliveryCharge;
    private String WXOrderId;
    private double Latitude;
    private double Longitude;
    private String RefundNote;
    private String CancelReason;
    private double TotalCost;
    private String IsPaid;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public ArrayList<OrderItem> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<OrderItem> products) {
        Products = products;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public double getDeliveryCharge() {
        return DeliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        DeliveryCharge = deliveryCharge;
    }

    public String getWXOrderId() {
        return WXOrderId;
    }

    public void setWXOrderId(String WXOrderId) {
        this.WXOrderId = WXOrderId;
    }

    public String getRefundNote() {
        return RefundNote;
    }

    public void setRefundNote(String refundNote) {
        RefundNote = refundNote;
    }

    public String getCancelReason() {
        return CancelReason;
    }

    public void setCancelReason(String cancelReason) {
        CancelReason = cancelReason;
    }

    public String getCreateTime() {
        return CreateTime.substring(0, 19).replace('T', ' ');
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getCancelTime() {
        return CancelTime.substring(0, 19).replace('T', ' ');
    }

    public void setCancelTime(String cancelTime) {
        CancelTime = cancelTime;
    }

    public String getFinishTime() {
        return FinishTime.substring(0, 19).replace('T', ' ');
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }

    public double getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(double totalCost) {
        TotalCost = totalCost;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getIsPaid() {
        return IsPaid;
    }

    public void setIsPaid(String isPaid) {
        IsPaid = isPaid;
    }
}
