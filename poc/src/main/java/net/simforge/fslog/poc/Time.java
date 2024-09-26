package net.simforge.fslog.poc;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Time {
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
