package com.marketplace.marketplace.service;

import com.marketplace.marketplace.entity.Product;
import com.marketplace.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String ACCOUNT_SERVICE_URL = "http://host.docker.internal:8080/users/";

    // Load CSV data into the database
    public void loadCSVData() {
        try {
            if (productRepository.count() > 0) {
                System.out.println("⚠️ Products already exist in the database. Skipping CSV load.");
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new ClassPathResource("products.csv").getInputStream(), StandardCharsets.UTF_8)
            );

            List<Product> products = reader.lines()
                    .skip(1) // Skip the header row
                    .map(this::mapToProduct)
                    .filter(product -> product != null)  // Remove invalid rows
                    .collect(Collectors.toList());

            productRepository.saveAll(products);
            System.out.println("✅ Products loaded successfully from CSV.");
        } catch (Exception e) {
            System.err.println("❌ Error loading CSV data: " + e.getMessage());
        }
    }

 


private Product mapToProduct(String line) {
    String[] fields = line.split(",");

    // ✅ Ensure there are at least 5 fields before processing
    if (fields.length < 5) {
        System.err.println("❌ Skipping invalid CSV row (missing fields): " + line);
        return null;
    }

    try {
        int id = Integer.parseInt(fields[0].trim());
        String name = fields[1].trim();
        String description = fields[2].trim();
        int price = Integer.parseInt(fields[3].trim());
        int stockQuantity = Integer.parseInt(fields[4].trim());

        return new Product(id, name, description, price, stockQuantity);
    } catch (NumberFormatException e) {
        System.err.println("❌ Error parsing row (invalid number format): " + line);
        return null;
    }
}



    // Fetch all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Fetch a single product by ID
    public Optional<Product> getProductById(Integer product_id) {
    System.out.println("🔍 Searching for product with ID: " + product_id);
    
    Optional<Product> product = productRepository.findById(product_id);
    
    if (product.isEmpty()) {
        System.err.println("❌ Product ID " + product_id + " not found in database.");
    } else {
        System.out.println("✅ Found product: " + product.get().getName());
    }
    
    return product;
}


    // Check stock availability for a given product
    public boolean isProductAvailable(Integer productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && product.get().getStockQuantity() >= quantity;
    }

    // Reduce stock quantity when an order is placed
    public ResponseEntity<String> reduceStock(Integer productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Product product = productOptional.get();
        if (product.getStockQuantity() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock for product " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
        return ResponseEntity.ok("Stock updated successfully");
    }

    // Restore stock quantity when an order is canceled
    public ResponseEntity<String> restoreStock(Integer productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Product product = productOptional.get();
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
        return ResponseEntity.ok("Stock restored successfully");
    }
}

