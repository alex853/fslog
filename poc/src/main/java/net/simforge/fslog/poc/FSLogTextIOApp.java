package net.simforge.fslog.poc;

import net.simforge.commons.misc.Str;
import org.beryx.textio.TerminalProperties;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static net.simforge.fslog.poc.FSLogConsoleApp.HHmm;

public class FSLogTextIOApp {
    private static final String CLEAR_SCREEN = "CLEAR_SCREEN";
    private static TextIO textIO;
    private static TextTerminal terminal;
    private static TerminalProperties<?> terminalProperties;

    public static final Column[] MOVEMENT_COLUMNS = new Column[]{
            new RowNumberColumn(),
            new DateOfFlightColumn(),
            new MovementTypeColumn(),
            new SimpleColumn("Callsign", 8, "Callsign"),
            new SimpleColumn("Flight #", 8, "FlightNumber"),
            new SimpleColumn("Type", 4, "AircraftType"),
            new SimpleColumn("Reg #", 8, "AircraftRegistration"),
            new SimpleColumn("Dep", 4, "Departure"),
            new SimpleColumn("Dest", 4, "Destination"),
            new TimeColumn(TimeType.TimeOut),
            new TimeColumn(TimeType.TimeOff),
            new TimeColumn(TimeType.TimeOn),
            new TimeColumn(TimeType.TimeIn),
            new DistanceColumn(),
            new SimpleColumn("Comment", 50, "Comment")
    };

    public static final Column[] DISCONTINUITY_COLUMNS = new Column[]{
            new RowNumberColumn(),
            new Column<Discontinuity>("no-header", 100) {
                @Override
                String format(Discontinuity discontinuity, Map<String, Object> ctx) {
                    return "---===|||===---===|||===--- " + discontinuity.getComment();
                }

                @Override
                void read(Object builder) {

                }
            }
    };
    public static void main(String[] args) {
        textIO = TextIoFactory.getTextIO();
        terminal = textIO.getTextTerminal();
        terminalProperties = terminal.getProperties();

        LogBook logBook = FSLogConsoleApp.loadLogBook();

        terminal.setBookmark(CLEAR_SCREEN);
        printLogBook(logBook);

        while (true) {
            terminal.println();
            String action = textIO
                    .newStringInputReader()
                    .withNumberedPossibleValues(
                            "Add Flight",
                            "Add Transfer",
                            "Add Discontinuity",
                            "Join Discontinuity",
                            "Save",
                            "Quit")
                    .read("Select action");
            terminal.println(action);

            if (action.equals("Add Flight")) {
                addFlight(logBook);
            } else if (action.equals("Add Discontinuity")) {
                addDiscontinuity(logBook);
            } else if (action.equals("Save")) {
                FSLogConsoleApp.saveLogBook(logBook);
                terminal.println("Logbook saved");
            } else if (action.equals("Quit")) {
                break;
            }
        }

        textIO.dispose();
    }

