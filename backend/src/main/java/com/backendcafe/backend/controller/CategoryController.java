package com.backendcafe.backend.controller;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.controllerImpl.CategoryControllerlMPL;
import com.backendcafe.backend.entity.Category;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.service.CategoryService;
import com.backendcafe.backend.untils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CategoryController implements CategoryControllerlMPL {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public ResponseEntity<JsonModel> addNewCategory(Map<String, String> body) throws JSONException {
        log.info("Inside Add New Category");
        try {
            return categoryService.addNewCategory(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {

        log.info("Inside GetALL");
        try {
            return categoryService.getAllCategory(filterValue);
        } catch (Exception ex) {
            ex.printStackTrace();

        }

        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<JsonModel> updateCategory(Map<String, String> body) throws JSONException {
        try {
            return categoryService.updateCategory(body);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }
}
