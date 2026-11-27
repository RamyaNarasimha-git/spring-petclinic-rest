package org.springframework.samples.petclinic.consultation;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VideoConsultationExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Not Found");
        pd.setType(URI.create("https://petclinic/api/errors/not-found"));
        return pd;
    }

    @ExceptionHandler(UnauthorizedException.class)
    ProblemDetail forbidden(UnauthorizedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setTitle("Forbidden");
        pd.setType(URI.create("https://petclinic/api/errors/forbidden"));
        return pd;
    }

    @ExceptionHandler({ InvalidBookingRequestException.class, MethodArgumentNotValidException.class })
    ProblemDetail badRequest(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        pd.setType(URI.create("https://petclinic/api/errors/bad-request"));
        return pd;
    }

    @ExceptionHandler({ NoVetAvailableException.class, CapacityExceededException.class })
    ProblemDetail conflict(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflict");
        pd.setType(URI.create("https://petclinic/api/errors/conflict"));
        return pd;
    }
}
