package net.simforge.fslog.poc;

import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalTime;

public class Discontinuity implements LogBookEntry {
    private LocalDate date;
    private LocalTime time;
    private String comment;
    private Node restOfXml;

    private Discontinuity() {
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getComment() {
        return comment;
    }

    public Node getRestOfXml() {
        return restOfXml;
    }

    public static class Builder {
        private Discontinuity discontinuity = new Discontinuity();

        public Builder() {
        }

        public Builder(Discontinuity discontinuity) {
            this.discontinuity = copy(discontinuity);
        }

        public Builder setDate(LocalDate date) {
            this.discontinuity.date = date;
            return this;
        }

        public Builder setTime(LocalTime time) {
            this.discontinuity.time = time;
            return this;
        }

        public Builder setComment(String comment) {
            this.discontinuity.comment = comment;
            return this;
        }

        public Builder setRestOfXml(Node node) {
            this.discontinuity.restOfXml = node.cloneNode(true);
            return this;
        }

        public Discontinuity build() {
            return copy(discontinuity);
        }

        private Discontinuity copy(Discontinuity source) {
            Discontinuity copy = new Discontinuity();
            copy.date = source.date;
            copy.time = source.time;
            copy.comment = source.comment;
            copy.restOfXml = source.restOfXml != null ? source.restOfXml.cloneNode(true) : null;
            return copy;
        }
    }
}
