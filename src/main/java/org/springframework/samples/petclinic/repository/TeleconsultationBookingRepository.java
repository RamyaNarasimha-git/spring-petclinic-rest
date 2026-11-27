package org.springframework.samples.petclinic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface TeleconsultationBookingRepository extends JpaRepository<TeleconsultationBooking, Integer> {

    @Query("select b from TeleconsultationBooking b where b.id = :bookingId and b.owner.id = :ownerId")
    Optional<TeleconsultationBooking> findByIdAndOwnerId(@Param("bookingId") int bookingId, @Param("ownerId") int ownerId);

    @Query("select b from TeleconsultationBooking b where b.owner.id = :ownerId")
    Page<TeleconsultationBooking> findAllByOwnerId(@Param("ownerId") int ownerId, Pageable pageable);

    @Query("select b from TeleconsultationBooking b where b.owner.id = :ownerId and b.pet.id = :petId")
    Page<TeleconsultationBooking> findAllByOwnerIdAndPetId(@Param("ownerId") int ownerId, @Param("petId") int petId, Pageable pageable);

    @Query(
        "select b from TeleconsultationBooking b " +
        "where b.owner.id = :ownerId " +
        "and (:status is null or b.status = :status) " +
        "and (:priority is null or b.priority = :priority) " +
        "and (:from is null or b.requestedAt >= :from) " +
        "and (:to is null or b.requestedAt <= :to)"
    )
    Page<TeleconsultationBooking> findOwnerBookings(
        @Param("ownerId") int ownerId,
        @Param("status") TeleconsultationBooking.BookingStatus status,
        @Param("priority") TeleconsultationBooking.Priority priority,
        @Param("from") OffsetDateTime from,
        @Param("to") OffsetDateTime to,
        Pageable pageable
    );

    @Query(
        "select case when count(p) > 0 then true else false end " +
        "from Pet p where p.id = :petId and p.owner.id = :ownerId"
    )
    boolean existsPetOwnedBy(@Param("ownerId") int ownerId, @Param("petId") int petId);

    @Query("select b from TeleconsultationBooking b where b.vet.id = :vetId")
    Page<TeleconsultationBooking> findAllByVetId(@Param("vetId") int vetId, Pageable pageable);
}
