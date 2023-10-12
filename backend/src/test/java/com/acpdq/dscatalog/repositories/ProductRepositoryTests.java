package com.acpdq.dscatalog.repositories;

import com.acpdq.dscatalog.entities.Product;
import com.acpdq.dscatalog.patterns.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long NonExistingId;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        NonExistingId = 999L;
    }

    @Test
    public void findByIdShouldReturnObjWhenIdExists() {

        Optional<Product> byId = productRepository.findById(existingId);

        assertTrue(byId.isPresent());
    }

    @Test
    public void findByIdShouldNoReturnObjWhenIdDoesNotExists() {

        Optional<Product> byId = productRepository.findById(NonExistingId);

        assertTrue(byId.isEmpty());
    }

    @Test
    public void saveShouldPersistWhenIdIsNull() {

        Product product = Factory.createProduct();
        product.setId(null);

        Product entity = productRepository.save(product);
        Optional<Product> byId = productRepository.findById(entity.getId());

        assertTrue(byId.isPresent());
        assertNotNull(entity.getId());
        assertSame(byId.get(), entity);
    }

    @Test
    public void deleteByIdShouldDeleteObjWhenIdExists() {

        productRepository.deleteById(existingId);
        Optional<Product> byId = productRepository.findById(existingId);

        assertTrue(byId.isEmpty());
    }
}
