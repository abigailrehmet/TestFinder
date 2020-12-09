package com.example.testfinder;

class Event {
    private int event_id, episode_id;
    private String event_type, date;

    public Event(String date, int event_id, int episode_id, String event_type) {
        this.event_id = event_id;
        this.episode_id = episode_id;
        this.event_type = event_type;
        this.date = date;
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
}
