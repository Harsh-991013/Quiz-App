package com.testmian.quiz_app.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int successCount;
    private int failedCount;
    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
        failedCount++;
    }

    public void incrementSuccess() {
        successCount++;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public List<String> getErrors() {
        return errors;
    }
}
