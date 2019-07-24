package net.simforge.fslog.poc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogBook {

    private List<LogBookEntry> entries = new ArrayList<>();

    public List<LogBookEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public ValidationResult add(LogBookEntry entry) {
        // todo checks
        entries.add(entry);

        return null;
    }
}
