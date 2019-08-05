package net.simforge.fslog.poc;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    public static final ValidationResult OK = new ValidationResult(Result.OK);

    private Result overallResult;
    private List<String> messages = new ArrayList<>();

    public ValidationResult(Result overallResult) {
        this.overallResult = overallResult;
    }

    public static ValidationResult createOK() {
        return new ValidationResult(Result.OK);
    }

    public Result getOverallResult() {
        return overallResult;
    }

    public void fail(String message) {
        messages.add(message);
        overallResult = Result.FAIL;
    }

    public enum Result {
        OK,
        FAIL
    }
}
