package org.springframework.samples.petclinic.consultation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.BookingConfirmationResponse;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.JoinTokenResponse;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(VideoConsultationController.class)
@WithMockUser
class VideoConsultationControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    VideoConsultationService service;

    @Test
    void shouldCreateBooking() throws Exception {
        when(service.createEmergencyBooking(any())).thenReturn(
                new BookingConfirmationResponse("a1", 1, 1, new VideoConsultationDTOs.AssignedVeterinarian(2), 10,
                        "CONFIRMED"));

        String body = "{" +
                "\"petId\":1," +
                "\"ownerId\":1," +
                "\"severity\":\"SEVERE\"," +
                "\"startTime\":\"" + LocalDateTime.now().plusHours(1) + "\"," +
                "\"endTime\":\"" + LocalDateTime.now().plusHours(2) + "\"" +
                "}";

        mockMvc.perform(post("/api/video-consultations")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointmentId").value("a1"));
    }

    @Test
    void shouldGenerateJoinToken() throws Exception {
        when(service.generateJoinToken(eq("a1"), eq(1), eq("OWNER")))
                .thenReturn(new JoinTokenResponse("a1", "token", 300));

        mockMvc.perform(post("/api/video-consultations/a1/join-token")
                .with(csrf())
                .header("X-User-Id", 1)
                .header("X-User-Role", "OWNER"))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"))
            .andExpect(jsonPath("$.token").value("token"));
    }
}
