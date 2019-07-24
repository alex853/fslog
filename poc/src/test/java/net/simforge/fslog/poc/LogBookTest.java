package net.simforge.fslog.poc;

import org.junit.Test;

import static net.simforge.fslog.poc.ValidationResult.Result.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LogBookTest {
    @Test
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
    }
}
