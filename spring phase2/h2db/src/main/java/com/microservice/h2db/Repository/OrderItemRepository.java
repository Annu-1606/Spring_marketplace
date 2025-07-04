package com.microservice.h2db.Repository;

import com.microservice.h2db.model.OrderItem;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderItemRepository extends Repository<OrderItem, Integer> {
    OrderItem save(OrderItem orderItem);
    OrderItem findById(Integer id);
    List<OrderItem> findAll();
}

