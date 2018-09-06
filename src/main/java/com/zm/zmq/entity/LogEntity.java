package com.zm.zmq.entity;

import java.util.Date;
import java.util.List;

public class LogEntity {

    private String date;
    private List<Object> data;
    private int size;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
