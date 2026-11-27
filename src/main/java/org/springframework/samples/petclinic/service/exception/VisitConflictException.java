package org.springframework.samples.petclinic.service.exception;

public class VisitConflictException extends RuntimeException {

    private final String field;
    private final String rejectedValue;

    public VisitConflictException(String field, String rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
