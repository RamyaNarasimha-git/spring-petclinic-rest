package org.springframework.samples.petclinic.service.exception;

public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(Integer petId) {
        super("Pet with id %s was not found".formatted(petId));
    }
}
