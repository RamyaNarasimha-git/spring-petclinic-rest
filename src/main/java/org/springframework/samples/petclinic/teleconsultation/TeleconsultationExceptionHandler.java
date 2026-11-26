package org.springframework.samples.petclinic.teleconsultation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = TeleconsultationController.class)
public class TeleconsultationExceptionHandler {

    @ExceptionHandler(TeleconsultationConflictException.class)
    public ProblemDetail handleConflict(TeleconsultationConflictException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Teleconsultation conflict");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("https://example.com/problems/teleconsultation-conflict"));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("path", request.getRequestURI());
        return pd;
    }

    @ExceptionHandler(TeleconsultationValidationException.class)
    public ProblemDetail handleValidation(TeleconsultationValidationException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Teleconsultation validation failed");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("https://example.com/problems/teleconsultation-validation"));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("path", request.getRequestURI());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Request validation failed");
        pd.setDetail("One or more fields are invalid");
        pd.setType(URI.create("https://example.com/problems/request-validation"));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setProperty("path", request.getRequestURI());
        pd.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage()))
                .toList());
        return pd;
    }
}
