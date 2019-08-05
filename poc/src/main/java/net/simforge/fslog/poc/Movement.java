package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Movement {
    LocalDate getDate();

    String getDeparture();

    String getDestination();

    LocalTime getTimeOut();

    LocalTime getTimeIn();
}
