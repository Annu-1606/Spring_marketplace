
package com.marketplace.marketplace;

import com.marketplace.marketplace.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MarketplaceServiceApplication implements CommandLineRunner {

    @Autowired
    private ProductService productService; // Inject ProductService

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceServiceApplication.class, args);
        System.out.println("✅ Marketplace Service is running on port 8081");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("📥 Loading products from CSV...");
        productService.loadCSVData();  // Ensure CSV is loaded before processing requests
        System.out.println("✅ CSV data loading completed.");
    }
}


