package com.testmian.quiz_app.service;

import com.testmian.quiz_app.entity.Category;
import com.testmian.quiz_app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all non-deleted categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(c -> c.getDeletedAt() == null)
                .toList();
    }

    // Get category by ID
    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null);
    }

    // Create new category
    public Category createCategory(Category category) {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new RuntimeException("Category with this name already exists.");
        }

        // Ensure timestamps are not null
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(null);
        category.setDeletedAt(null);

        return categoryRepository.save(category);
    }

    // Update existing category
    public Category updateCategory(Integer id, Category updatedCategory) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        existing.setCategoryName(updatedCategory.getCategoryName());
        existing.setDescription(updatedCategory.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(existing);
    }

    // Soft delete category
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    // Restore soft-deleted category
    public Category restoreCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        if (category.getDeletedAt() != null) {
            category.setDeletedAt(null);
            categoryRepository.save(category);
        }
        return category;
    }
}
