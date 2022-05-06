package com.test.logParser.entities;
import javax.persistence.Id;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name = "events")
public class Event {
    @Id
    private String id;
    private long duration;
    private EventType type;
    private long timestamp;
    private String host;
    private boolean alert;

    public Event(String id, long duration, EventType type,long timestamp, String host) {
        this.id = id;
        this.duration = duration;
        this.type = type;
        this.host = host;
        this.timestamp = timestamp;
        this.alert = false;
    }

    public Event() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}
