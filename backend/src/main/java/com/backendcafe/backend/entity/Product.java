package com.backendcafe.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;


@NamedQuery(name = "Product.findByName", query = "select p from Product p where p.name=:name")
@NamedQuery(name = "Product.findByProductId", query = "select new com.backendcafe.backend.wrapper.ProductWrapper(p.id,p.name,p.description , p.price ,p.status,p.category.id,p.category.name,p.image) from Product p where p.id =:id")
@NamedQuery(name = "Product.findByCategory", query = "select new com.backendcafe.backend.wrapper.ProductWrapper(p.id,p.name) from Product p where p.category.id =:id")
@NamedQuery(name = "Product.findAllProduct", query = "select new com.backendcafe.backend.wrapper.ProductWrapper(p.id,p.name,p.description , p.price ,p.status,p.category.id,p.category.name,p.image) from Product p")
@NamedQuery(name = "Product.updateStatus", query = "update Product p set p.status=:status where p.id=:id")
@Data
@Entity
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "product")
public class Product implements Serializable {

    public static final Long serialVersionUid = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;


    @Column(name = "status")
    private String status;

    @Column(name = "image")
    private String image;
}
