package com.acpdq.dscatalog.controllers;

import com.acpdq.dscatalog.dto.ProductDTO;
import com.acpdq.dscatalog.patterns.Factory;
import com.acpdq.dscatalog.services.ProductService;
import com.acpdq.dscatalog.services.exceptions.DatabaseException;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.createProductDTO();

        PageImpl<ProductDTO> page = new PageImpl<>(List.of(productDTO));

        when(productService.findAll(any())).thenReturn(page);
        when(productService.findById(existingId)).thenReturn(productDTO);
        when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(productService.insert(any())).thenReturn(productDTO);

        when(productService.update(eq(existingId), any())).thenReturn(productDTO);
        when(productService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        doThrow(DatabaseException.class).when(productService).delete(dependentId);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnObjWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnObjCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        MediaType applicationJson = MediaType.APPLICATION_JSON;

        mockMvc.perform(post("/products")
                        .content(jsonBody)
                        .contentType(applicationJson)
                        .accept(applicationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void updateShouldReturnObjWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        MediaType applicationJson = MediaType.APPLICATION_JSON;

        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(applicationJson)
                        .accept(applicationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        MediaType applicationJson = MediaType.APPLICATION_JSON;

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(applicationJson)
                        .accept(applicationJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
        mockMvc.perform(delete("/products/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }
}
