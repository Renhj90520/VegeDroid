package com.xjconvenience.vege.vege.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderItem implements Parcelable {
    private double Count;
    private int Id;
    private String Name;
    private String UnitName;
    private double Price;
    private String Description;
    private int State;
    private double Cost;

    protected OrderItem(Parcel in) {
        Count = in.readDouble();
        Id = in.readInt();
        Name = in.readString();
        UnitName = in.readString();
        Price = in.readDouble();
        Description = in.readString();
        State = in.readInt();
        Cost = in.readDouble();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    public double getCount() {
        return Count;
    }

    public void setCount(double count) {
        Count = count;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public double getCost() {
        return Cost;
    }

    public void setCost(double cost) {
        Cost = cost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(Count);
        parcel.writeInt(Id);
        parcel.writeString(Name);
        parcel.writeString(UnitName);
        parcel.writeDouble(Price);
        parcel.writeString(Description);
        parcel.writeInt(State);
        parcel.writeDouble(Cost);
    }
}
