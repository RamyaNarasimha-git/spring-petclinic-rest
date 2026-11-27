package org.springframework.samples.petclinic.consultation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_consultation_bookings")
public class VideoConsultationBooking {

    public enum Severity {
        MILD(1),
        MODERATE(5),
        SEVERE(10);

        private final int priority;

        Severity(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    public enum BookingStatus {
        REQUESTED,
        CONFIRMED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_id", nullable = false, unique = true, length = 64)
    private String externalId;

    @Column(name = "pet_id", nullable = false)
    private Integer petId;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "vet_id")
    private Integer vetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 16)
    private Severity severity;

    @Column(name = "triage_priority", nullable = false)
    private Integer triagePriority;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Version
    private Integer version;

    protected VideoConsultationBooking() {
    }

    public VideoConsultationBooking(String externalId, Integer petId, Integer ownerId, Severity severity,
            LocalDateTime startTime, LocalDateTime endTime) {
        this.externalId = externalId;
        this.petId = petId;
        this.ownerId = ownerId;
        this.severity = severity;
        this.triagePriority = severity.getPriority();
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = BookingStatus.REQUESTED;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Integer getPetId() {
        return petId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public Integer getVetId() {
        return vetId;
    }

    public void setVetId(Integer vetId) {
        this.vetId = vetId;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Integer getTriagePriority() {
        return triagePriority;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
