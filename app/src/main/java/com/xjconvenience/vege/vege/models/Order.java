package com.xjconvenience.vege.vege.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class Order {
    private int Id;
    private String OpenId;
    private Date CreateTime;
    private Date CancelTime;
    private Date FinishTime;
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
    private String Latitude;
    private String Longitude;
    private String RefundNote;
    private String CancelReason;
    private double TotalCost;

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

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public Date getCancelTime() {
        return CancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        CancelTime = cancelTime;
    }

    public Date getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(Date finishTime) {
        FinishTime = finishTime;
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

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
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

    public double getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(double totalCost) {
        TotalCost = totalCost;
    }
}
