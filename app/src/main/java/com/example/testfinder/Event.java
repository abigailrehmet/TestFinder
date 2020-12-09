package com.example.testfinder;

class Event {
    private int event_id, episode_id;
    private String event_type, date, county, state;
    private double lat, lng;

    public Event(String county, String state, String date, int event_id, int episode_id, String event_type, double lat, double lng) {
        this.event_id = event_id;
        this.episode_id = episode_id;
        this.event_type = event_type;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.county = county;
        this.state = state;
    }

    public int getEvent_id() {
        return event_id;
    }

    public int getEpisode_id() {
        return episode_id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public String getDate() {
        return date;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getCounty() {return county;}

    public String getState() {return state;}
}
