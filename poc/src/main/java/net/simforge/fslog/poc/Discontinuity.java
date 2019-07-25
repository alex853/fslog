package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalTime;

public class Discontinuity implements LogBookEntry {
    private LocalDate date;
    private LocalTime time;

    private Discontinuity() {
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
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

        public Discontinuity build() {
            return copy(discontinuity);
        }

        private Discontinuity copy(Discontinuity source) {
            Discontinuity copy = new Discontinuity();
            copy.date = source.date;
            copy.time = source.time;
            return copy;
        }
    }
}
