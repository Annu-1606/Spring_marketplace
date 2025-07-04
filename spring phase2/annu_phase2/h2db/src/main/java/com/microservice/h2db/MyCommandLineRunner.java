package com.microservice.h2db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.microservice.h2db.model.Product;
import com.microservice.h2db.Repository.ProductRepository;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepo;

    private void loadProducts() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("products.csv"), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstRow = true;  // Flag to skip header
            
            while ((line = br.readLine()) != null) {
                if (isFirstRow) {  // Skip the first row (header)
                    isFirstRow = false;
                    continue;
                }
                System.out.println("processing csv row:" + line);
                String[] data = line.split(",");

                // Validate if all required columns are present
                if (data.length < 5) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }

                try {
                    Product product = new Product(
                        Integer.parseInt(data[0].trim()),  // product_id
                        data[1].trim(),                   // name
                        data[2].trim(),                   // description
                        Integer.parseInt(data[3].trim()), // price
                        Integer.parseInt(data[4].trim())  // stockQuantity
                    );

                    productRepo.save(product);
                    System.out.println("product saved:" +product);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping row with invalid number format: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        loadProducts();
    }
}

