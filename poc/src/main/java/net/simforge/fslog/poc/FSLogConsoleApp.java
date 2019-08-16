package net.simforge.fslog.poc;

import net.simforge.fslog.poc.xml.XmlLogBookIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class FSLogConsoleApp {

    public static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");

    private static final String filename = "C:\\Dropbox\\Dev\\FSLog\\xml\\alexey.xml";

    public static void main(String[] args) {
        LogBook logBook = loadLogBook();
        logBook.compute();
        printLogBook(logBook);

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("[A]dd, [S]ave, [Q]uit: ");
            String selectedAction = scanner.nextLine();

            if (selectedAction.equalsIgnoreCase("a")) {
                addFlight(logBook);
            } else if (selectedAction.equalsIgnoreCase("s")) {
                saveLogBook(logBook);
            } else if (selectedAction.equalsIgnoreCase("q")) {
                return;
            }

        }
    }

    private static void addFlight(LogBook logBook) {
        List<LogBookEntry> entries = logBook.getEntries();
        LogBookEntry lastEntry = entries.isEmpty() ? null : entries.get(entries.size() - 1);

        if (lastEntry == null) {
            throw new IllegalStateException("Adding to empty logbook is supported");
        }

        if (!(lastEntry instanceof Movement)) {
            throw new IllegalStateException("Previous entry should be flight report");
        }

        Movement previousMovement = (Movement) lastEntry;

        FlightReport.Builder builder = new FlightReport.Builder();

        System.out.println();
        System.out.println("Adding flight.....");
        System.out.println();

        System.out.print("Specify date of flight (or empty string for current date): ");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        LocalDate date;
        if (s == null || s.trim().length() == 0) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(s, DateTimeFormatter.ISO_DATE);
        }
        // todo check legality of date
        builder.setDate(date);

        System.out.println("Departure airport is: " + previousMovement.getDestination() + " (destination of last flight)");
        builder.setDeparture(previousMovement.getDestination());

        System.out.print("Specify destination airport: ");
        String destination = scanner.nextLine();
        // todo check destination correctness
        destination = destination.toUpperCase().trim();
        builder.setDestination(destination);

        System.out.print("Time OUT: ");
        builder.setTimeOut(readTime());
        // todo check not null
        // todo check overlapping

        System.out.print("Time OFF: ");
        builder.setTimeOff(readTime());
        // todo check it is not earlier than OUT

        System.out.print("Time ON: ");
        builder.setTimeOn(readTime());
        // todo check it is not earlier than OFF or OUT

        System.out.print("Time IN: ");
        builder.setTimeIn(readTime());
        // todo check not null
        // todo check it is not earlier than ON or OFF or OUT

        logBook.add(builder.build());
        printLogBook(logBook);
    }

    private static LocalTime readTime() {
        Scanner scanner = new Scanner(System.in);
        String time = scanner.nextLine();
        if (time == null || time.trim().length() == 0) {
            return null;
        }
        return LocalTime.parse(time, HHmm);
    }

    private static void printLogBook(LogBook logBook) {
        List<LogBookEntry> entries = logBook.getEntries();

        int number = 0;
        for (LogBookEntry entry : entries) {
            number++;
            if (entry instanceof FlightReport) {
                FlightReport flight = (FlightReport) entry;
                System.out.println(String.format("%3d   Flight    %s %5s %5s   %s  %s  %s  %s  %s             %10.2f %10.2f",
                        number,
                        flight.getDate().format(DateTimeFormatter.ISO_DATE),
                        flight.getDeparture(),
                        flight.getDestination(),
                        printTime(flight.getTimeOut()),
                        printTime(flight.getTimeOff()),
                        printTime(flight.getTimeOn()),
                        printTime(flight.getTimeIn()),
                        "-", 0.0, 0.0
//                        LocalTime.MIDNIGHT.plusSeconds(flight.getDuration().getSeconds()).format(HHmm),
//                        flight.getPilotMoney(),
//                        pilotStatus.getAccount()
                ));
            } else if (entry instanceof Transfer) {
                Transfer transfer = (Transfer) entry;
                System.out.println(String.format("%3d   TRANSFER  %s %5s %5s   %s  %s  %s  %s  %s             %10.2f %10.2f",
                        number,
                        transfer.getDate().format(DateTimeFormatter.ISO_DATE),
                        transfer.getDeparture(),
                        transfer.getDestination(),
                        printTime(transfer.getTimeOut()),
                        printTime(null),
                        printTime(null),
                        transfer.getStatus() == Transfer.Status.DONE ? printTime(transfer.getTimeIn()) : "-=|=-",
                        "-", 0.0, 0.0
//                        LocalTime.MIDNIGHT.plusSeconds(transfer.getDuration().getSeconds()).format(HHmm),
//                        transfer.getPilotMoney(),
//                        pilotStatus.getAccount()
                ));
            } else if (entry instanceof Discontinuity) {
                Discontinuity discontinuity = (Discontinuity) entry;
                System.out.println(String.format("%3d   ==|==|==  %s",
                        number,
                        discontinuity.getDate().format(DateTimeFormatter.ISO_DATE)
                ));
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private static String printTime(LocalTime time) {
        return time != null ? time.format(HHmm) : "     ";
    }

    private static LogBook loadLogBook() {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return XmlLogBookIO.readLogBook(fis);
    }

    private static void saveLogBook(LogBook logBook) {
        Path original = Paths.get(filename);
        Path copy = Paths.get(filename.replace(".xml", "." + System.currentTimeMillis() + ".xml"));
        try {
            Files.copy(original, copy, StandardCopyOption.REPLACE_EXISTING);

            FileOutputStream fos = new FileOutputStream(filename);
            XmlLogBookIO.writeLogBook(logBook, fos);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
