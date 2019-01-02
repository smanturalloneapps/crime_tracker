package com.taranga.crimetracker.model.event_bus;

public final class DataEvent {

    public final Object obj;

    public DataEvent(Object obj) {
        this.obj = obj;
    }
}
