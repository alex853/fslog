package net.simforge.fslog.poc;

import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalTime;

public class FlightReport implements LogBookEntry, Movement {
    private LocalDate date;
    private String callsign;
    private String flightNumber;
    private String aircraftType;
    private String aircraftRegistration;
    private String departure;
    private String destination;
    private LocalTime timeOut;
    private LocalTime timeOff;
    private LocalTime timeOn;
    private LocalTime timeIn;
//    private Integer distance;
    private String comment;
//    private String remarks;
    private Node restOfXml;

    private FlightReport() {
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public String getCallsign() {
        return callsign;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public String getAircraftRegistration() {
        return aircraftRegistration;
    }

    @Override
    public String getDeparture() {
        return departure;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public LocalTime getTimeOut() {
        return timeOut;
    }

    public LocalTime getTimeOff() {
        return timeOff;
    }

    public LocalTime getTimeOn() {
        return timeOn;
    }

    @Override
    public LocalTime getTimeIn() {
        return timeIn;
    }

    public String getComment() {
        return comment;
    }

    public Node getRestOfXml() {
        return restOfXml;
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

        public Builder setCallsign(String callsign) {
            this.flightReport.callsign = callsign;
            return this;
        }

        public Builder setFlightNumber(String flightNumber) {
            this.flightReport.flightNumber = flightNumber;
            return this;
        }

        public Builder setAircraftType(String aircraftType) {
            this.flightReport.aircraftType = aircraftType;
            return this;
        }

        public Builder setAircraftRegistration(String aircraftRegistration) {
            this.flightReport.aircraftRegistration = aircraftRegistration;
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

        public Builder setComment(String comment) {
            this.flightReport.comment = comment;
            return this;
        }

        public Builder setRestOfXml(Node node) {
            this.flightReport.restOfXml = node.cloneNode(true);
            return this;
        }

        public FlightReport build() {
            return copy(flightReport);
        }

        private FlightReport copy(FlightReport source) {
            FlightReport copy = new FlightReport();
            copy.date = source.date;
            copy.callsign = source.callsign;
            copy.flightNumber = source.flightNumber;
            copy.aircraftType = source.aircraftType;
            copy.aircraftRegistration = source.aircraftRegistration;
            copy.departure = source.departure;
            copy.destination = source.destination;
            copy.timeOut = source.timeOut;
            copy.timeOff = source.timeOff;
            copy.timeOn = source.timeOn;
            copy.timeIn = source.timeIn;
            copy.comment = source.comment;
            copy.restOfXml = source.restOfXml != null ? source.restOfXml.cloneNode(true) : null;
            return copy;
        }
    }
}
