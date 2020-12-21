package com.example.testfinder;

import java.util.Date;

class BHPercentage {
    private int mind;
    private double hispanic_rate, black_rate;
    private String date, state, county;
    public BHPercentage(int mind, double hispanic_rate, double black_rate, String date, String state, String county) {
        this.mind = mind;
        this.hispanic_rate = hispanic_rate;
        this.black_rate = black_rate;
        this.date = date;
        this.state = state;
        this.county = county;
    }

    public String getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

    public double getBlack_rate() {
        return black_rate;
    }

    public double getHispanic_rate() {
        return hispanic_rate;
    }

    public int getMind() {
        return mind;
    }

    public String getCounty() {
        return county;
    }
}
