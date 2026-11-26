package org.springframework.samples.petclinic.teleconsultation;

public class TeleconsultationValidationException extends RuntimeException {
    public TeleconsultationValidationException(String message) {
        super(message);
    }
}
