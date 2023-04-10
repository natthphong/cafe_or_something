package com.backendcafe.backend.wrapper;

import com.backendcafe.backend.entity.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ProductWrapper {
    private Integer id;


    private String name;

    private String description;

    private Integer price;
    private String status;

    public ProductWrapper(Integer id, String name, String description, Integer price, String status, Integer category_id, String categoryName, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.category_id = category_id;
        this.categoryName = categoryName;
        this.image = image;
    }

    private Integer category_id;
    private String categoryName;
    private String image;

    public ProductWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductWrapper(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
