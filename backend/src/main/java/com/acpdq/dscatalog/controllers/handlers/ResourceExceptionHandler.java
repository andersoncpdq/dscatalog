package com.acpdq.dscatalog.controllers.handlers;

import com.acpdq.dscatalog.dto.CustomErrorDTO;
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
        int status = HttpStatus.NOT_FOUND.value();
        CustomErrorDTO customErrorDTO = new CustomErrorDTO();
        customErrorDTO.setTimeStamp(Instant.now());
        customErrorDTO.setStatus(status);
        customErrorDTO.setError("Resource not found");
        customErrorDTO.setMsg(e.getMessage());
        customErrorDTO.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDTO);
    }
}
