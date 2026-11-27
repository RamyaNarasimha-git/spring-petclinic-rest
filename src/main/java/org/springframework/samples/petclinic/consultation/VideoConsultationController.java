package org.springframework.samples.petclinic.consultation;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.BookingConfirmationResponse;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.CreateBookingRequest;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.JoinTokenResponse;

@RestController
@RequestMapping("/api/video-consultations")
public class VideoConsultationController {

    private final VideoConsultationService service;

    public VideoConsultationController(VideoConsultationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingConfirmationResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(service.createEmergencyBooking(request));
    }

    @PostMapping("/{appointmentId}/join-token")
    public ResponseEntity<JoinTokenResponse> joinToken(@PathVariable String appointmentId,
            @RequestHeader("X-User-Id") Integer userId, @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(service.generateJoinToken(appointmentId, userId, role));
    }
}
