package org.springframework.samples.petclinic.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.TeleconsultationBookingMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.advice.TeleconsultationExceptionHandler;
import org.springframework.samples.petclinic.rest.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.service.TeleconsultationBookingService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TeleconsultationBookingController.class)
@Import({TeleconsultationBookingMapper.class, TeleconsultationExceptionHandler.class})
class TeleconsultationBookingControllerWebMvcTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TeleconsultationBookingService service;

    private static TeleconsultationBooking booking(int id, int ownerId) {
        Owner owner = new Owner();
        owner.setId(ownerId);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        Pet pet = new Pet();
        pet.setId(7);
        pet.setName("Leo");
        pet.setOwner(owner);

        Vet vet = new Vet();
        vet.setId(3);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        TeleconsultationBooking b = new TeleconsultationBooking();
        b.setOwner(owner);
        b.setPet(pet);
        b.setVet(vet);
        b.setPriority(TeleconsultationBooking.Priority.CRITICAL);
        b.setStatus(TeleconsultationBooking.BookingStatus.REQUESTED);
        b.setChannel(TeleconsultationBooking.ConsultationChannel.VIDEO);
        b.setSymptoms("vomiting");
        b.setContactEmail("a@b.com");
        b.setContactPhone("+1 555 000 0000");
        b.setRequestedAt(OffsetDateTime.parse("2026-01-01T10:00:00Z"));

        // set id via reflection-free trick: leave null; mapper handles null id. For controller tests, id isn't required in body assertions.
        return b;
    }

    @Test
    void create_returns201() throws Exception {
        TeleconsultationBooking saved = booking(1, 1);
        when(service.createBooking(any(), any())).thenReturn(saved);

        String body = "{" +
            "\"ownerId\":1," +
            "\"petId\":7," +
            "\"channel\":\"VIDEO\"," +
            "\"symptoms\":\"vomiting\"," +
            "\"contactEmail\":\"a@b.com\"," +
            "\"contactPhone\":\"+1 555 000 0000\"," +
            "\"emergency\":true" +
            "}";

        mvc.perform(post("/api/teleconsultations/owners/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", "abc")
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.priority").value("CRITICAL"))
            .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    void create_ownerIdMismatch_returns400() throws Exception {
        String body = "{" +
            "\"ownerId\":2," +
            "\"petId\":7," +
            "\"channel\":\"VIDEO\"," +
            "\"symptoms\":\"vomiting\"," +
            "\"emergency\":true" +
            "}";

        mvc.perform(post("/api/teleconsultations/owners/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Invalid Argument"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getBookingForOwner(eq(10), eq(1))).thenThrow(new ResourceNotFoundException("Booking not found"));

        mvc.perform(get("/api/teleconsultations/10")
                .param("ownerId", "1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void updateStatus_versionConflict_returns409() throws Exception {
        when(service.updateStatus(eq(10), eq(1), any())).thenThrow(new OptimisticLockException("Version conflict"));

        String body = "{" +
            "\"status\":\"CANCELLED\"," +
            "\"cancellationReason\":\"changed mind\"," +
            "\"version\":1" +
            "}";

        mvc.perform(patch("/api/teleconsultations/10/status")
                .param("ownerId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Concurrent Modification"));
    }

    @Test
    void listOwner_returnsPage() throws Exception {
        TeleconsultationBooking saved = booking(1, 1);
        when(service.listOwnerBookings(eq(1), any(), any(), any(), any(), any())).thenReturn(
            new PageImpl<>(List.of(saved), PageRequest.of(0, 20), 1)
        );

        mvc.perform(get("/api/teleconsultations/owners/1/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
}
