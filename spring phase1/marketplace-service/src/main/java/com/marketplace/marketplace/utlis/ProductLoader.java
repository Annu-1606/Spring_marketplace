package com.marketplace.marketplace.utils;

import com.marketplace.marketplace.entity.Product;
import com.marketplace.marketplace.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductLoader {

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    public void loadProductsFromCSV() {
        if (!productRepository.findAll().isEmpty()) return; // Prevents duplicate loading

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("products.csv");
            if (inputStream == null) {
                System.err.println("⚠️ Error: products.csv file not found!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<Product> products = new ArrayList<>();
            String line;

            reader.readLine(); // Skip CSV header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Product product = new Product(
                        Integer.parseInt(data[0]), // id
                        data[1],  // name
                        data[2],  // description
                        Integer.parseInt(data[3]), // price
                        Integer.parseInt(data[4])  // stock_quantity
                );
                products.add(product);
            }

            productRepository.saveAll(products);
            System.out.println("✅ Products loaded successfully from CSV!");
        } catch (Exception e) {
            System.err.println("⚠️ Error loading products: " + e.getMessage());
        }
    }}