    private static void addFlight(LogBook logBook) {
        List<LogBookEntry> entries = logBook.getEntries();
        Integer position;
        Movement previousMovement = null;
        List<LogBookEntry> prevEntries;
        if (entries.isEmpty()) {
            position = 0;
            prevEntries = new ArrayList<>();
        } else {
            position = textIO.newIntInputReader()
                    .withMinVal(0)
                    .withMaxVal(entries.size())
                    .withDefaultValue(entries.size())
                    .read("Specify ### of row after which you are going to add new flight (or 0 to add as first entry)");

            LogBookEntry previousEntry = position >= 1 ? entries.get(position - 1) : null;
            LogBookEntry nextEntry = position < entries.size() ? entries.get(position) : null;

            if (previousEntry instanceof Movement) {
                previousMovement = (Movement) previousEntry;
            }

            prevEntries = entries.subList(Math.max(0, position - 5), position);

            if (nextEntry != null) {
                if (!(nextEntry instanceof Discontinuity)) {
                    terminal.println("Unable to insert new flight before flight or transfer, it should be discontinuity only");
                    return;
                }
            }
        }

        terminal.resetToBookmark(CLEAR_SCREEN);
        terminal.println("Adding flight...........");
        terminal.println();

        for (Column column : MOVEMENT_COLUMNS) {
            terminal.print(align(column, column.header));
            terminal.print(" ");
        }
        terminal.println();

        TerminalProperties<?> props = terminal.getProperties();
        Map<String, Object> ctx = new HashMap<>();

        int rowNumber = position - prevEntries.size() + 1;
        for (LogBookEntry entry : prevEntries) {
            Column[] columns = MOVEMENT_COLUMNS;
            if (entry instanceof FlightReport) {
                props.setPromptColor(Color.GREEN);
            } else if (entry instanceof Transfer) {
                props.setPromptColor(Color.CYAN);
            } else if (entry instanceof Discontinuity) {
                props.setPromptColor(Color.YELLOW);
                columns = DISCONTINUITY_COLUMNS;
            }

            ctx.put("rowNumber", rowNumber);

            for (Column column : columns) {
                terminal.print(align(column, column.format(entry, ctx)));
                terminal.print(" ");
            }
            terminal.println();

            rowNumber++;
        }

        ctx.put("rowNumber", position + 1);

        FlightReport.Builder builder = new FlightReport.Builder();

        int DOF_COLUMN_INDEX = 1;
        int DEP_COLUMN_INDEX = 7;
        for (int currentColumnIndex = DOF_COLUMN_INDEX; currentColumnIndex < MOVEMENT_COLUMNS.length; currentColumnIndex++) {
            FlightReport newFlight = builder.build();
            for (int i = 0; i < currentColumnIndex; i++) {
                Column column = MOVEMENT_COLUMNS[i];
                terminal.print(align(column, column.format(newFlight, ctx)));
                if (i < currentColumnIndex - 1) {
                    terminal.print(" ");
                }
            }

            if (currentColumnIndex == DEP_COLUMN_INDEX && previousMovement != null) {
                builder.setDeparture(previousMovement.getDestination());
            } else {
                Column currentColumn = MOVEMENT_COLUMNS[currentColumnIndex];
                currentColumn.read(builder);
            }

            terminal.resetLine();
        }

        FlightReport newFlight = builder.build();
        logBook.insert(position, newFlight);

        terminal.resetToBookmark(CLEAR_SCREEN);
        printLogBook(logBook);
    }

    private static void addDiscontinuity(LogBook logBook) {
        List<LogBookEntry> entries = logBook.getEntries();
        Integer position;

        List<LogBookEntry> prevEntries;
        List<LogBookEntry> nextEntries;
        if (entries.isEmpty()) {
            position = 0;
            prevEntries = new ArrayList<>();
            nextEntries = new ArrayList<>();
        } else {
            position = textIO.newIntInputReader()
                    .withMinVal(0)
                    .withMaxVal(entries.size())
                    .withDefaultValue(entries.size())
                    .read("Specify ### of row after which you are going to add new discontinuity (or 0 to add as first entry)");

            prevEntries = entries.subList(Math.max(0, position - 3), position);
            nextEntries = entries.subList(position, Math.min(position + 3, entries.size()));
        }

        terminal.resetToBookmark(CLEAR_SCREEN);
        terminal.println("Adding Discontinuity...........");
        terminal.println();

        for (Column column : MOVEMENT_COLUMNS) {
            terminal.print(align(column, column.header));
            terminal.print(" ");
        }
        terminal.println();

        Map<String, Object> ctx = new HashMap<>();

        int rowNumber = position - prevEntries.size() + 1;

        List<LogBookEntry> subset = new ArrayList<>(prevEntries);
        subset.add(new Discontinuity.Builder().setComment("<<< NEW DISCONTINUITY WILL BE CREATED HERE >>>").build());
        subset.addAll(nextEntries);

        for (LogBookEntry entry : subset) {
            Column[] columns = MOVEMENT_COLUMNS;
            if (entry instanceof FlightReport) {
                terminalProperties.setPromptColor(Color.GREEN);
            } else if (entry instanceof Transfer) {
                terminalProperties.setPromptColor(Color.CYAN);
            } else if (entry instanceof Discontinuity) {
                terminalProperties.setPromptColor(Color.YELLOW);
                columns = DISCONTINUITY_COLUMNS;
            }

            ctx.put("rowNumber", rowNumber);

            for (Column column : columns) {
                terminal.print(align(column, column.format(entry, ctx)));
                terminal.print(" ");
            }
            terminal.println();

            rowNumber++;
        }

        String comment = textIO.newStringInputReader().withMinLength(0).read("Specify comment for new discontinuity");

        Discontinuity newDiscontinuity = new Discontinuity.Builder().setComment(comment).build();
        logBook.insert(position, newDiscontinuity);

        terminal.resetToBookmark(CLEAR_SCREEN);
        printLogBook(logBook);
    }

