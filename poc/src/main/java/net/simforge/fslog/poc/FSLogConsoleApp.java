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

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        LogBook logBook = loadLogBook();
        logBook.compute();
        printLogBook(logBook);

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("Add [F]light, Add [D]iscontinuity, Add [T]ransfer, [J]oin, [S]ave, or [Q]uit: ");
            String selectedAction = scanner.nextLine();

            if (selectedAction.equalsIgnoreCase("f")) {
                addFlight(logBook);
            } else if (selectedAction.equalsIgnoreCase("t")) {
                addTransfer(logBook);
            } else if (selectedAction.equalsIgnoreCase("d")) {
                addDiscontinuity(logBook);
            } else if (selectedAction.equalsIgnoreCase("j")) {
                join(logBook);
            } else if (selectedAction.equalsIgnoreCase("s")) {
                saveLogBook(logBook);
            } else if (selectedAction.equalsIgnoreCase("q")) {
                return;
            }

        }
    }

    private static void addFlight(LogBook logBook) {
        System.out.println();
        System.out.println("Adding flight.....");
        System.out.println();

        Movement previousMovement = null;
        int position;

        List<LogBookEntry> entries = logBook.getEntries();
        if (!entries.isEmpty()) {
            System.out.print("Specify ## of flight AFTER which you are going to add flight (or empty for adding to tail, or 0 to add as first entry): ");
            String s = scanner.nextLine();

            if (s == null || s.trim().length() == 0) {
                position = entries.size();
            } else {
                position = Integer.parseInt(s);
            }

            LogBookEntry previousEntry = position >= 1 ? entries.get(position - 1) : null;
            LogBookEntry nextEntry = position < entries.size() ? entries.get(position) : null;

            if (previousEntry instanceof Movement) {
                previousMovement = (Movement) previousEntry;
            }

            if (nextEntry != null) {
                if (!(nextEntry instanceof Discontinuity)) {
                    System.out.println("Unable to insert new flight before flight or transfer, it should be discontinuity only");
                    return;
                }
            }
        } else {
            position = 0;
        }

        FlightReport.Builder builder = new FlightReport.Builder();

        System.out.print("Specify date of flight (or empty string for current date): ");
        String s = scanner.nextLine();
        LocalDate date;
        if (s == null || s.trim().length() == 0) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(s, DateTimeFormatter.ISO_DATE);
        }
        // todo check legality of date
        builder.setDate(date);

        System.out.print("Specify callsign: ");
        String callsign = scanner.nextLine();
        // todo check correctness
        callsign = callsign.toUpperCase().trim();
        builder.setCallsign(callsign);

        System.out.print("Specify flight number: ");
        String flightNumber = scanner.nextLine();
        // todo check correctness
        flightNumber = flightNumber.toUpperCase().trim();
        builder.setFlightNumber(flightNumber);

        System.out.print("Specify aircraft type: ");
        String aircraftType = scanner.nextLine();
        // todo check correctness
        aircraftType = aircraftType.toUpperCase().trim();
        builder.setAircraftType(aircraftType);

        System.out.print("Specify aircraft registration: ");
        String aircraftRegistration = scanner.nextLine();
        // todo check correctness
        aircraftRegistration = aircraftRegistration.toUpperCase().trim();
        builder.setAircraftRegistration(aircraftRegistration);

        if (previousMovement != null) {
            System.out.println("Departure airport is: " + previousMovement.getDestination() + " (destination of last flight)");
            builder.setDeparture(previousMovement.getDestination());
        } else {
            System.out.print("Specify departure airport: ");
            String departure = scanner.nextLine();
            // todo check departure correctness
            departure = departure.toUpperCase().trim();
            builder.setDeparture(departure);
        }

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

        FlightReport newFlightReport = builder.build();
