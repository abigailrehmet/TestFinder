package com.example.testfinder;

class Event {
    private int event_id, episode_id, mag, year, direct_deaths, indirect_deaths, dp_cost, dc_cost, indirect_injuries, direct_injuries;
    private String event_type, date, county, state, source;
    private double lat, lng;

    public Event(String county, String state, String date, int event_id, int episode_id, String event_type, double lat, double lng,
         int mag,
         int year,
        int direct_deaths,
        int indirect_deaths,
        int dp_cost,
        int dc_cost,
        int indirect_injuries,
        int direct_injuries,
        String source) {

        this.event_id = event_id;
        this.episode_id = episode_id;
        this.event_type = event_type;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
        this.county = county;
        this.state = state;
        this.mag = mag;
        this.year = year;
        this.direct_deaths = direct_deaths;
        this.indirect_deaths = indirect_deaths;
        this.dp_cost = dp_cost;
        this.dc_cost = dc_cost;
        this.indirect_injuries = indirect_injuries;
        this.direct_injuries = direct_injuries;
        this.source = source;
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

    public int getDc_cost() {
        return dc_cost;
    }

    public int getDirect_deaths() {
        return direct_deaths;
    }

    public int getDirect_injuries() {
        return direct_injuries;
    }

    public int getDp_cost() {
        return dp_cost;
    }

    public int getIndirect_deaths() {
        return indirect_deaths;
    }

    public int getIndirect_injuries() {
        return indirect_injuries;
    }

    public int getMag() {
        return mag;
    }

    public int getYear() {
        return year;
    }

    public String getSource() {
        return source;
    }
}