    private static void printLogBook(LogBook logBook) {
        List<LogBookEntry> entries = logBook.getEntries();

        TerminalProperties<?> props = terminal.getProperties();

        for (Column column : MOVEMENT_COLUMNS) {
            terminal.print(align(column, column.header));
            terminal.print(" ");
        }
        terminal.println();

        Map<String, Object> ctx = new HashMap<>();
        int rowNumber = 1;
        for (LogBookEntry entry : entries) {
            Column[] columns = MOVEMENT_COLUMNS;
            if (entry instanceof FlightReport) {
                props.setPromptColor(Color.GREEN);
            } else if (entry instanceof Transfer) {
                props.setPromptColor(Color.CYAN);
            } else if (entry instanceof Discontinuity) {
                props.setPromptColor(Color.YELLOW);
                columns = DISCONTINUITY_COLUMNS;
            }

            ctx.put("rowNumber", rowNumber);

            for (Column column : columns) {
                terminal.print(align(column, column.format(entry, ctx)));
                terminal.print(" ");
            }
            terminal.println();

            rowNumber++;
        }
    }

    private static String align(Column column, String value) {
        if (value == null) {
            return Str.al("", column.width);
        }
        value = column.leftAlign ? Str.al(value, column.width) : Str.ar(value, column.width);
        value = Str.limit(value, column.width);
        return value;
    }

    private abstract static class Column<T> {
        protected final String header;
        protected final int width;
        protected final boolean leftAlign;

        public Column(String header, int width) {
            this.header = header;
            this.width = width;
            this.leftAlign = true;
        }

        public Column(String header, int width, boolean leftAlign) {
            this.header = header;
            this.width = width;
            this.leftAlign = leftAlign;
        }

        abstract String format(T value, Map<String, Object> ctx);

        abstract void read(Object builder);
    }

    private static class SimpleColumn extends Column<LogBookEntry> {
        private final String propertyName;

        public SimpleColumn(String header, int width, String propertyName) {
            super(header, width);
            this.propertyName = propertyName;
        }

        @Override
        String format(LogBookEntry entry, Map<String, Object> ctx) {
            try {
                String getterName = "get" + propertyName;
                Method declaredMethod = entry.getClass().getDeclaredMethod(getterName);
                Object v = declaredMethod.invoke(entry);
                if (v == null) {
                    return null;
                }
                return v.toString();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }

        @Override
        void read(Object builder) {
            String input = textIO.newStringInputReader().withMinLength(0).withMaxLength(width).read();
            try {
                String setterName = "set" + propertyName;
                Method declaredMethod = Arrays.stream(builder.getClass().getDeclaredMethods()).filter(m -> m.getName().equals(setterName)).findFirst().get();
                declaredMethod.invoke(builder, input);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // no op
            }

        }
    }

    private static class MovementTypeColumn extends Column<Movement> {
        public MovementTypeColumn() {
            super("", 8);
        }

        @Override
        String format(Movement movement, Map<String, Object> ctx) {
            return movement instanceof FlightReport ? "Flight" : "Transfer";
        }

        @Override
        void read(Object builder) {
            // no op
        }
    }

    private static class DateOfFlightColumn extends Column<Movement> {
        public DateOfFlightColumn() {
            super("DOF", 10);
        }

