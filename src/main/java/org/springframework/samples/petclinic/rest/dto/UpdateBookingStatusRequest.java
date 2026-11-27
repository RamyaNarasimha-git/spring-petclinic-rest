package org.springframework.samples.petclinic.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;

public record UpdateBookingStatusRequest(
    @NotNull TeleconsultationBooking.BookingStatus status,
    @Size(max = 512) String cancellationReason,
    @NotNull Long version
) {
    public UpdateBookingStatusRequest {
        if (cancellationReason != null) {
            cancellationReason = cancellationReason.trim();
        }
        if (status == TeleconsultationBooking.BookingStatus.CANCELLED && (cancellationReason == null || cancellationReason.isBlank())) {
            throw new IllegalArgumentException("cancellationReason is required when status is CANCELLED");
        }
    }
}
