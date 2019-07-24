package net.simforge.fslog.poc.xml;

import net.simforge.fslog.poc.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlLogBookReaderTest {
    @Test
    public void test1() {
        LogBook logBook = XmlLogBookReader.readLogBook(XmlLogBookReaderTest.class.getResourceAsStream("/logbook1.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        assertEquals(1, entries.size());

        FlightReport flightReport = (FlightReport) entries.get(0);

        assertEquals(LocalDate.of(2019, 7, 16), flightReport.getDate());
        assertEquals("EGSF", flightReport.getDeparture());
        assertEquals("EGTB", flightReport.getDestination());
    }

    @Test
    public void test2() {
        LogBook logBook = XmlLogBookReader.readLogBook(XmlLogBookReaderTest.class.getResourceAsStream("/logbook2.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        assertEquals(1, entries.size());

        Discontinuity discontinuity = (Discontinuity) entries.get(0);

        assertEquals(LocalDate.of(2019, 1, 27), discontinuity.getDate());
    }

    @Test
    public void test3() {
        LogBook logBook = XmlLogBookReader.readLogBook(XmlLogBookReaderTest.class.getResourceAsStream("/logbook3.xml"));
        List<LogBookEntry> entries = logBook.getEntries();

        assertEquals(1, entries.size());

        Transfer transfer = (Transfer) entries.get(0);

        assertEquals(LocalDate.of(2019, 4, 15), transfer.getDate());
        assertEquals("KBFL", transfer.getDeparture());
        assertEquals("EGTB", transfer.getDestination());
    }
}
