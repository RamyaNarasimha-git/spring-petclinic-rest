package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.Collection;

/**
 * Map Visit & VisitDto using mapstruct
 */
@Mapper(uses = PetMapper.class)
public interface VisitMapper {
    default Visit toVisit(VisitDto visitDto) {
        if (visitDto == null) {
            return null;
        }
        Visit visit = new Visit();
        visit.setDate(visitDto.date());
        visit.setDescription(visitDto.description());
        visit.setId(visitDto.id());
        Pet pet = new Pet();
        pet.setId(visitDto.petId());
        visit.setPet(pet);
        return visit;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    Visit toVisit(VisitFieldsDto visitFieldsDto);

    default VisitDto toVisitDto(Visit visit) {
        if (visit == null) {
            return null;
        }
        return new VisitDto(
            visit.getDate(),
            visit.getDescription(),
            visit.getId(),
            visit.getPet() == null ? null : visit.getPet().getId());
    }

    Collection<VisitDto> toVisitsDto(Collection<Visit> visits);

}
