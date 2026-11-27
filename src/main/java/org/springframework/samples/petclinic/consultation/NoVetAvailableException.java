package org.springframework.samples.petclinic.consultation;

public class NoVetAvailableException extends RuntimeException {

    public NoVetAvailableException(String message) {
        super(message);
    }
}
