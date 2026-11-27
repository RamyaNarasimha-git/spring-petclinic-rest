package org.springframework.samples.petclinic.rest.dto;

import org.springframework.samples.petclinic.model.TeleconsultationBooking;

import java.time.OffsetDateTime;

public record TeleconsultationBookingResponse(
    Integer id,
    Integer ownerId,
    Integer petId,
    Integer vetId,
    String ownerName,
    String petName,
    String vetName,
    TeleconsultationBooking.Priority priority,
    TeleconsultationBooking.BookingStatus status,
    TeleconsultationBooking.ConsultationChannel channel,
    String symptoms,
    String contactEmail,
    String contactPhone,
    OffsetDateTime requestedAt,
    OffsetDateTime scheduledStart,
    OffsetDateTime scheduledEnd,
    String cancellationReason,
    long version
) {
}
