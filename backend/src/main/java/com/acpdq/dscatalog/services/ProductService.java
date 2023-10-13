package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.ProductDTO;
import com.acpdq.dscatalog.entities.Category;
import com.acpdq.dscatalog.entities.Product;
import com.acpdq.dscatalog.repositories.CategoryRepository;
import com.acpdq.dscatalog.repositories.ProductRepository;
import com.acpdq.dscatalog.services.exceptions.DatabaseException;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> all = productRepository.findAll(pageable);
        return all.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Id = " + id + " not found"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        return new ProductDTO( productRepository.save(entity) );
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            return new ProductDTO( productRepository.save(entity) );
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id = " + id + " not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if ( !productRepository.existsById(id) )
            throw new ResourceNotFoundException("Id = " + id + " not found");

        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        dto.getCategories().forEach(catDto -> {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        });
    }
}
