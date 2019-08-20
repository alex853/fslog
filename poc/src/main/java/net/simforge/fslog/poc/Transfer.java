package net.simforge.fslog.poc;

import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transfer implements LogBookEntry, Movement {

    public enum Method {
        ROADS("roads"),
        FLIGHTS("flights");

        private String code;

        Method(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static Method byCode(String code) {
            for (Method method : values()) {
                if (method.code.equals(code)) {
                    return method;
                }
            }
            return null;
        }
    }

    public enum Status {
        IN_PROGRESS("in-progress"),
        DONE("done");

        private String code;

        Status(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static Status byCode(String code) {
            for (Status status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

    private LocalDate date;
    private String departure;
    private String destination;
    private LocalTime timeOut;
    private LocalTime timeIn;
    private Method method;
    private Status status;
    private String comment;
    private Node restOfXml;

    private Transfer() {
    }

    @Override
    public LocalDate getDate() {
        return date;
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

    @Override
    public LocalTime getTimeIn() {
        return timeIn;
    }

    public Method getMethod() {
        return method;
    }

    public Status getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
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

        public Builder setMethod(Method method) {
            this.transfer.method = method;
            return this;
        }

        public Builder setStatus(Status status) {
            this.transfer.status = status;
            return this;
        }

        public Builder setComment(String comment) {
            this.transfer.comment = comment;
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
            copy.method = source.method;
            copy.status = source.status;
            copy.comment = source.comment;
            copy.restOfXml = source.restOfXml != null ? source.restOfXml.cloneNode(true) : null;
            return copy;
        }
    }
}
