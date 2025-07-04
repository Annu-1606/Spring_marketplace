package com.microservice.h2db.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference; 


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")  // Maps the column correctly in DB
    @JsonProperty("order_id")   // Ensures JSON uses "order_id"
    private Integer orderId;
    
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("total_price")
    private Integer totalPrice;
    private String status;  // "PLACED", "CANCELLED", "DELIVERED"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference  // Prevents circular reference when serializing 'items' in Order
    private List<OrderItem> items;
}

