package org.springframework.samples.petclinic.teleconsultation;

public class TeleconsultationConflictException extends RuntimeException {
    public TeleconsultationConflictException(String message) {
        super(message);
    }
}