        @Override
        String format(Movement movement, Map<String, Object> ctx) {
            LocalDate date = movement.getDate();
            return date.format(DateTimeFormatter.ISO_DATE);
        }

        @Override
        void read(Object builder) {
            String input = textIO.newStringInputReader().withMinLength(0).read();
            LocalDate date;
            if (input == null || input.trim().length() == 0) {
                date = LocalDate.now();
            } else {
                date = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
            }

            if (builder instanceof FlightReport.Builder) {
                ((FlightReport.Builder) builder).setDate(date);
            } else {
                ((Transfer.Builder) builder).setDate(date);
            }
        }
    }

    private static class DistanceColumn extends Column<Movement> {
        public DistanceColumn() {
            super("Dist", 4, false);
        }

        @Override
        String format(Movement movement, Map<String, Object> ctx) {
            if (movement instanceof Transfer)
                return null;
            FlightReport flightReport = (FlightReport) movement;
            if (flightReport.getDistance() == null)
                return null;
            return flightReport.getDistance().toString();
        }

        @Override
        void read(Object builder) {
            if (!(builder instanceof FlightReport.Builder)) {
                return;
            }

            Integer input = textIO.newIntInputReader().withMinVal(0).withDefaultValue(0).read();
            if (input == 0) {
                input = null;
            }

            ((FlightReport.Builder)builder).setDistance(input);
        }
    }

    private enum TimeType {
        TimeOut,
        TimeOff,
        TimeOn,
        TimeIn
    }

    private static class TimeColumn extends Column<Movement> {
        private final TimeType timeType;

        public TimeColumn(TimeType timeType) {
            super(timeType.equals(TimeType.TimeOut) ? "T.Out" :
                    timeType.equals(TimeType.TimeOff) ? "T.Off" :
                            timeType.equals(TimeType.TimeOn) ? "T.On" :
                                    "T.In", 5);
            this.timeType = timeType;
        }

        @Override
        String format(Movement movement, Map<String, Object> ctx) {
            LocalTime time = null;
            switch (timeType) {
                case TimeOut:
                    time = movement.getTimeOut();
                    break;
                case TimeOff:
                    if (movement instanceof FlightReport) {
                        time = ((FlightReport) movement).getTimeOff();
                    }
                    break;
                case TimeOn:
                    if (movement instanceof FlightReport) {
                        time = ((FlightReport) movement).getTimeOn();
                    }
                    break;
                case TimeIn:
                    time = movement.getTimeIn();
                    break;
            }
            return time != null ? time.format(HHmm) : null;
        }

        @Override
        void read(Object builder) {
            LocalTime time;

            String input = textIO.newStringInputReader().withMinLength(0).read();
            if (input == null || input.trim().length() == 0) {
                time = null;
            } else {
                time = LocalTime.parse(input, HHmm);
            }

            switch (timeType) {
                case TimeOut:
                    if (builder instanceof FlightReport.Builder) {
                        ((FlightReport.Builder) builder).setTimeOut(time);
                    } else {
                        ((Transfer.Builder) builder).setTimeOut(time);
                    }
                    break;
                case TimeOff:
                    if (builder instanceof FlightReport.Builder) {
                        ((FlightReport.Builder) builder).setTimeOff(time);
                    }
                    break;
                case TimeOn:
                    if (builder instanceof FlightReport.Builder) {
                        ((FlightReport.Builder) builder).setTimeOn(time);
                    }
                    break;
                case TimeIn:
                    if (builder instanceof FlightReport.Builder) {
                        ((FlightReport.Builder) builder).setTimeIn(time);
                    } else {
                        ((Transfer.Builder) builder).setTimeIn(time);
                    }
                    break;
            }

        }
    }

    private static class RowNumberColumn extends Column<LogBookEntry> {
        public RowNumberColumn() {
            super("###", 3);
        }

        @Override
        String format(LogBookEntry entry, Map<String, Object> ctx) {
            return Str.ar(Integer.toString((Integer) ctx.get("rowNumber")), width);
        }

        @Override
        void read(Object builder) {
            // no op
        }
    }
}
