package net.simforge.fslog.poc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface Movement {
    LocalDate getDate();

    String getDeparture();

    String getDestination();

    LocalTime getTimeOut();

    LocalTime getTimeIn();

    default LocalDateTime getDateTimeOut() {
        return getDate().atTime(getTimeOut());
    }

    default LocalDateTime getDateTimeIn() {
        return getTimeIn().isAfter(getTimeOut()) ? getDate().atTime(getTimeIn()) : getDate().plusDays(1).atTime(getTimeIn());
    }
}
