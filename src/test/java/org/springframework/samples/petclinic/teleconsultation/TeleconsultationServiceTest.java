package org.springframework.samples.petclinic.teleconsultation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.PetRepository;

class TeleconsultationServiceTest {

    private final TeleconsultationRepository teleconsultationRepository = mock(TeleconsultationRepository.class);
    private final PetRepository petRepository = mock(PetRepository.class);
    private final TeleconsultationService service = new TeleconsultationService(teleconsultationRepository, petRepository);

    @Test
    void createEmergency_throwsWhenPetNotFound() {
        when(petRepository.findById(10)).thenReturn(Optional.empty());

        TeleconsultationCreateRequest req = new TeleconsultationCreateRequest(10, ContactMethod.VIDEO, true, null, null);
        assertThrows(TeleconsultationValidationException.class, () -> service.createEmergency(1, req));
    }

    @Test
    void createEmergency_throwsWhenPetNotOwnedByOwner() {
        Pet pet = new Pet();
        Owner owner = new Owner();
        owner.setId(99);
        pet.setOwner(owner);

        when(petRepository.findById(10)).thenReturn(Optional.of(pet));

        TeleconsultationCreateRequest req = new TeleconsultationCreateRequest(10, ContactMethod.PHONE, true, null, null);
        assertThrows(TeleconsultationValidationException.class, () -> service.createEmergency(1, req));
    }

    @Test
    void createEmergency_throwsOnOverlap() {
        Pet pet = new Pet();
        Owner owner = new Owner();
        owner.setId(1);
        pet.setOwner(owner);

        when(petRepository.findById(10)).thenReturn(Optional.of(pet));
        when(teleconsultationRepository.existsOverlappingForPet(eq(10), any(), any())).thenReturn(true);

        TeleconsultationCreateRequest req = new TeleconsultationCreateRequest(10, ContactMethod.VIDEO, true, null, null);
        assertThrows(TeleconsultationConflictException.class, () -> service.createEmergency(1, req));
    }

    @Test
    void getById_deniesOtherOwner() {
        Teleconsultation t = new Teleconsultation();
        t.setId(5);
        t.setOwnerId(2);
        when(teleconsultationRepository.findById(5)).thenReturn(Optional.of(t));

        assertThrows(TeleconsultationValidationException.class, () -> service.getById(1, 5));
    }

    @Test
    void list_enforcesSizeUpperBound() {
        assertThrows(TeleconsultationValidationException.class,
                () -> service.list(1, Optional.empty(), Optional.empty(), 0, 500));
    }

    @Test
    void list_byOwner_ok() {
        Teleconsultation t = new Teleconsultation();
        t.setId(1);
        t.setOwnerId(1);
        t.setPetId(10);
        t.setContactMethod(ContactMethod.PHONE);
        t.setStatus(TeleconsultationStatus.REQUESTED);
        t.setStartTs(OffsetDateTime.now().plusMinutes(10));
        t.setEndTs(OffsetDateTime.now().plusMinutes(40));

        when(teleconsultationRepository.findByOwnerId(eq(1), any())).thenReturn(new PageImpl<>(java.util.List.of(t)));

        Page<TeleconsultationResponse> page = service.list(1, Optional.of(1), Optional.empty(), 0, 20);
        assertEquals(1, page.getTotalElements());
    }
}
