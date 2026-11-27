package org.springframework.samples.petclinic.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.TeleconsultationBookingMapper;
import org.springframework.samples.petclinic.model.TeleconsultationBooking;
import org.springframework.samples.petclinic.rest.dto.AssignVetRequest;
import org.springframework.samples.petclinic.rest.dto.TeleconsultationBookingRequest;
import org.springframework.samples.petclinic.rest.dto.TeleconsultationBookingResponse;
import org.springframework.samples.petclinic.rest.dto.UpdateBookingStatusRequest;
import org.springframework.samples.petclinic.service.TeleconsultationBookingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/teleconsultations")
@Tag(name = "Teleconsultations")
@Validated
public class TeleconsultationBookingController {

    private final TeleconsultationBookingService service;
    private final TeleconsultationBookingMapper mapper;

    public TeleconsultationBookingController(TeleconsultationBookingService service, TeleconsultationBookingMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a teleconsultation booking")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Validation Error", content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PostMapping("/owners/{ownerId}/bookings")
    public ResponseEntity<TeleconsultationBookingResponse> create(
        @PathVariable @Positive int ownerId,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
        @Valid @RequestBody TeleconsultationBookingRequest request
    ) {
        if (request.ownerId() == null || request.ownerId() != ownerId) {
            throw new IllegalArgumentException("ownerId in path must match request.ownerId");
        }
        TeleconsultationBooking saved = service.createBooking(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    @Operation(summary = "Get booking by id (owner scoped)")
    @GetMapping("/{bookingId}")
    public TeleconsultationBookingResponse getById(
        @PathVariable @Positive int bookingId,
        @RequestParam("ownerId") @Positive int ownerId
    ) {
        return mapper.toResponse(service.getBookingForOwner(bookingId, ownerId));
    }

    @Operation(summary = "List owner bookings")
    @GetMapping("/owners/{ownerId}/bookings")
    public Page<TeleconsultationBookingResponse> listOwner(
        @PathVariable @Positive int ownerId,
        @RequestParam(value = "status", required = false) TeleconsultationBooking.BookingStatus status,
        @RequestParam(value = "priority", required = false) TeleconsultationBooking.Priority priority,
        @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
        @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
        Pageable pageable
    ) {
        return service.listOwnerBookings(ownerId, status, priority, from, to, pageable).map(mapper::toResponse);
    }

    @Operation(summary = "List bookings for a pet (owner scoped)")
    @GetMapping("/owners/{ownerId}/pets/{petId}/bookings")
    public Page<TeleconsultationBookingResponse> listPet(
        @PathVariable @Positive int ownerId,
        @PathVariable @Positive int petId,
        Pageable pageable
    ) {
        return service.listPetBookings(ownerId, petId, pageable).map(mapper::toResponse);
    }

    @Operation(summary = "Update booking status (owner scoped)")
    @PatchMapping("/{bookingId}/status")
    public TeleconsultationBookingResponse updateStatus(
        @PathVariable @Positive int bookingId,
        @RequestParam("ownerId") @Positive int ownerId,
        @Valid @RequestBody UpdateBookingStatusRequest request
    ) {
        return mapper.toResponse(service.updateStatus(bookingId, ownerId, request));
    }

    @Operation(summary = "Assign vet and schedule a booking")
    @PatchMapping("/{bookingId}/assign-vet")
    public TeleconsultationBookingResponse assignVet(
        @PathVariable @Positive int bookingId,
        @Valid @RequestBody AssignVetRequest request
    ) {
        return mapper.toResponse(service.assignVet(bookingId, request));
    }

    @Operation(summary = "List bookings for a vet")
    @GetMapping("/vets/{vetId}/bookings")
    public Page<TeleconsultationBookingResponse> listVet(
        @PathVariable @Positive int vetId,
        Pageable pageable
    ) {
        return service.listVetBookings(vetId, pageable).map(mapper::toResponse);
    }
}