//        logBook.add(newFlightReport);
        logBook.insert(position, newFlightReport);
        printLogBook(logBook);
    }

    private static void addTransfer(LogBook logBook) {
        System.out.println();
        System.out.println("Adding transfer.....");
        System.out.println();

        Movement previousMovement = null;
        int position;

        List<LogBookEntry> entries = logBook.getEntries();
        if (!entries.isEmpty()) {
            System.out.print("Specify ## of entry AFTER which you are going to add transfer (or empty for adding to tail, or 0 to add as first entry): ");
            String s = scanner.nextLine();

            if (s == null || s.trim().length() == 0) {
                position = entries.size();
            } else {
                position = Integer.parseInt(s);
            }

            LogBookEntry previousEntry = position >= 1 ? entries.get(position - 1) : null;
            LogBookEntry nextEntry = position < entries.size() ? entries.get(position) : null;

            if (previousEntry instanceof Movement) {
                previousMovement = (Movement) previousEntry;
            }

            if (nextEntry != null) {
                if (!(nextEntry instanceof Discontinuity)) {
                    System.out.println("Unable to insert new transfer before flight or transfer, it should be discontinuity only");
                    return;
                }
            }
        } else {
            position = 0;
        }

        Transfer.Builder builder = new Transfer.Builder();

        System.out.print("Specify date of transfer (or empty string for current date): ");
        String s = scanner.nextLine();
        LocalDate date;
        if (s == null || s.trim().length() == 0) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(s, DateTimeFormatter.ISO_DATE);
        }
        // todo check legality of date
        builder.setDate(date);

        if (previousMovement != null) {
            System.out.println("Departure airport is: " + previousMovement.getDestination() + " (destination of last flight)");
            builder.setDeparture(previousMovement.getDestination());
        } else {
            System.out.print("Specify departure airport: ");
            String departure = scanner.nextLine();
            // todo check departure correctness
            departure = departure.toUpperCase().trim();
            builder.setDeparture(departure);
        }

        System.out.print("Specify destination airport: ");
        String destination = scanner.nextLine();
        // todo check destination correctness
        destination = destination.toUpperCase().trim();
        builder.setDestination(destination);

        System.out.print("Time OUT (or empty for current time): ");
        LocalTime timeOut = readTime();
        if (timeOut == null) {
            timeOut = Time.now().toLocalTime();
        }
        builder.setTimeOut(timeOut);
        // todo check not null
        // todo check overlapping

        while (true) {
            System.out.print("Specify mode [R]oards, [F]lights: ");
            String mode = scanner.nextLine();

            if ("r".equalsIgnoreCase(mode)) {
                builder.setMethod(Transfer.Method.ROADS);
                break;
            } else if ("f".equalsIgnoreCase(mode)) {
                builder.setMethod(Transfer.Method.FLIGHTS);
                break;
            } else {
                System.out.println("Unknown mode specified");
            }
        }

        Transfer newTransfer = builder.build();
        logBook.insert(position, newTransfer);
        logBook.compute();
        printLogBook(logBook);
    }

    private static void addDiscontinuity(LogBook logBook) {
        System.out.println();
        System.out.println("Adding discontinuity.....");
        System.out.println();

        int position;

        List<LogBookEntry> entries = logBook.getEntries();
        if (!entries.isEmpty()) {
            System.out.print("Specify ## of entry AFTER which you are going to add discontinuity (or empty for adding to tail, or 0 to add as first entry): ");
            String s = scanner.nextLine();

            if (s == null || s.trim().length() == 0) {
                position = entries.size();
            } else {
                position = Integer.parseInt(s);
            }
        } else {
            position = 0;
        }

        Discontinuity.Builder builder = new Discontinuity.Builder();

        System.out.print("Specify comment: ");
        String comment = scanner.nextLine();
        builder.setComment(comment);

        Discontinuity newDiscontinuity = builder.build();
        logBook.insert(position, newDiscontinuity);
        printLogBook(logBook);
    }

    private static void join(LogBook logBook) {
        System.out.println();
        System.out.println("Joining discontinuity.....");
        System.out.println();

        int position;

        List<LogBookEntry> entries = logBook.getEntries();
        if (!entries.isEmpty()) {
            System.out.print("Specify ## of entry discontinuity you are going to join: ");
            String s = scanner.nextLine();

            if (s == null || s.trim().length() == 0) {
                return;
            }

            position = Integer.parseInt(s) - 1;

            LogBookEntry entry = entries.get(position);
            if (!(entry instanceof Discontinuity)) {
                System.out.println("Entry specified is not discontinuity, exiting");
                return;
            }
        } else {
            System.out.println("There is no entries in logbook, exiting");
            return;
        }

        logBook.remove(position);
        printLogBook(logBook);
    }

    private static LocalTime readTime() {
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
                System.out.println(String.format("%3d   Flight    %s %10s %10s %5s %10s %5s %5s   %s  %s  %s  %s  %s",
                        number,
                        flight.getDate().format(DateTimeFormatter.ISO_DATE),
                        m(flight.getCallsign()),
                        m(flight.getFlightNumber()),
                        m(flight.getAircraftType()),
                        m(flight.getAircraftRegistration()),
                        m(flight.getDeparture()),
                        m(flight.getDestination()),
                        printTime(flight.getTimeOut()),
                        printTime(flight.getTimeOff()),
                        printTime(flight.getTimeOn()),
                        printTime(flight.getTimeIn()),
                        m(flight.getComment())
                ));
            } else if (entry instanceof Transfer) {
                Transfer transfer = (Transfer) entry;
                System.out.println(String.format("%3d   TRANSFER  %s %10s %10s %5s %10s %5s %5s   %s  %s  %s  %s  %s",
                        number,
                        transfer.getDate().format(DateTimeFormatter.ISO_DATE),
                        "",
                        "",
                        "",
                        "",
                        transfer.getDeparture(),
                        transfer.getDestination(),
                        printTime(transfer.getTimeOut()),
                        printTime(null),
                        printTime(null),
                        transfer.getStatus() == Transfer.Status.DONE ? printTime(transfer.getTimeIn()) : "-=|=-",
                        m(transfer.getComment())
                ));
            } else if (entry instanceof Discontinuity) {
                Discontinuity discontinuity = (Discontinuity) entry;
                System.out.println(String.format("%3d   ==|==|==|==|==|==|== %s",
                        number,
                        m(discontinuity.getComment())
                ));
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private static String printTime(LocalTime time) {
        return time != null ? time.format(HHmm) : "     ";
    }

    private static String m(String s) {
        return m(s, "");
    }

    private static String m(String s, String def) {
        return s != null ? s : def;
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
