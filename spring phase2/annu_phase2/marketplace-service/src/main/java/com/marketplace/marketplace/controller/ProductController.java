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
    private ProductService productService;  // ✅ Use service, not repository

    // ✅ Fetch all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return products.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ✅ Fetch a product by ID
    @GetMapping("/{product_id}")
    public ResponseEntity<?> getProduct(@PathVariable("product_id") Integer productId) {
        Optional<Product> product = productService.getProductById(productId);
        return product.isPresent()
                ? ResponseEntity.ok(product.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Product not found");
    }

    // ✅ Reduce stock for a product
    @PostMapping("/{product_id}/reduceStock")
    public ResponseEntity<String> reduceStock(@PathVariable("product_id") Integer productId,
                                              @RequestParam("quantity") Integer quantity) {
        return productService.reduceStock(productId, quantity);
    }

    // ✅ Restore stock when order is canceled
    @PostMapping("/{product_id}/restoreStock")
    public ResponseEntity<String> restoreStock(@PathVariable("product_id") Integer productId,
                                               @RequestParam("quantity") Integer quantity) {
        return productService.restoreStock(productId, quantity);
    }
}

