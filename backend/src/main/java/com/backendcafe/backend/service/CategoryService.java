package com.backendcafe.backend.service;

import com.backendcafe.backend.config.JwtFilter;
import com.backendcafe.backend.config.JwtUtil;
import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.entity.Category;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.repository.CategoryRepository;
import com.backendcafe.backend.untils.CafeUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final JwtFilter jwtFilter;
    private  final JwtUtil jwtUtil;

    public CategoryService(CategoryRepository categoryRepository, JwtFilter jwtFilter, JwtUtil jwtUtil) {
        this.categoryRepository = categoryRepository;
        this.jwtFilter = jwtFilter;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<JsonModel> addNewCategory(Map<String, String> body) throws JSONException {
        log.info("Service Add Category {}");

        try {
            if (jwtFilter.isAdmin()) {
                log.info("Is Admin");
                if (validateCategoryMap(body, false)) {
                    List<Category> c = categoryRepository.findByName(body.get("name"));
                    if (!c.isEmpty() && c.size() > 0) {
                        return CafeUtils.message("Name Category Have Already", HttpStatus.OK);
                    }
                    categoryRepository.save(getCategoryFromMap(body, false));
                    return CafeUtils.message("Category Add Success", HttpStatus.OK);

                }

            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    private boolean validateCategoryMap(Map<String, String> body, boolean validateId) {
        if (body.containsKey("name")) {
            if (validateId && body.containsKey("id")) {
                return true;
            } else if (!validateId) {
                return true;
            }

        }
        return false;
    }

    private Category getCategoryFromMap(Map<String, String> body, Boolean isAdd) {
        Category category = new Category();
        if (isAdd) {
            category.setId(Integer.parseInt(body.get("id")));
        }
        category.setName(body.get("name"));
        return category;
    }

    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Filter : True");
                return new ResponseEntity<List<Category>>(categoryRepository.getAllCategory(), HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<JsonModel> updateCategory(Map<String, String> body) throws JSONException {
        log.info("update");
        try {
            if (jwtFilter.isAdmin()) {
                log.info("isAdmin");
                if (validateCategoryMap(body, true)) {
                    Optional optional = categoryRepository.findById(Integer.parseInt(body.get("id")));
                    if (!optional.isEmpty()) {
                        categoryRepository.save(getCategoryFromMap(body, true));
                        return CafeUtils.message("UPDATE CATEGORY SUCCESS", HttpStatus.OK);
                    } else {
                        return CafeUtils.message("CAFE IS DOES NOT EXIST", HttpStatus.OK);
                    }
                }
                return CafeUtils.message(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();

    }
}
