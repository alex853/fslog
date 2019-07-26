package net.simforge.fslog.poc;

import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transfer implements LogBookEntry {
    private LocalDate date;
    private String departure;
    private String destination;
    private LocalTime timeOut;
    private LocalTime timeIn;
    private Node restOfXml;

    private Transfer() {
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

    public LocalTime getTimeIn() {
        return timeIn;
    }

    public Node getRestOfXml() {
        return restOfXml;
    }

    public static class Builder {
        private Transfer transfer = new Transfer();

        public Builder() {
        }

        public Builder(Transfer transfer) {
            this.transfer = copy(transfer);
        }

        public Builder setDate(LocalDate date) {
            this.transfer.date = date;
            return this;
        }

        public Builder setDeparture(String departure) {
            this.transfer.departure = departure;
            return this;
        }

        public Builder setDestination(String destination) {
            this.transfer.destination = destination;
            return this;
        }

        public Builder setTimeOut(LocalTime timeOut) {
            this.transfer.timeOut = timeOut;
            return this;
        }

        public Builder setTimeIn(LocalTime timeIn) {
            this.transfer.timeIn = timeIn;
            return this;
        }

        public Builder setRestOfXml(Node node) {
            this.transfer.restOfXml = node.cloneNode(true);
            return this;
        }

        public Transfer build() {
            return copy(transfer);
        }

        private Transfer copy(Transfer source) {
            Transfer copy = new Transfer();
            copy.date = source.date;
            copy.departure = source.departure;
            copy.destination = source.destination;
            copy.timeOut = source.timeOut;
            copy.timeIn = source.timeIn;
            copy.restOfXml = source.restOfXml.cloneNode(true);
            return copy;
        }
    }
}
