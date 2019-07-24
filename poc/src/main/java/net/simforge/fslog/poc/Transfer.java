package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transfer implements LogBookEntry {
    private LocalDate date;
    private String departure;
    private String destination;
    private LocalTime timeOut;
    private LocalTime timeIn;

    public Transfer() {
    }

    private Transfer(Transfer src) {
        this.date = src.date;
        this.departure = src.departure;
        this.destination = src.destination;
        this.timeOut = src.timeOut;
        this.timeIn = src.timeIn;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public Transfer setDate(LocalDate date) {
        Transfer copy = new Transfer(this);
        copy.date = date;
        return copy;
    }

    public String getDeparture() {
        return departure;
    }

    public Transfer setDeparture(String departure) {
        Transfer copy = new Transfer(this);
        copy.departure = departure;
        return copy;
    }

    public String getDestination() {
        return destination;
    }

    public Transfer setDestination(String destination) {
        Transfer copy = new Transfer(this);
        copy.destination = destination;
        return copy;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public Transfer setTimeOut(LocalTime timeOut) {
        Transfer copy = new Transfer(this);
        copy.timeOut = timeOut;
        return copy;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public Transfer setTimeIn(LocalTime timeIn) {
        Transfer copy = new Transfer(this);
        copy.timeIn = timeIn;
        return copy;
    }
}
