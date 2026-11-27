package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.service.exception.PetNotFoundException;
import org.springframework.samples.petclinic.service.exception.VisitConflictException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicServiceImplVisitValidationTests {

    private static final int PET_ID = 8;

    @Mock
    private PetRepository petRepository;
    @Mock
    private VetRepository vetRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private VisitRepository visitRepository;
    @Mock
    private SpecialtyRepository specialtyRepository;
    @Mock
    private PetTypeRepository petTypeRepository;

    private ClinicServiceImpl clinicService;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicServiceImpl(
            petRepository,
            vetRepository,
            ownerRepository,
            visitRepository,
            specialtyRepository,
            petTypeRepository);
    }

    @Test
    void saveVisitRejectsUnknownPet() {
        when(petRepository.findById(PET_ID)).thenReturn(null);
        Visit requestedVisit = visit(pet(PET_ID), LocalDate.of(2099, 12, 31), null);

        assertThatThrownBy(() -> clinicService.saveVisit(requestedVisit))
            .isInstanceOf(PetNotFoundException.class)
            .hasMessage("Pet with id 8 was not found");
        verify(visitRepository, never()).findByPetId(PET_ID);
        verify(visitRepository, never()).save(requestedVisit);
    }

    @Test
    void saveVisitRejectsAnotherVisitForPetOnSameDate() {
        LocalDate date = LocalDate.of(2099, 12, 31);
        Pet persistedPet = pet(PET_ID);
        Visit requestedVisit = visit(pet(PET_ID), date, null);
        Visit existingVisit = visit(persistedPet, date, 42);
        when(petRepository.findById(PET_ID)).thenReturn(persistedPet);
        when(visitRepository.findByPetId(PET_ID)).thenReturn(List.of(existingVisit));

        assertThatThrownBy(() -> clinicService.saveVisit(requestedVisit))
            .isInstanceOf(VisitConflictException.class)
            .hasMessageContaining("already scheduled");
        verify(visitRepository, never()).save(requestedVisit);
    }

    @Test
    void saveVisitAttachesExistingPetAndSavesWhenDateIsAvailable() {
        LocalDate date = LocalDate.of(2099, 12, 31);
        Pet persistedPet = pet(PET_ID);
        Visit requestedVisit = visit(pet(PET_ID), date, null);
        when(petRepository.findById(PET_ID)).thenReturn(persistedPet);
        when(visitRepository.findByPetId(PET_ID)).thenReturn(List.of());

        clinicService.saveVisit(requestedVisit);

        assertThat(requestedVisit.getPet()).isSameAs(persistedPet);
        verify(visitRepository).save(requestedVisit);
    }

    @Test
    void saveVisitAllowsSavingExistingVisitWithoutTreatingItAsDuplicate() {
        LocalDate date = LocalDate.of(2099, 12, 31);
        Pet persistedPet = pet(PET_ID);
        Visit requestedVisit = visit(pet(PET_ID), date, 42);
        Visit existingVisit = visit(persistedPet, date, 42);
        when(petRepository.findById(PET_ID)).thenReturn(persistedPet);
        when(visitRepository.findByPetId(PET_ID)).thenReturn(List.of(existingVisit));

        clinicService.saveVisit(requestedVisit);

        verify(visitRepository).save(requestedVisit);
    }

    private static Pet pet(int id) {
        Pet pet = new Pet();
        pet.setId(id);
        return pet;
    }

    private static Visit visit(Pet pet, LocalDate date, Integer id) {
        Visit visit = new Visit();
        visit.setPet(pet);
        visit.setDate(date);
        visit.setDescription("check-up");
        visit.setId(id);
        return visit;
    }
}
