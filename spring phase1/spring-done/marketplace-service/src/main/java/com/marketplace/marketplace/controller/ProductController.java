package com.marketplace.marketplace.controller;

import com.marketplace.marketplace.entity.Product;
import com.marketplace.marketplace.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Get a product by ID
    @GetMapping("/{product_id}")
public ResponseEntity<?> getProduct(@PathVariable("product_id") Integer product_id) {
    Optional<Product> product = productService.getProductById(product_id);
    if (product.isPresent()) {
        return ResponseEntity.ok(product.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }
}
}
