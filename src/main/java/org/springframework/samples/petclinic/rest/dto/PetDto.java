package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A pet.
 */
@Schema(name = "Pet", description = "A pet.")
@JsonTypeName("Pet")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-22T18:53:39.218600500+05:30[Asia/Calcutta]", comments = "Generator version: 7.26.0")
public record PetDto(
    @NotNull
    @Size(min = 1, max = 30)
    @Schema(name = "name", example = "Leo", description = "The name of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name,

    @PetAgeValidation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    @Valid
    @Schema(name = "birthDate", example = "2010-09-07", description = "The date of birth of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("birthDate")
    LocalDate birthDate,

    @NotNull
    @Valid
    @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    PetTypeDto type,

    @Min(0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    @Min(0)
    @Schema(name = "ownerId", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet's owner.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("ownerId")
    @Nullable Integer ownerId,

    @Schema(name = "visits", accessMode = Schema.AccessMode.READ_ONLY, description = "Vet visit bookings for this pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("visits")
    List<@Valid VisitDto> visits
) {

    @JsonCreator
    public PetDto {
        if (visits == null) {
            visits = new ArrayList<>();
        }
    }

    public PetDto(String name, LocalDate birthDate, PetTypeDto type, Integer id, List<@Valid VisitDto> visits) {
        this(name, birthDate, type, id, null, visits);
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public PetTypeDto getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public @Nullable Integer getOwnerId() {
        return ownerId;
    }

    public List<@Valid VisitDto> getVisits() {
        return visits;
    }

    public PetDto name(String name) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto birthDate(LocalDate birthDate) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto type(PetTypeDto type) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto id(Integer id) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto ownerId(@Nullable Integer ownerId) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto visits(List<@Valid VisitDto> visits) {
        return new PetDto(name, birthDate, type, id, ownerId, visits);
    }

    public PetDto addVisitsItem(VisitDto visitsItem) {
        List<VisitDto> updatedVisits = visits == null ? new ArrayList<>() : new ArrayList<>(visits);
        updatedVisits.add(visitsItem);
        return visits(updatedVisits);
    }
}
