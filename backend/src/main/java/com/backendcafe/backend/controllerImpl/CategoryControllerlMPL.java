package com.backendcafe.backend.controllerImpl;


import com.backendcafe.backend.entity.Category;
import com.backendcafe.backend.models.JsonModel;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryControllerlMPL {

    @PostMapping(path = "/add")
    public ResponseEntity<JsonModel> addNewCategory(@RequestBody(required = true)
                                                 Map<String, String> body) throws JSONException;

    @GetMapping(path = "/get")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false)
                                                         String filterValue);

    @PostMapping(path = "/update")
    ResponseEntity<JsonModel> updateCategory(@RequestBody(required = true) Map<String, String> body) throws JSONException;

}
