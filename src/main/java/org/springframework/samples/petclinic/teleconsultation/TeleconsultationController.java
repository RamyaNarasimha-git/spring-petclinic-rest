package org.springframework.samples.petclinic.teleconsultation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teleconsultations")
@Tag(name = "Teleconsultations", description = "Emergency tele-consultation endpoints")
public class TeleconsultationController {

    private final TeleconsultationService teleconsultationService;

    public TeleconsultationController(TeleconsultationService teleconsultationService) {
        this.teleconsultationService = teleconsultationService;
    }

    @PostMapping(value = "/emergency", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an emergency teleconsultation")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = TeleconsultationResponse.class)))
    public TeleconsultationResponse createEmergency(
            @RequestHeader("X-Owner-Id") Integer ownerId,
            @Valid @RequestBody TeleconsultationCreateRequest request) {
        return teleconsultationService.createEmergency(ownerId, request);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get teleconsultation by id")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = TeleconsultationResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found")
    public TeleconsultationResponse getById(
            @RequestHeader("X-Owner-Id") Integer ownerId,
            @PathVariable Integer id) {
        return teleconsultationService.getById(ownerId, id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List teleconsultations for owner or pet")
    public Page<TeleconsultationResponse> list(
            @RequestHeader("X-Owner-Id") Integer ownerId,
            @RequestParam Optional<Integer> ownerIdFilter,
            @RequestParam Optional<Integer> petId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return teleconsultationService.list(ownerId, ownerIdFilter, petId, page, size);
    }
}
