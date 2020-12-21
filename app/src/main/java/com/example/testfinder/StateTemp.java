package com.example.testfinder;

class StateTemp {
    double avgMax, avgMin, max, min;
    String state;
    public StateTemp(String state, double avgMax, double avgMin, double max, double min) {
        this.state = state;
        this.avgMax = avgMax;
        this.avgMin = avgMin;
        this.max = max;
        this.min = min;
    }

    public String getState() {
        return state;
    }

    public double getAvgMax() {
        return avgMax;
    }

    public double getAvgMin() {
        return avgMin;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }
}
