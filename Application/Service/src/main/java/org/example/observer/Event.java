package org.example.observer;

import org.example.Bug;
import org.example.EmployeeDTO;

import java.io.Serializable;

public class Event implements Serializable {
    private Iterable<EmployeeDTO> data;
    private Iterable<Bug> bugs;
    private EventType type;

    public Iterable<EmployeeDTO> getData() {
        return data;
    }

    public void setData(Iterable<EmployeeDTO> data) {
        this.data = data;
    }

    public Iterable<Bug> getBugs() {
        return bugs;
    }

    public void setBugs(Iterable<Bug> bugs) {
        this.bugs = bugs;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }
}
