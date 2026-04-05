package mx.edu.utez.Integradora5A_SIGRAD.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDACION", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, "CONFLICTO", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, "NO_AUTORIZADO", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, "ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "ERROR", ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> build(HttpStatus status, String tipo, String mensaje) {
        Map<String, String> body = new HashMap<>();
        body.put("mensaje", mensaje);
        body.put("tipo", tipo);
        return ResponseEntity.status(status).body(body);
    }
}

