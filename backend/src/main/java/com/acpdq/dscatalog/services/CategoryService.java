package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.CategoryDTO;
import com.acpdq.dscatalog.entities.Category;
import com.acpdq.dscatalog.repositories.CategoryRepository;
import com.acpdq.dscatalog.services.exceptions.DatabaseException;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(PageRequest pageRequest) {
        Page<Category> all = categoryRepository.findAll(pageRequest);
        return all.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category byId = categoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Id = " + id + " not found"));
        return new CategoryDTO(byId);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        return new CategoryDTO( categoryRepository.save(entity) );
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = categoryRepository.getReferenceById(id);
            entity.setName(dto.getName());
            return new CategoryDTO( categoryRepository.save(entity) );
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id = " + id + " not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if ( !categoryRepository.existsById(id) )
            throw new ResourceNotFoundException("Id = " + id + " not found");

        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }
}
