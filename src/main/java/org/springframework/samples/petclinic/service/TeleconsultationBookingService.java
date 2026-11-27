package org.springframework.samples.petclinic.service;

import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.TeleconsultationBookingRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.rest.dto.AssignVetRequest;
import org.springframework.samples.petclinic.rest.dto.TeleconsultationBookingRequest;
import org.springframework.samples.petclinic.rest.dto.UpdateBookingStatusRequest;

import org.springframework.samples.petclinic.rest.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeleconsultationBookingService {

    private static final Set<TeleconsultationBooking.BookingStatus> TERMINAL_STATUSES =
        EnumSet.of(TeleconsultationBooking.BookingStatus.COMPLETED, TeleconsultationBooking.BookingStatus.CANCELLED, TeleconsultationBooking.BookingStatus.NO_SHOW);

    private final TeleconsultationBookingRepository bookingRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final VetRepository vetRepository;

    private final Map<String, Integer> idempotencyStore = new ConcurrentHashMap<>();

    public TeleconsultationBookingService(
        TeleconsultationBookingRepository bookingRepository,
        OwnerRepository ownerRepository,
        PetRepository petRepository,
        VetRepository vetRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
        this.vetRepository = vetRepository;
    }

    @Transactional
    public TeleconsultationBooking createBooking(TeleconsultationBookingRequest request, String idempotencyKey) {
        if (!ownerRepository.existsById(request.ownerId())) {
            throw new ResourceNotFoundException("Owner not found");
        }
        if (!bookingRepository.existsPetOwnedBy(request.ownerId(), request.petId())) {
            throw new IllegalArgumentException("Pet does not belong to owner");
        }

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Integer existingId = idempotencyStore.get(idempotencyKey);
            if (existingId != null) {
                return bookingRepository.findByIdAndOwnerId(existingId, request.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
            }
        }

        Owner owner = ownerRepository.findById(request.ownerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        Pet pet = petRepository.findById(request.petId()).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        TeleconsultationBooking booking = new TeleconsultationBooking();
        booking.setOwner(owner);
        booking.setPet(pet);
        booking.setChannel(request.channel());
        booking.setSymptoms(request.symptoms());
        booking.setContactEmail(request.contactEmail());
        booking.setContactPhone(request.contactPhone());
        booking.setRequestedAt(OffsetDateTime.now());
        booking.setPriority(Boolean.TRUE.equals(request.emergency()) ? TeleconsultationBooking.Priority.CRITICAL : TeleconsultationBooking.Priority.NORMAL);
        booking.setStatus(TeleconsultationBooking.BookingStatus.REQUESTED);

        TeleconsultationBooking saved = bookingRepository.save(booking);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyStore.putIfAbsent(idempotencyKey, saved.getId());
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public TeleconsultationBooking getBookingForOwner(int bookingId, int ownerId) {
        return bookingRepository.findByIdAndOwnerId(bookingId, ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    @Transactional(readOnly = true)
    public Page<TeleconsultationBooking> listOwnerBookings(
        int ownerId,
        TeleconsultationBooking.BookingStatus status,
        TeleconsultationBooking.Priority priority,
        OffsetDateTime from,
        OffsetDateTime to,
        Pageable pageable
    ) {
        if (!ownerRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("Owner not found");
        }
        return bookingRepository.findOwnerBookings(ownerId, status, priority, from, to, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TeleconsultationBooking> listPetBookings(int ownerId, int petId, Pageable pageable) {
        if (!bookingRepository.existsPetOwnedBy(ownerId, petId)) {
            throw new IllegalArgumentException("Pet does not belong to owner");
        }
        return bookingRepository.findAllByOwnerIdAndPetId(ownerId, petId, pageable);
    }

    @Transactional
    public TeleconsultationBooking updateStatus(int bookingId, int ownerId, UpdateBookingStatusRequest request) {
        TeleconsultationBooking booking = getBookingForOwner(bookingId, ownerId);

        if (!Objects.equals(booking.getVersion(), request.version())) {
            throw new OptimisticLockException("Version conflict");
        }

        if (TERMINAL_STATUSES.contains(booking.getStatus())) {
            throw new IllegalStateException("Cannot change status from terminal state");
        }

        validateStatusTransition(booking.getStatus(), request.status());

        booking.setStatus(request.status());
        if (request.status() == TeleconsultationBooking.BookingStatus.CANCELLED) {
            booking.setCancellationReason(request.cancellationReason());
        }

        try {
            return bookingRepository.saveAndFlush(booking);
        } catch (OptimisticLockingFailureException ex) {
            throw new OptimisticLockException("Version conflict", ex);
        }
    }

    @Transactional
    public TeleconsultationBooking assignVet(int bookingId, AssignVetRequest request) {
        TeleconsultationBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!Objects.equals(booking.getVersion(), request.version())) {
            throw new OptimisticLockException("Version conflict");
        }

        Vet vet = vetRepository.findById(request.vetId())
            .orElseThrow(() -> new ResourceNotFoundException("Vet not found"));

        booking.setVet(vet);
        booking.setScheduledStart(request.scheduledStart());
        booking.setScheduledEnd(request.scheduledEnd());
        booking.setPriority(request.priority());
        if (booking.getStatus() == TeleconsultationBooking.BookingStatus.REQUESTED || booking.getStatus() == TeleconsultationBooking.BookingStatus.TRIAGED) {
            booking.setStatus(TeleconsultationBooking.BookingStatus.SCHEDULED);
        }

        try {
            return bookingRepository.saveAndFlush(booking);
        } catch (OptimisticLockingFailureException ex) {
            throw new OptimisticLockException("Version conflict", ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<TeleconsultationBooking> listVetBookings(int vetId, Pageable pageable) {
        if (!vetRepository.existsById(vetId)) {
            throw new ResourceNotFoundException("Vet not found");
        }
        return bookingRepository.findAllByVetId(vetId, pageable);
    }

    private static void validateStatusTransition(TeleconsultationBooking.BookingStatus from, TeleconsultationBooking.BookingStatus to) {
        if (from == to) {
            return;
        }

        Set<TeleconsultationBooking.BookingStatus> allowed = switch (from) {
            case REQUESTED -> EnumSet.of(TeleconsultationBooking.BookingStatus.TRIAGED, TeleconsultationBooking.BookingStatus.SCHEDULED, TeleconsultationBooking.BookingStatus.CANCELLED);
            case TRIAGED -> EnumSet.of(TeleconsultationBooking.BookingStatus.SCHEDULED, TeleconsultationBooking.BookingStatus.CANCELLED);
            case SCHEDULED -> EnumSet.of(TeleconsultationBooking.BookingStatus.IN_PROGRESS, TeleconsultationBooking.BookingStatus.CANCELLED, TeleconsultationBooking.BookingStatus.NO_SHOW);
            case IN_PROGRESS -> EnumSet.of(TeleconsultationBooking.BookingStatus.COMPLETED, TeleconsultationBooking.BookingStatus.CANCELLED);
            case COMPLETED, CANCELLED, NO_SHOW -> EnumSet.noneOf(TeleconsultationBooking.BookingStatus.class);
        };

        if (!allowed.contains(to)) {
            throw new IllegalStateException("Invalid status transition: " + from + " -> " + to);
        }
    }
}
