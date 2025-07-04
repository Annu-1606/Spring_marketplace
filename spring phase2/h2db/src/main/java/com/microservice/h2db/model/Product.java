package com.microservice.h2db.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @Column(name = "product_id")
    private Integer product_id;
    
    private String name;
    private String description;
    private Integer price;
    
    
    @JsonProperty("stock_quantity")  // ✅ Ensures correct JSON field name
    @Column(name = "stock_quantity")  // ✅ Ensures correct database column
    private Integer stockQuantity;
}
