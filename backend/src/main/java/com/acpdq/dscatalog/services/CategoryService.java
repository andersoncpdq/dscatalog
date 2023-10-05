package com.acpdq.dscatalog.services;

import com.acpdq.dscatalog.dto.CategoryDTO;
import com.acpdq.dscatalog.entities.Category;
import com.acpdq.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> all = categoryRepository.findAll();
        return all.stream().map(CategoryDTO::new).toList();
    }
}
