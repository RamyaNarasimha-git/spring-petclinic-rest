package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;

import java.time.OffsetDateTime;

public record AssignVetRequest(
    @NotNull @Positive Integer vetId,
    @NotNull OffsetDateTime scheduledStart,
    @NotNull OffsetDateTime scheduledEnd,
    @NotNull TeleconsultationBooking.Priority priority,
    @NotNull Long version
) {
    public AssignVetRequest {
        if (scheduledStart != null && scheduledEnd != null && !scheduledEnd.isAfter(scheduledStart)) {
            throw new IllegalArgumentException("scheduledEnd must be after scheduledStart");
        }
    }
}
