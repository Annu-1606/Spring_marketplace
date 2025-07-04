package com.microservice.h2db.Repository;

import com.microservice.h2db.model.Product;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductRepository extends Repository<Product, Integer> {
    Product save(Product product);
    Product findById(Integer id);
    List<Product> findAll();
}

