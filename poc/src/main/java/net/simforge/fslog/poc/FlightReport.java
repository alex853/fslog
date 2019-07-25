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

    private FlightReport() {
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public LocalTime getTimeOut() {
        return timeOut;
    }

    public LocalTime getTimeOff() {
        return timeOff;
    }

    public LocalTime getTimeOn() {
        return timeOn;
    }

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public static class Builder {
        private FlightReport flightReport = new FlightReport();

        public Builder() {
        }

        public Builder(FlightReport flightReport) {
            this.flightReport = copy(flightReport);
        }

        public Builder setDate(LocalDate date) {
            this.flightReport.date = date;
            return this;
        }

        public Builder setDeparture(String departure) {
            this.flightReport.departure = departure;
            return this;
        }

        public Builder setDestination(String destination) {
            this.flightReport.destination = destination;
            return this;
        }

        public Builder setTimeOut(LocalTime timeOut) {
            this.flightReport.timeOut = timeOut;
            return this;
        }

        public Builder setTimeOff(LocalTime timeOff) {
            this.flightReport.timeOff = timeOff;
            return this;
        }

        public Builder setTimeOn(LocalTime timeOn) {
            this.flightReport.timeOn = timeOn;
            return this;
        }

        public Builder setTimeIn(LocalTime timeIn) {
            this.flightReport.timeIn = timeIn;
            return this;
        }

        public FlightReport build() {
            return copy(flightReport);
        }

        private FlightReport copy(FlightReport source) {
            FlightReport copy = new FlightReport();
            copy.date = source.date;
            copy.departure = source.departure;
            copy.destination = source.destination;
            copy.timeOut = source.timeOut;
            copy.timeOff = source.timeOff;
            copy.timeOn = source.timeOn;
            copy.timeIn = source.timeIn;
            return copy;
        }
    }
}
