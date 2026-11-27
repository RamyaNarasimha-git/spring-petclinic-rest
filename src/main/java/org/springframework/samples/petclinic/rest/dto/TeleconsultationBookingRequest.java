package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;

public record TeleconsultationBookingRequest(
    @NotNull @Positive Integer ownerId,
    @NotNull @Positive Integer petId,
    @NotNull TeleconsultationBooking.ConsultationChannel channel,
    @NotBlank @Size(max = 2000) String symptoms,
    @Email @Size(max = 254) String contactEmail,
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,32}$") @Size(max = 32) String contactPhone,
    @NotNull Boolean emergency
) {
    public TeleconsultationBookingRequest {
        if (symptoms != null) {
            symptoms = symptoms.trim();
        }
        if (contactEmail != null) {
            contactEmail = contactEmail.trim().toLowerCase();
        }
        if (contactPhone != null) {
            contactPhone = contactPhone.trim();
        }
    }
}
