package net.simforge.fslog.poc;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    public static final ValidationResult OK = new ValidationResult(Result.OK);

    private Result overallResult;
    private List<String> messages = new ArrayList<>();
    private List<ValidationResult> nested = new ArrayList<>();

    public ValidationResult(Result overallResult) {
        this.overallResult = overallResult;
    }

    public static ValidationResult createOK() {
        return new ValidationResult(Result.OK);
    }

    public static ValidationResult combine(ValidationResult result1, ValidationResult result2) {
        ValidationResult result = createOK();
        result.nested.add(result1);
        result.nested.add(result2);
        result.overallResult = Result.intersect(result1.getOverallResult(), result2.getOverallResult());
        return result;
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
        FAIL;

        public static Result intersect(Result... results) {
            Result totalResult = OK;
            for (Result result : results) {
                if (result == FAIL) {
                    totalResult = FAIL;
                }
            }
            return totalResult;
        }
    }
}
