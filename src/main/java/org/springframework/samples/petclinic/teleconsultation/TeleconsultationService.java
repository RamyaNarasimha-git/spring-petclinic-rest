package org.springframework.samples.petclinic.teleconsultation;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeleconsultationService {

    private static final int MAX_PAGE_SIZE = 100;

    private final TeleconsultationRepository teleconsultationRepository;
    private final PetRepository petRepository;

    public TeleconsultationService(TeleconsultationRepository teleconsultationRepository, PetRepository petRepository) {
        this.teleconsultationRepository = teleconsultationRepository;
        this.petRepository = petRepository;
    }

    @Transactional
    public TeleconsultationResponse createEmergency(Integer authenticatedOwnerId, TeleconsultationCreateRequest request) {
        if (authenticatedOwnerId == null) {
            throw new TeleconsultationValidationException("Missing owner context");
        }
        if (request == null) {
            throw new TeleconsultationValidationException("Request must not be null");
        }
        if (request.asap() == null) {
            throw new TeleconsultationValidationException("asap must be provided");
        }
        if (!request.asap() && request.requestedStart() == null) {
            throw new TeleconsultationValidationException("requestedStart must be provided when asap=false");
        }

        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new TeleconsultationValidationException("Pet not found"));
        if (pet.getOwner() == null || pet.getOwner().getId() == null || !pet.getOwner().getId().equals(authenticatedOwnerId)) {
            throw new TeleconsultationValidationException("Pet does not belong to owner");
        }

        OffsetDateTime start = request.asap() ? OffsetDateTime.now() : request.requestedStart();
        if (start.isBefore(OffsetDateTime.now().minusSeconds(1))) {
            throw new TeleconsultationValidationException("Start time must be in the future");
        }
        OffsetDateTime end = start.plusMinutes(30);

        boolean overlaps = teleconsultationRepository.existsOverlappingForPet(request.petId(), start, end);
        if (overlaps) {
            throw new TeleconsultationConflictException("Pet already has an overlapping teleconsultation");
        }

        Teleconsultation t = new Teleconsultation();
        t.setOwnerId(authenticatedOwnerId);
        t.setPetId(request.petId());
        t.setContactMethod(request.contactMethod());
        t.setStatus(request.asap() ? TeleconsultationStatus.REQUESTED : TeleconsultationStatus.SCHEDULED);
        t.setStartTs(start);
        t.setEndTs(end);
        t.setNotes(request.notes());

        Teleconsultation saved = teleconsultationRepository.save(t);
        return TeleconsultationResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public TeleconsultationResponse getById(Integer authenticatedOwnerId, Integer id) {
        Teleconsultation t = teleconsultationRepository.findById(id)
                .orElseThrow(() -> new TeleconsultationValidationException("Teleconsultation not found"));
        if (authenticatedOwnerId == null || !authenticatedOwnerId.equals(t.getOwnerId())) {
            throw new TeleconsultationValidationException("Not allowed");
        }
        return TeleconsultationResponse.from(t);
    }

    @Transactional(readOnly = true)
    public Page<TeleconsultationResponse> list(Integer authenticatedOwnerId,
                                              Optional<Integer> ownerId,
                                              Optional<Integer> petId,
                                              int page,
                                              int size) {
        if (authenticatedOwnerId == null) {
            throw new TeleconsultationValidationException("Missing owner context");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new TeleconsultationValidationException("size must be between 1 and " + MAX_PAGE_SIZE);
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTs"));

        if (petId.isPresent()) {
            Pet pet = petRepository.findById(petId.get())
                    .orElseThrow(() -> new TeleconsultationValidationException("Pet not found"));
            if (pet.getOwner() == null || pet.getOwner().getId() == null || !pet.getOwner().getId().equals(authenticatedOwnerId)) {
                throw new TeleconsultationValidationException("Pet does not belong to owner");
            }
            return teleconsultationRepository.findByPetId(petId.get(), pageable).map(TeleconsultationResponse::from);
        }

        Integer effectiveOwnerId = ownerId.orElse(authenticatedOwnerId);
        if (!effectiveOwnerId.equals(authenticatedOwnerId)) {
            throw new TeleconsultationValidationException("Not allowed");
        }
        return teleconsultationRepository.findByOwnerId(effectiveOwnerId, pageable).map(TeleconsultationResponse::from);
    }
}
