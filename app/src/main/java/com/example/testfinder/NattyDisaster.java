package com.example.testfinder;

class NattyDisaster {
    String event_type, county;
    public NattyDisaster(String etype, String c) {
        this.event_type = etype;
        this.county = c;
    }

    public String getEventType() {
        return event_type;
    }

    public String getCounty() {
        return county;
    }

}
