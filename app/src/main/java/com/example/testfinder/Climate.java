package com.example.testfinder;

import java.util.Date;

class Climate {
    private int id, max_temp, min_temp;
    String date, state;
    public Climate(int id, int max_temp, int min_temp, String date, String state) {
        this.id = id;
        this. max_temp = max_temp;
        this.min_temp = min_temp;
        this.date = date;
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getMax_temp() {
        return max_temp;
    }

    public int getMin_temp() {
        return min_temp;
    }

    public String getState() {
        return state;
    }
}
