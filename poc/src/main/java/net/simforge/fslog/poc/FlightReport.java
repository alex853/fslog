package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalTime;

public class FlightReport implements LogBookEntry {
    private LocalDate date;
//    private String callsign;
//    private String flightNumber;
//    private String aircraftType;
//    private String aircraftRegistration;
    private String departure;
    private String destination;
    private LocalTime timeOut;
    private LocalTime timeOff;
    private LocalTime timeOn;
    private LocalTime timeIn;
//    private Integer distance;
//    private String comment;
//    private String remarks;

    public FlightReport() {
    }

    private FlightReport(FlightReport src) {
        this.date = src.date;
        this.departure = src.departure;
        this.destination = src.destination;
        this.timeOut = src.timeOut;
        this.timeOff = src.timeOff;
        this.timeOn = src.timeOn;
        this.timeIn = src.timeIn;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public FlightReport setDate(LocalDate date) {
        FlightReport copy = new FlightReport(this);
        copy.date = date;
        return copy;
    }

    public String getDeparture() {
        return departure;
    }

    public FlightReport setDeparture(String departure) {
        FlightReport copy = new FlightReport(this);
        copy.departure = departure;
        return copy;
    }

    public String getDestination() {
        return destination;
    }

    public FlightReport setDestination(String destination) {
        FlightReport copy = new FlightReport(this);
        copy.destination = destination;
        return copy;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public FlightReport setTimeOut(LocalTime timeOut) {
        FlightReport copy = new FlightReport(this);
        copy.timeOut = timeOut;
        return copy;
    }

    public LocalTime getTimeOff() {
        return timeOut;
    }

    public FlightReport setTimeOff(LocalTime timeOff) {
        FlightReport copy = new FlightReport(this);
        copy.timeOff = timeOff;
        return copy;
    }

    public LocalTime getTimeOn() {
        return timeOut;
    }

    public FlightReport setTimeOn(LocalTime timeOn) {
        FlightReport copy = new FlightReport(this);
        copy.timeOn = timeOn;
        return copy;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public FlightReport setTimeIn(LocalTime timeIn) {
        FlightReport copy = new FlightReport(this);
        copy.timeIn = timeIn;
        return copy;
    }
}
