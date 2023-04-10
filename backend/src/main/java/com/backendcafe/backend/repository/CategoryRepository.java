package com.backendcafe.backend.repository;

import com.backendcafe.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> getAllCategory();
    List<Category> findByName(@Param("name") String name);
}
