package org.springframework.samples.petclinic.teleconsultation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

@Schema(description = "Request to create an emergency teleconsultation")
public record TeleconsultationCreateRequest(
        @NotNull
        @Schema(description = "Pet id", example = "3")
        Integer petId,

        @NotNull
        @Schema(description = "Contact method", example = "VIDEO")
        ContactMethod contactMethod,

        @Schema(description = "If true, book ASAP. If false, requestedStart must be provided")
        Boolean asap,

        @Future
        @Schema(description = "Requested start timestamp when asap=false", example = "2030-01-01T10:00:00Z")
        OffsetDateTime requestedStart,

        @Size(max = 2048)
        @Schema(description = "Optional notes", example = "Breathing difficulty")
        String notes
) {
}
