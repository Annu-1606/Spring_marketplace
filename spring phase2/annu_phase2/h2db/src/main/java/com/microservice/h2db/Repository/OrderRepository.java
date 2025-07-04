package com.microservice.h2db.Repository;

import com.microservice.h2db.model.Order;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface OrderRepository extends Repository<Order, Integer> {
    Order save(Order order);
    Order findById(Integer id);
    List<Order> findByUserId(Integer userId);
    List<Order> findAll();
}

