package org.springframework.samples.petclinic.teleconsultation;

import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeleconsultationRepository extends JpaRepository<Teleconsultation, Integer> {

    @Query("select t from Teleconsultation t where t.ownerId = :ownerId")
    Page<Teleconsultation> findByOwnerId(@Param("ownerId") Integer ownerId, Pageable pageable);

    @Query("select t from Teleconsultation t where t.petId = :petId")
    Page<Teleconsultation> findByPetId(@Param("petId") Integer petId, Pageable pageable);

    @Query("select case when count(t) > 0 then true else false end " +
            "from Teleconsultation t " +
            "where t.petId = :petId " +
            "and t.startTs < :endTs " +
            "and t.endTs > :startTs")
    boolean existsOverlappingForPet(@Param("petId") Integer petId,
                                   @Param("startTs") OffsetDateTime startTs,
                                   @Param("endTs") OffsetDateTime endTs);

    @Query("select case when count(t) > 0 then true else false end " +
            "from Teleconsultation t " +
            "where t.vetId = :vetId " +
            "and t.startTs < :endTs " +
            "and t.endTs > :startTs")
    boolean existsOverlappingForVet(@Param("vetId") Integer vetId,
                                   @Param("startTs") OffsetDateTime startTs,
                                   @Param("endTs") OffsetDateTime endTs);
}
