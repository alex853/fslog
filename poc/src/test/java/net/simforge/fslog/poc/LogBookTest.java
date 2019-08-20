package net.simforge.fslog.poc;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.LocalTime;

import static net.simforge.fslog.poc.ValidationResult.Result.FAIL;
import static net.simforge.fslog.poc.ValidationResult.Result.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LogBookTest {
/*    @Test
    public void testAddCorrectFlights() {
        LogBook logBook = new LogBook();

        FlightReport flight1 = null;
        FlightReport flight2 = null;

        ValidationResult result = logBook.add(flight1);
        assertEquals(OK, result.getOverallResult());

        result = logBook.add(flight2);
        assertEquals(OK, result.getOverallResult());

        assertEquals(2, logBook.getEntries().size());
    }

    @Test
    public void testAddOverlappingFlights() {
        fail();
    }*/

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
