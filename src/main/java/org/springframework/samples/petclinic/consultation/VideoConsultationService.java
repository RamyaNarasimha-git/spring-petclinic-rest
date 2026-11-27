package org.springframework.samples.petclinic.consultation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.AssignedVeterinarian;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.BookingConfirmationResponse;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.CreateBookingRequest;
import org.springframework.samples.petclinic.consultation.VideoConsultationDTOs.JoinTokenResponse;

@Service
public class VideoConsultationService {

    private final VideoConsultationBookingRepository bookingRepository;
    private final VideoConsultationSlotRepository slotRepository;
    private final JoinTokenService joinTokenService;

    public VideoConsultationService(VideoConsultationBookingRepository bookingRepository,
            VideoConsultationSlotRepository slotRepository, JoinTokenService joinTokenService) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.joinTokenService = joinTokenService;
    }

    @Transactional
    public BookingConfirmationResponse createEmergencyBooking(CreateBookingRequest request) {
        validateRequestWindow(request.startTime(), request.endTime());

        VideoConsultationBooking.Severity severity = parseSeverity(request.severity());
        String externalId = UUID.randomUUID().toString();

        VideoConsultationBooking booking = new VideoConsultationBooking(externalId, request.petId(), request.ownerId(),
                severity, request.startTime(), request.endTime());

        Integer assignedVetId = assignFirstAvailableVet(request.startTime(), request.endTime());
        booking.setVetId(assignedVetId);

        reserveCapacity(assignedVetId, request.startTime(), request.endTime());

        booking.setStatus(VideoConsultationBooking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return new BookingConfirmationResponse(booking.getExternalId(), booking.getPetId(), booking.getOwnerId(),
                new AssignedVeterinarian(assignedVetId), booking.getTriagePriority(), booking.getStatus().name());
    }

    public JoinTokenResponse generateJoinToken(String appointmentId, Integer userId, String role) {
        VideoConsultationBooking booking = bookingRepository.findByExternalId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Video consultation not found"));

        if (!isAuthorized(booking, userId, role)) {
            throw new UnauthorizedException("Not allowed to access this consultation");
        }

        String token = joinTokenService.createJoinToken(appointmentId, userId, role);
        return new JoinTokenResponse(appointmentId, token, joinTokenService.getExpiresInSeconds());
    }

    private boolean isAuthorized(VideoConsultationBooking booking, Integer userId, String role) {
        if (role == null) {
            return false;
        }
        return switch (role) {
        case "OWNER" -> booking.getOwnerId().equals(userId);
        case "VET" -> booking.getVetId() != null && booking.getVetId().equals(userId);
        case "STAFF" -> true;
        default -> false;
        };
    }

    private void reserveCapacity(Integer vetId, LocalDateTime start, LocalDateTime end) {
        VideoConsultationSlot slot = slotRepository.findByVetIdAndStartTimeAndEndTime(vetId, start, end)
                .orElseGet(() -> slotRepository.save(new VideoConsultationSlot(vetId, start, end, 1)));

        if (!slot.hasCapacity()) {
            throw new CapacityExceededException("No remaining capacity for selected time window");
        }
        slot.incrementBooked();
        slotRepository.save(slot);
    }

    private Integer assignFirstAvailableVet(LocalDateTime start, LocalDateTime end) {
        // Deterministic placeholder: in real app we'd query vets table.
        List<Integer> candidateVets = List.of(1, 2, 3);

        return candidateVets.stream()
                .filter(vetId -> bookingRepository.findOverlappingConfirmedBookings(vetId, start, end).isEmpty())
                .findFirst()
                .orElseThrow(() -> new NoVetAvailableException("No veterinarian available for requested time window"));
    }

    private void validateRequestWindow(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new InvalidBookingRequestException("Invalid time window");
        }
        if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new InvalidBookingRequestException("Start time must not be in the past");
        }
    }

    private VideoConsultationBooking.Severity parseSeverity(String severity) {
        try {
            return VideoConsultationBooking.Severity.valueOf(severity);
        }
        catch (Exception ex) {
            throw new InvalidBookingRequestException("Unsupported severity: " + severity);
        }
    }
}
