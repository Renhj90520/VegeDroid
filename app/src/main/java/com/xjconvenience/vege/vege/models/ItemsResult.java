package com.xjconvenience.vege.vege.models;

import java.util.ArrayList;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class ItemsResult<T> {
    private int count;
    private ArrayList<T> items;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }
}
