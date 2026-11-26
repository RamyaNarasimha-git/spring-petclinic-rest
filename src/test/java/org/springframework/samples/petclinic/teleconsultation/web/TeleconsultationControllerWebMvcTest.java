package org.springframework.samples.petclinic.teleconsultation.web;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.teleconsultation.*;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TeleconsultationController.class)
class TeleconsultationControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TeleconsultationService teleconsultationService;

    @Test
    void createEmergency_returns201() throws Exception {
        TeleconsultationResponse resp = new TeleconsultationResponse(
                1, 1, 10, null, ContactMethod.VIDEO, TeleconsultationStatus.REQUESTED,
                OffsetDateTime.parse("2030-01-01T10:00:00Z"), OffsetDateTime.parse("2030-01-01T10:30:00Z"),
                null, null, OffsetDateTime.parse("2030-01-01T09:59:00Z"), OffsetDateTime.parse("2030-01-01T09:59:00Z"));

        when(teleconsultationService.createEmergency(eq(1), any())).thenReturn(resp);

        TeleconsultationCreateRequest req = new TeleconsultationCreateRequest(10, ContactMethod.VIDEO, true, null, null);

        mockMvc.perform(post("/api/teleconsultations/emergency")
                        .header("X-Owner-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.petId").value(10));
    }

    @Test
    void createEmergency_validationErrors_return400ProblemDetail() throws Exception {
        String body = "{}";
        var actions = mockMvc.perform(post("/api/teleconsultations/emergency")
                        .header("X-Owner-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        ProblemDetailAssertions.assertProblemDetail(actions);
    }

    @Test
    void getById_mapsValidationExceptionTo400() throws Exception {
        // Note: OpenAPI states 404, but current handler maps domain validation to 400.
        when(teleconsultationService.getById(1, 99)).thenThrow(new TeleconsultationValidationException("Teleconsultation not found"));

        var actions = mockMvc.perform(get("/api/teleconsultations/99")
                        .header("X-Owner-Id", "1"))
                .andExpect(status().isBadRequest());

        ProblemDetailAssertions.assertProblemDetail(actions);
    }

    @Test
    void list_sizeTooLarge_returns400() throws Exception {
        when(teleconsultationService.list(eq(1), any(), any(), anyInt(), anyInt()))
                .thenThrow(new TeleconsultationValidationException("size must be between 1 and 100"));

        var actions = mockMvc.perform(get("/api/teleconsultations")
                        .header("X-Owner-Id", "1")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        ProblemDetailAssertions.assertProblemDetail(actions);
    }

    @Test
    void list_success_returnsPage() throws Exception {
        TeleconsultationResponse resp = new TeleconsultationResponse(
                1, 1, 10, null, ContactMethod.PHONE, TeleconsultationStatus.SCHEDULED,
                OffsetDateTime.parse("2030-01-01T10:00:00Z"), OffsetDateTime.parse("2030-01-01T10:30:00Z"),
                null, null, OffsetDateTime.parse("2030-01-01T09:59:00Z"), OffsetDateTime.parse("2030-01-01T09:59:00Z"));

        when(teleconsultationService.list(eq(1), any(), any(), eq(0), eq(20)))
                .thenReturn(new PageImpl<>(java.util.List.of(resp)));

        mockMvc.perform(get("/api/teleconsultations")
                        .header("X-Owner-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}
