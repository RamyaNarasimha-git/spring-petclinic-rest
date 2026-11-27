package org.springframework.samples.petclinic.consultation;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_consultation_slots",
        uniqueConstraints = @UniqueConstraint(name = "uk_video_slot_vet_window", columnNames = { "vet_id", "start_time",
                "end_time" }))
public class VideoConsultationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vet_id", nullable = false)
    private Integer vetId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "booked", nullable = false)
    private Integer booked;

    @Version
    private Integer version;

    protected VideoConsultationSlot() {
    }

    public VideoConsultationSlot(Integer vetId, LocalDateTime startTime, LocalDateTime endTime, Integer capacity) {
        this.vetId = vetId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.booked = 0;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVetId() {
        return vetId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getBooked() {
        return booked;
    }

    public boolean hasCapacity() {
        return booked < capacity;
    }

    public void incrementBooked() {
        this.booked = this.booked + 1;
    }
}
