package org.springframework.samples.petclinic.consultation;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface VideoConsultationSlotRepository extends JpaRepository<VideoConsultationSlot, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<VideoConsultationSlot> findByVetIdAndStartTimeAndEndTime(Integer vetId, LocalDateTime startTime,
            LocalDateTime endTime);
}
