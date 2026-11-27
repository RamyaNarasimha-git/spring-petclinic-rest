package org.springframework.samples.petclinic.mapper;

import org.springframework.samples.petclinic.model.TeleconsultationBooking;
import org.springframework.samples.petclinic.rest.dto.TeleconsultationBookingResponse;
import org.springframework.stereotype.Component;

@Component
public class TeleconsultationBookingMapper {

    public TeleconsultationBookingResponse toResponse(TeleconsultationBooking booking) {
        String ownerName = booking.getOwner() != null ? booking.getOwner().getFirstName() + " " + booking.getOwner().getLastName() : null;
        String petName = booking.getPet() != null ? booking.getPet().getName() : null;
        String vetName = booking.getVet() != null ? booking.getVet().getFirstName() + " " + booking.getVet().getLastName() : null;

        return new TeleconsultationBookingResponse(
            booking.getId(),
            booking.getOwner() != null ? booking.getOwner().getId() : null,
            booking.getPet() != null ? booking.getPet().getId() : null,
            booking.getVet() != null ? booking.getVet().getId() : null,
            ownerName,
            petName,
            vetName,
            booking.getPriority(),
            booking.getStatus(),
            booking.getChannel(),
            booking.getSymptoms(),
            booking.getContactEmail(),
            booking.getContactPhone(),
            booking.getRequestedAt(),
            booking.getScheduledStart(),
            booking.getScheduledEnd(),
            booking.getCancellationReason(),
            booking.getVersion()
        );
    }
}
