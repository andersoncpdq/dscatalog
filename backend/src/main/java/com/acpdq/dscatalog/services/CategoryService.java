package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.CategoryDTO;
import com.acpdq.dscatalog.entities.Category;
import com.acpdq.dscatalog.repositories.CategoryRepository;
import com.acpdq.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category byId = categoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new CategoryDTO(byId);
    }
}
