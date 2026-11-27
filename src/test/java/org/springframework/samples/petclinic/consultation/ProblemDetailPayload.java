package org.springframework.samples.petclinic.consultation;

public record ProblemDetailPayload(String type, String title, int status, String detail) {
}
