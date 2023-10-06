package com.acpdq.dscatalog.controllers.handlers;

import com.acpdq.dscatalog.dto.CustomErrorDTO;
import com.acpdq.dscatalog.services.exceptions.DatabaseException;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorDTO> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        CustomErrorDTO customErrorDTO = new CustomErrorDTO();
        customErrorDTO.setTimeStamp(Instant.now());
        customErrorDTO.setStatus(status.value());
        customErrorDTO.setError("Resource not found");
        customErrorDTO.setMsg(e.getMessage());
        customErrorDTO.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDTO);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomErrorDTO> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomErrorDTO customErrorDTO = new CustomErrorDTO();
        customErrorDTO.setTimeStamp(Instant.now());
        customErrorDTO.setStatus(status.value());
        customErrorDTO.setError("Database exception");
        customErrorDTO.setMsg(e.getMessage());
        customErrorDTO.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDTO);
    }
}
