package org.springframework.samples.petclinic.teleconsultation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "Teleconsultation response")
public record TeleconsultationResponse(
        Integer id,
        Integer ownerId,
        Integer petId,
        Integer vetId,
        ContactMethod contactMethod,
        TeleconsultationStatus status,
        OffsetDateTime startTs,
        OffsetDateTime endTs,
        String meetingUrl,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TeleconsultationResponse from(Teleconsultation t) {
        return new TeleconsultationResponse(
                t.getId(),
                t.getOwnerId(),
                t.getPetId(),
                t.getVetId(),
                t.getContactMethod(),
                t.getStatus(),
                t.getStartTs(),
                t.getEndTs(),
                t.getMeetingUrl(),
                t.getNotes(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
