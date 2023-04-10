package com.backendcafe.backend.service;

import com.backendcafe.backend.repository.BillRepository;
import com.backendcafe.backend.repository.CategoryRepository;
import com.backendcafe.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private final BillRepository billRepository;

    public DashboardService(CategoryRepository categoryRepository, ProductRepository productRepository, BillRepository billRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.billRepository = billRepository;
    }

    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryRepository.count());
        map.put("product", productRepository.count());
        map.put("bill", billRepository.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
