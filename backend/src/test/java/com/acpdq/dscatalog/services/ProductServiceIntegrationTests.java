package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.ProductDTO;
import com.acpdq.dscatalog.patterns.Factory;
import com.acpdq.dscatalog.repositories.ProductRepository;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;

        productDTO = Factory.createProductDTO();
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> all = productService.findAll(pageable);

        assertFalse(all.isEmpty());
        assertEquals(10, all.getSize());
        assertEquals(countTotalProducts, all.getTotalElements());
    }

    @Test
    public void findAllShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(50, 10);

        Page<ProductDTO> all = productService.findAll(pageable);
        assertTrue(all.isEmpty());
    }

    @Test
    public void findByIdShouldReturnObjWhenIdExists() {
        ProductDTO byId = productService.findById(existingId);
        assertNotNull(byId);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnObjWhenIdExists() {
        ProductDTO dto = productService.update(existingId, productDTO);
        assertNotNull(dto);
    }

    @Test
    public void updateShouldThrowExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(existingId);
        assertEquals(countTotalProducts - 1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }
}
