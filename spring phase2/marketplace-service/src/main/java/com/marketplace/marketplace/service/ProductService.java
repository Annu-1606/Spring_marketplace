package com.marketplace.marketplace.service;

import com.marketplace.marketplace.entity.Product;
import com.marketplace.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Fetch all products from H2 database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Fetch a single product by ID
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    // Check stock availability
    public boolean isProductAvailable(Integer productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStockQuantity() >= quantity;
    }

    // Reduce stock quantity when an order is placed
    public ResponseEntity<String> reduceStock(Integer productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getStockQuantity() >= quantity) {
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);
                return ResponseEntity.ok("stock updated");
            } else {
                throw new RuntimeException("Insufficient stock");
            }
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    // Restore stock when an order is canceled
    public ResponseEntity<String> restoreStock(Integer productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);
            return ResponseEntity.ok("stck restored");
        } else {
            throw new RuntimeException("Product not found");
        }
    }
}

