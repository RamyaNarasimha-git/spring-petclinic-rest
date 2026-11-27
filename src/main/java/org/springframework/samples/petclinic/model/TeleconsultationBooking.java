package org.springframework.samples.petclinic.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "teleconsultation_bookings",
    indexes = {
        @Index(name = "idx_tcb_owner_id", columnList = "owner_id"),
        @Index(name = "idx_tcb_pet_id", columnList = "pet_id"),
        @Index(name = "idx_tcb_vet_id", columnList = "vet_id"),
        @Index(name = "idx_tcb_status_priority_requested_at", columnList = "status,priority,requested_at")
    }
)
public class TeleconsultationBooking {

    public enum Priority {
        NORMAL,
        HIGH,
        CRITICAL
    }

    public enum BookingStatus {
        REQUESTED,
        TRIAGED,
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    public enum ConsultationChannel {
        VIDEO,
        PHONE,
        CHAT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
    private Vet vet;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 16)
    private Priority priority = Priority.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 24)
    private BookingStatus status = BookingStatus.REQUESTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private ConsultationChannel channel = ConsultationChannel.VIDEO;

    @Column(name = "symptoms", nullable = false, length = 2000)
    private String symptoms;

    @Column(name = "contact_email", length = 254)
    private String contactEmail;

    @Column(name = "contact_phone", length = 32)
    private String contactPhone;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "scheduled_start")
    private OffsetDateTime scheduledStart;

    @Column(name = "scheduled_end")
    private OffsetDateTime scheduledEnd;

    @Column(name = "cancellation_reason", length = 512)
    private String cancellationReason;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.requestedAt == null) {
            this.requestedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Integer getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public ConsultationChannel getChannel() {
        return channel;
    }

    public void setChannel(ConsultationChannel channel) {
        this.channel = channel;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(OffsetDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public OffsetDateTime getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(OffsetDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public OffsetDateTime getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(OffsetDateTime scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeleconsultationBooking that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
