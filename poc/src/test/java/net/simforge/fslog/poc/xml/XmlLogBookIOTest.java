package net.simforge.fslog.poc.xml;

import net.simforge.fslog.poc.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlLogBookIOTest {
    @Test
    public void test_readFlightReport() {
        LogBook logBook = XmlLogBookIO.readLogBook(XmlLogBookIOTest.class.getResourceAsStream("/test-logbook.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        FlightReport flightReport = (FlightReport) entries.get(2);

        assertFlightReport(flightReport);
    }

    @Test
    public void test_readDiscontinuity() {
        LogBook logBook = XmlLogBookIO.readLogBook(XmlLogBookIOTest.class.getResourceAsStream("/test-logbook.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        Discontinuity discontinuity = (Discontinuity) entries.get(0);

        assertEquals(LocalDate.of(2019, 1, 27), discontinuity.getDate());
    }

    @Test
    public void test_readTransfer() {
        LogBook logBook = XmlLogBookIO.readLogBook(XmlLogBookIOTest.class.getResourceAsStream("/test-logbook.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        Transfer transfer = (Transfer) entries.get(1);

        assertEquals(LocalDate.of(2019, 4, 15), transfer.getDate());
        assertEquals("KBFL", transfer.getDeparture());
        assertEquals("EGTB", transfer.getDestination());
    }

    @Test
    public void test_readFlightReport_then_write_then_compare() {
        LogBook logBook = XmlLogBookIO.readLogBook(XmlLogBookIOTest.class.getResourceAsStream("/test-logbook.xml"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlLogBookIO.writeLogBook(logBook, baos);

        logBook = XmlLogBookIO.readLogBook(new ByteArrayInputStream(baos.toByteArray()));
        List<LogBookEntry> entries = logBook.getEntries();

        FlightReport flightReport = (FlightReport) entries.get(2);

        assertFlightReport(flightReport);
    }

    private void assertFlightReport(FlightReport flightReport) {
        assertEquals(LocalDate.of(2019, 7, 16), flightReport.getDate());
        assertEquals("EGSF", flightReport.getDeparture());
        assertEquals("EGTB", flightReport.getDestination());
    }
}
