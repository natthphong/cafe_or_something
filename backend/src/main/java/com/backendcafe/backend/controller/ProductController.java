package com.backendcafe.backend.controller;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.controllerImpl.ProductControllerIMPL;
import com.backendcafe.backend.entity.Product;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.service.ProductService;
import com.backendcafe.backend.untils.CafeUtils;
import com.backendcafe.backend.wrapper.ProductWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ProductController implements ProductControllerIMPL {

    private final ProductService productService;

    @Value("${project.image}")
    private String path;

    public ProductController(ProductService fileService) {
        this.productService = fileService;
    }

    @Override
    public ResponseEntity<JsonModel> fileUpload(@PathVariable(name = "productId", required = true)
                                                Integer productId,
                                                @RequestParam(name = "image", required = true)
                                                MultipartFile image) throws IOException {
        log.info("Add");
        String api = productService.upLoadImage(path, image, productId);
        if (api.equals(null)) {
            return CafeUtils.deFault();
        } else if (api.equals("No")) {
            return CafeUtils.message("NO PRODUCTID", HttpStatus.UNAUTHORIZED);

        } else if (api.equals("NotAdmin")) {
            return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);
        }
        return CafeUtils.message(api, HttpStatus.OK);
    }

    @Override
    public void showImage(@PathVariable(required = true, name = "filename") String filename
            , HttpServletResponse response) throws IOException {
        log.info("SHOW");
        InputStream image = productService.getFile(path, filename);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(image, response.getOutputStream());
    }

    @Override
    public ResponseEntity<JsonModel> addProduct(Map<String, String> body) {
        try {
            return productService.addProduct(body);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        log.info("Inside Get All Product");
        try {
            return productService.getAllProduct();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<JsonModel> updateProduct(Map<String, String> body) {
        try {

            return productService.updateProduct(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> deleteProduct(String id) {
        try {
            return productService.deleteProduct(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> updateStatus(Map<String, String> body) {
        try {
            return  productService.updateStatus(body);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer categoryId) {
            try {
                    return  productService.getByCategory(categoryId);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return  new ResponseEntity<>(new ArrayList<>() , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getByProductId(Integer productId) {
        try {
            return productService.getByProductId(productId);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ProductWrapper()  , HttpStatus.BAD_REQUEST);
    }


}
