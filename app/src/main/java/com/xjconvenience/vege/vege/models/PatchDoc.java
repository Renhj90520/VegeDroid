package com.xjconvenience.vege.vege.models;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class PatchDoc {
    private String op = "replace";
    private String path;
    private Object value;

    public void setPath(String path) {
        this.path = path;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
