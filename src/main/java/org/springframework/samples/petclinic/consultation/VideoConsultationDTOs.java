package org.springframework.samples.petclinic.consultation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class VideoConsultationDTOs {

    private VideoConsultationDTOs() {
    }

    public record CreateBookingRequest(@NotNull Integer petId, @NotNull Integer ownerId, @NotNull String severity,
            @NotNull LocalDateTime startTime, @NotNull LocalDateTime endTime,
            @Size(max = 500) String reason) {
    }

    public record AssignedVeterinarian(Integer vetId) {
    }

    public record BookingConfirmationResponse(String appointmentId, Integer petId, Integer ownerId, AssignedVeterinarian vet,
            Integer triagePriority, String status) {
    }

    public record JoinTokenResponse(String appointmentId, String token, long expiresInSeconds) {
    }
}
