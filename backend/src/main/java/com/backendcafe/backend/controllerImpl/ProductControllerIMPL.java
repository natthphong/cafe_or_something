package com.backendcafe.backend.controllerImpl;

import com.backendcafe.backend.entity.Product;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.untils.CafeUtils;
import com.backendcafe.backend.wrapper.ProductWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/product")
public interface ProductControllerIMPL {
    @PostMapping("/addImage/{productId}")
    public ResponseEntity<JsonModel> fileUpload(@PathVariable(name = "productId", required = true)
                                                Integer productId,
                                                @RequestParam(name = "image", required = true) MultipartFile image) throws IOException;

    @GetMapping("/getImage/{filename}")
    public void showImage(@PathVariable(required = true, name = "filename") String filename
            , HttpServletResponse response) throws IOException;

    @PostMapping("/addProduct")
    public ResponseEntity<JsonModel> addProduct(@RequestBody(required = true) Map<String, String> body);

    @GetMapping("/get")
    public ResponseEntity<List<ProductWrapper>> getAllProduct();

    @PutMapping("/update")
    public ResponseEntity<JsonModel> updateProduct(@RequestBody(required = true) Map<String, String> body);

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<JsonModel> deleteProduct(@PathVariable(name = "id", required = true) String id);

    @PutMapping("/updateStatus")
    public ResponseEntity<JsonModel> updateStatus(@RequestBody(required = true) Map<String, String> body);

    @GetMapping("/getByCategory/{categoryId}")
    public ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable(required = true) Integer categoryId);

    @GetMapping("/getByProductId/{productId}")
    public ResponseEntity<ProductWrapper> getByProductId(@PathVariable(required = true) Integer productId);
}
