package com.xjconvenience.vege.vege.models;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderItem {
    private double Count;
    private int Id;
    private String Name;
    private String UnitName;
    private double Price;
    private String Description;
    private int State;
    private double Cost;

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
}
