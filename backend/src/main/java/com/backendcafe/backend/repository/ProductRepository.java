package com.backendcafe.backend.repository;

import com.backendcafe.backend.entity.Product;
import com.backendcafe.backend.wrapper.ProductWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product ,Integer> {
    List<Product> findByName(@Param("name") String name);
    List<ProductWrapper> findAllProduct();
    List<ProductWrapper> findByCategory(@Param("id")Integer id);
    ProductWrapper findByProductId(@Param("id")Integer id);
    @Modifying
    @Transactional
    Integer updateStatus(@Param("status")String status  , @Param("id") Integer id);

}
