package com.backendcafe.backend.service;

import com.backendcafe.backend.config.JwtFilter;
import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.entity.Category;
import com.backendcafe.backend.entity.Product;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.repository.CategoryRepository;
import com.backendcafe.backend.repository.ProductRepository;
import com.backendcafe.backend.untils.CafeUtils;
import com.backendcafe.backend.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class ProductService {

    private final JwtFilter jwtFilter;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(JwtFilter jwtFilter, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.jwtFilter = jwtFilter;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public String upLoadImage(String path, MultipartFile file, Integer productId) throws IOException {
        try {
            Optional<Product> p = productRepository.findById(productId);
            log.info("product {}", p);
            if (!p.isEmpty() && jwtFilter.isAdmin()) {
                log.info("Is Admin");
                log.info("Inside upLoad {}", path);
                String name = p.get().getName() + ".png";
                String randomID = UUID.randomUUID().toString();
                //String file_name = randomID.concat(name.substring(name.lastIndexOf(".")));
                String filePath = path + File.separator + name;
                String api = CafeConstants.URL + "/product/getImage/" + name;
                File f = new File(path);
                if (!f.exists()) {
                    f.mkdir();
                }
                p.get().setImage(api);
                productRepository.save(p.get());
                Files.copy(file.getInputStream(), Paths.get(filePath));
                return api;
            } else if (p.isEmpty()) {
                return "No";
            } else {
                return "NotAdmin";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public InputStream getFile(String path, String filename) throws FileNotFoundException {
        String fullPath = path + File.separator + filename;
        InputStream is = new FileInputStream(fullPath);
        return is;
    }

    public ResponseEntity<JsonModel> addProduct(Map<String, String> body) {
        log.info("In side AddProduct {}", body);
        try {
            if (jwtFilter.isAdmin()) {
                log.info("IS ADMIN");
                if (validateAddProduct(body, false)) {
                    Optional<Category> c = categoryRepository.findById(Integer.parseInt(body.get("categoryID")));
                    log.info("hello c {}", c.get());
                    if (!c.isEmpty()) {
                        List<Product> check = productRepository.findByName(body.get("name"));
                        log.info("check {}", check);
                        if (check.isEmpty()) {
                            Product p = productRepository.save(getProductFromMap(body, c.get(), false));
                            return CafeUtils.message(p.getImage(), HttpStatus.OK);
                        } else {
                            return CafeUtils.message("NAME PRODUCT HAVE ALREADY", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        return CafeUtils.message("Category ID Invisible", HttpStatus.BAD_REQUEST);
                    }
                }

            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    public boolean validateAddProduct(Map<String, String> body, boolean validateId) {
        if (body.containsKey("name")) {
            if (body.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }

        return false;
    }

    public Product getProductFromMap(Map<String, String> body, Category c, boolean isAdd) {

        Product product = new Product();
        product.setCategory(c);
        if (isAdd) {
            product.setId(Integer.parseInt(body.get("id")));
        } else {
            product.setStatus("true");
            product.setImage(CafeConstants.URL + "/product/getImage/img.png");
        }

        product.setName(body.get("name"));
        product.setDescription(body.get("description"));
        product.setPrice(Integer.parseInt(body.get("price")));

        log.info("product {}", product);
        return product;
    }

    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            log.info("Service {}", productRepository.findAllProduct());

            return new ResponseEntity<>(productRepository.findAllProduct(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<JsonModel> updateProduct(Map<String, String> body) {
        log.info("Update");
        try {
            if (jwtFilter.isAdmin()) {
                log.info("Is Admin");
                if (validateAddProduct(body, true)) {
                    Optional<Product> p = productRepository.findById(Integer.parseInt(body.get("id")));
                    if (!p.isEmpty()) {
                        Product newProduct = getProductFromMap(body, p.get().getCategory(), true);
                        newProduct.setStatus(p.get().getStatus());
                        newProduct.setImage(p.get().getImage());
                        log.info("product {}", newProduct);
                        productRepository.save(newProduct);
                        return CafeUtils.message("UPDATE SUCCESS", HttpStatus.OK);
                    } else {
                        return CafeUtils.message("ProductID INVALID", HttpStatus.UNAUTHORIZED);
                    }

                } else {
                    return CafeUtils.message(CafeConstants.INVALID_DATA, HttpStatus.UNAUTHORIZED);
                }
            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    public ResponseEntity<JsonModel> deleteProduct(String id) {
        try {
            if (jwtFilter.isAdmin()) {

                Optional<Product> p = productRepository.findById(Integer.parseInt(id));
                if (!p.isEmpty()) {
                    productRepository.delete(p.get());
                    return CafeUtils.message("DELETE SUCCESS", HttpStatus.OK);
                } else {
                    return CafeUtils.message("PRODUCT ID INVALIDATE", HttpStatus.BAD_REQUEST);
                }


            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    public ResponseEntity<JsonModel> updateStatus(Map<String, String> body) {
        try {
            if (jwtFilter.isAdmin()) {
                if (body.containsKey("id") && body.containsKey("status")) {
                    Optional<Product> p = productRepository.findById(Integer.parseInt(body.get("id")));
                    if (!p.isEmpty()) {

                        productRepository.updateStatus(body.get("status"), Integer.parseInt(body.get("id")));
                        return CafeUtils.message("PRODUCT STATUS IS UPDATE ALREADY", HttpStatus.OK);
                    } else {
                        return CafeUtils.message("PRODUCT ID INVALIDATE", HttpStatus.BAD_REQUEST);
                    }

                } else {
                    return CafeUtils.message("id or status InValidate", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.message("YOU NOT ADMIN", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }


    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer categoryId) {

        try {
            log.info("Inside getByCategory");
            Optional<Category> c = categoryRepository.findById(categoryId);
            if (!c.isEmpty()) {
                log.info("Have Category");
                List<ProductWrapper> productWrappers = productRepository.findByCategory(categoryId);
                return new ResponseEntity<>(productWrappers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ProductWrapper getProductWrapper(Optional<Product> product) {
        return new ProductWrapper(product.get().getId(), product.get().getName()
                , product.get().getDescription(), product.get().getPrice());
    }

    public ResponseEntity<ProductWrapper> getByProductId(Integer productId) {
        try {
            log.info("Inside GetByProductId");
            Optional<Product> product = productRepository.findById(productId);

            if (!product.isEmpty()) {
                ProductWrapper productWrapper = getProductWrapper(product);
                log.info("Have product {}", productWrapper);
                return new ResponseEntity<>(productWrapper, HttpStatus.OK);
            } else {
                log.info("Haven't product");
                return new ResponseEntity<>(new ProductWrapper(), HttpStatus.BAD_REQUEST);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.BAD_REQUEST);
    }
}
