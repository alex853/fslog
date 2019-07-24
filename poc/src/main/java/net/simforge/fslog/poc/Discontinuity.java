package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalTime;

public class Discontinuity implements LogBookEntry {
    private LocalDate date;
    private LocalTime time;

    public Discontinuity() {
    }

    private Discontinuity(Discontinuity src) {
        this.date = src.date;
        this.time = src.time;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public Discontinuity setDate(LocalDate date) {
        Discontinuity copy = new Discontinuity(this);
        copy.date = date;
        return copy;
    }

    public LocalTime getTime() {
        return time;
    }

    public Discontinuity setTime(LocalTime time) {
        Discontinuity copy = new Discontinuity(this);
        copy.time = time;
        return copy;
    }
}
