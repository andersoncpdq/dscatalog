package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.ProductDTO;
import com.acpdq.dscatalog.entities.Category;
import com.acpdq.dscatalog.entities.Product;
import com.acpdq.dscatalog.patterns.Factory;
import com.acpdq.dscatalog.repositories.CategoryRepository;
import com.acpdq.dscatalog.repositories.ProductRepository;
import com.acpdq.dscatalog.services.exceptions.DatabaseException;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    private Product product;
    private Category category;
    private ProductDTO productDTO;
    private PageImpl<Product> page;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 3L;

        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));

        when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        when(productRepository.save(product)).thenReturn(product);

        when(productRepository.existsById(existingId)).thenReturn(true);
        when(productRepository.existsById(nonExistingId)).thenReturn(false);
        when(productRepository.existsById(dependentId)).thenReturn(true);

        doNothing().when(productRepository).deleteById(existingId);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,1);
        Page<ProductDTO> all = productService.findAll(pageable);

        assertFalse(all.isEmpty());
        verify(productRepository).findAll(pageable);
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
    public void deleteShouldDoNothingWhenIdExists() {
        productService.delete(existingId);
        verify(productRepository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });
        verify(productRepository).deleteById(dependentId);
    }
}
