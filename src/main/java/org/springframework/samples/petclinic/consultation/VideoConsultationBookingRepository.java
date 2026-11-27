package org.springframework.samples.petclinic.consultation;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoConsultationBookingRepository extends JpaRepository<VideoConsultationBooking, Integer> {

    Optional<VideoConsultationBooking> findByExternalId(String externalId);

    @Query("select b from VideoConsultationBooking b where b.vetId = :vetId and b.status = 'CONFIRMED' and (b.startTime < :end and b.endTime > :start)")
    java.util.List<VideoConsultationBooking> findOverlappingConfirmedBookings(@Param("vetId") Integer vetId,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
