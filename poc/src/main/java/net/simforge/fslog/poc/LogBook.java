package net.simforge.fslog.poc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogBook {

    private List<LogBookEntry> entries = new ArrayList<>();

    public List<LogBookEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void compute() {
        for (int i = 0; i < entries.size(); i++) {
            LogBookEntry entry = entries.get(i);

            LogBookEntry prev = null;
            LogBookEntry next = null;
            if (i > 0) {
                prev = entries.get(i - 1);
            }
            if (i < entries.size() - 1 - 1) {
                next = entries.get(i + 1);
            }

            LogBookEntry newEntry = null;
            if (entry instanceof Transfer) {
                newEntry = compute((Transfer) entry);
            }

            if (newEntry != null) {
                ValidationResult result;

                if (prev != null) {
                    result = validate(prev, newEntry);
                    if (result.getOverallResult() != ValidationResult.Result.OK) {
                        throw new IllegalStateException();
                    }
                }

                if (next != null) {
                    result = validate(newEntry, next);
                    if (result.getOverallResult() != ValidationResult.Result.OK) {
                        throw new IllegalStateException();
                    }
                }

                entries.set(i, newEntry);
            }
        }
    }

    public static Transfer compute(Transfer transfer) {
        if (transfer.getStatus() == Transfer.Status.DONE && transfer.getTimeIn() != null) {
            return null;
        }

        // so we need to calculate
        LocalDateTime timeOut = transfer.getDate().atTime(transfer.getTimeOut());
        double distance = GeoHelper.getDistance(transfer.getDeparture(), transfer.getDestination());
        double speed;
        double minTime;
        switch (transfer.getMethod()) {
            case ROADS:
                speed = 30;
                minTime = 0.25;
                break;
            case FLIGHTS:
                speed = 400;
                minTime = 2;
                break;
            case MACH_3:
                speed = 1500;
                minTime = 0.25;
                break;
            default:
                throw new IllegalStateException("Could not determine speed for " + transfer.getMethod() + " transfer method");
        }

        double hours = minTime + distance / speed;

        if (hours >= 24) {
            throw new IllegalStateException("Transfer longer than 24 hours is not supported");
        }

        LocalDateTime timeIn = timeOut.plusMinutes((int) (hours * 60));

        Transfer.Builder builder = new Transfer.Builder(transfer);
        if (timeIn.isAfter(Time.now())) {
            builder.setStatus(Transfer.Status.IN_PROGRESS);
            builder.setEstimatedTimeIn(timeIn.toLocalTime());
        } else {
            builder.setStatus(Transfer.Status.DONE);
            builder.setTimeIn(timeIn.toLocalTime());
            builder.setEstimatedTimeIn(null);
        }

        return builder.build();
    }

    public ValidationResult add(LogBookEntry entry) {
        compute();

        if (entries.isEmpty()) {
            entries.add(entry);
            return ValidationResult.OK;
        }

        LogBookEntry previous = entries.get(entries.size() - 1);

        ValidationResult result = validate(previous, entry);

        if (result.getOverallResult() == ValidationResult.Result.OK) {
            entries.add(entry);
        }

        return result;
    }

    public ValidationResult insert(int position, LogBookEntry entry) {
        compute();

        if (entries.isEmpty()) {
            entries.add(entry);
            return ValidationResult.OK;
        }

        LogBookEntry previous = position > 0 ? entries.get(position - 1) : null;
        LogBookEntry next = position < entries.size() ? entries.get(position) : null;

        ValidationResult validationAgainstPrevious = validate(previous, entry);
        ValidationResult validationAgainstNext = validate(entry, next);
        ValidationResult result = ValidationResult.combine(validationAgainstPrevious, validationAgainstNext);

        if (result.getOverallResult() == ValidationResult.Result.OK) {
            entries.add(position, entry);
        }

        return result;
    }

    public ValidationResult remove(int position) {
        compute();

        LogBookEntry previous = position > 0 ? entries.get(position - 1) : null;
        LogBookEntry next = position < entries.size() - 1 ? entries.get(position + 1) : null;

        if (previous != null && next != null) {
            ValidationResult result = validate(previous, next);
            if (result.getOverallResult() == ValidationResult.Result.OK) {
                entries.remove(position);
            }
            return result;
        } else {
            return ValidationResult.createOK();
        }
    }

    private ValidationResult validate(LogBookEntry entry1, LogBookEntry entry2) {
        if (entry1 instanceof Movement && entry2 instanceof Movement) {
            return validate((Movement) entry1, (Movement) entry2);
        } else {
            return ValidationResult.OK;
        }
    }

    private ValidationResult validate(Movement movement1, Movement movement2) {
        ValidationResult result = ValidationResult.createOK();

        // location check
        if (movement1.getDestination() == null) {
            result.fail("Destination of previous record is unknown");
        } else if (movement2.getDeparture() == null) {
            result.fail("Departure of next record is unknown");
        } else if (!movement1.getDestination().equals(movement2.getDeparture())) {
            result.fail("Departure of next record does not match destination of previous record");
        }

        // time check
        if (movement1.getDate() == null) {
            result.fail("Date of previous record is unknown");
        } else if (movement1.getTimeIn() == null) {
            result.fail("Time IN of previous record is unknown");
        } else if (movement2.getDate() == null) {
            result.fail("Date of next record is unknown");
        } else if (movement2.getTimeOut() == null) {
            result.fail("Time OUT of previous record is unknown");
        } else if (movement1.getDateTimeIn().isAfter(movement2.getDateTimeOut())) {
            result.fail("Time OUT of next record is before Time IN of previous record");
        }

        return result;
    }
}
