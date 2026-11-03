package com.example.apijogos.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(
        RecursoNaoEncontradoException ex
    ) {
        return ResponseEntity.status(404)
            .body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(
        RegraNegocioException ex
    ) {
        return ResponseEntity.status(409)
            .body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(
        MethodArgumentNotValidException ex
    ) {

        String erros = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
            .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest()
            .body(Map.of("erro", erros));
    }
}