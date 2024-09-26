package net.simforge.fslog.poc;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static net.simforge.fslog.poc.ValidationResult.Result.FAIL;
import static net.simforge.fslog.poc.ValidationResult.Result.OK;
import static org.junit.Assert.assertEquals;

public class LogBookTest {
    @Test
    public void testAddCorrectFlightAfterTransfer() {
        LogBook logBook = new LogBook();

        logBook.add(transferRoads(LocalDate.of(2019, 8, 1), "EGLL", "EGKK", LocalTime.of(12, 0)));
        ValidationResult result = logBook.add(flight(LocalDate.of(2019, 8, 2), "EGKK", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        assertEquals(OK, result.getOverallResult());
    }

    @Test
    public void testAddIncorrectFlightAfterTransfer_time() {
        LogBook logBook = new LogBook();

        logBook.add(transferRoads(LocalDate.of(2019, 8, 1), "EGLL", "EGKK", LocalTime.of(12, 0)));
        ValidationResult result = logBook.add(flight(LocalDate.of(2019, 8, 1), "EGKK", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        assertEquals(FAIL, result.getOverallResult());
    }

    @Test
    public void testAddIncorrectFlightAfterTransfer_location() {
        LogBook logBook = new LogBook();

        logBook.add(transferRoads(LocalDate.of(2019, 8, 1), "EGLL", "EGKK", LocalTime.of(12, 0)));
        ValidationResult result = logBook.add(flight(LocalDate.of(2019, 8, 2), "EGCC", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        assertEquals(FAIL, result.getOverallResult());
    }

    @Test
    public void testAddCorrectFlightAfterCorrectButNotComputedTransfer() {
        LogBook logBook = new LogBook();

        logBook.add(transferRoads(LocalDate.of(2019, 8, 1), "EGLL", "EGKK", LocalTime.of(12, 0)));
        ValidationResult result = logBook.add(flight(LocalDate.of(2019, 8, 1), "EGKK", "EGTB", LocalTime.of(15, 0), LocalTime.of(16, 0)));
        assertEquals(OK, result.getOverallResult());
    }

    @Test
    public void testAddCorrectDiscontinuityAfterFlight() {
        LogBook logBook = new LogBook();

        logBook.add(transferRoads(LocalDate.of(2019, 8, 1), "EGLL", "EGKK", LocalTime.of(12, 0)));
        ValidationResult result = logBook.add(discontinuity());
        assertEquals(OK, result.getOverallResult());
    }

    @Test
    public void testInsertCorrectDiscontinuityBetweenFlights() {
        LogBook logBook = new LogBook();

        logBook.add(flight(LocalDate.of(2019, 8, 2), "EGKK", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        logBook.add(flight(LocalDate.of(2019, 8, 3), "EGTB", "EGKK", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        ValidationResult result = logBook.insert(1, discontinuity());
        assertEquals(OK, result.getOverallResult());
    }

    @Test
    public void testJoiningCorrectDiscontinuity() {
        LogBook logBook = new LogBook();

        logBook.add(flight(LocalDate.of(2019, 8, 2), "EGKK", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        logBook.add(discontinuity());
        logBook.add(flight(LocalDate.of(2019, 8, 2), "EGTB", "EGKK", LocalTime.of(12, 0), LocalTime.of(13, 0)));

        ValidationResult result = logBook.remove(1);
        assertEquals(OK, result.getOverallResult());
    }

    @Test
    public void testJoiningIncorrectDiscontinuity() {
        LogBook logBook = new LogBook();

        logBook.add(flight(LocalDate.of(2019, 8, 2), "EGKK", "EGTB", LocalTime.of(10, 0), LocalTime.of(11, 0)));
        logBook.add(discontinuity());
        logBook.add(flight(LocalDate.of(2019, 8, 2), "EGTB", "EGKK", LocalTime.of(10, 30), LocalTime.of(13, 0)));

        ValidationResult result = logBook.remove(1);
        assertEquals(FAIL, result.getOverallResult());
    }

    private FlightReport flight(LocalDate date, String dep, String dest, LocalTime out, LocalTime in) {
        return new FlightReport.Builder()
                .setDate(date)
                .setDeparture(dep)
                .setDestination(dest)
                .setTimeOut(out)
                .setTimeIn(in)
                .build();
    }

    private Transfer transferRoads(LocalDate date, String dep, String dest, LocalTime out) {
        return new Transfer.Builder()
                .setDate(date)
                .setDeparture(dep)
                .setDestination(dest)
                .setTimeOut(out)
                .setMethod(Transfer.Method.ROADS)
                .build();
    }

    private LogBookEntry discontinuity() {
        return new Discontinuity.Builder()
                .build();
    }

}
