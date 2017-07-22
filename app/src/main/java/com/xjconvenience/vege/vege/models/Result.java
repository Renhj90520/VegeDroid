package com.xjconvenience.vege.vege.models;

/**
 * Created by Ren Haojie on 2017/7/19.
 */

public class Result<T> {
    private String message;
    private int state;
    private T body;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
