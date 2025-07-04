package com.marketplace.marketplace.service;

import com.marketplace.marketplace.dto.*;
import com.marketplace.marketplace.entity.*;
import com.marketplace.marketplace.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private RestTemplate restTemplate;

    @Transactional
    public Order placeOrder(OrderRequest orderRequest) {
        System.out.println("🔹 Received Order Request: " + orderRequest);

        if (orderRequest.getId() == null) {
            throw new RuntimeException("❌ Error: user_id is null in request!");
        }

        System.out.println("🔹 Fetching user data from: http://host.docker.internal:8080/users/" + orderRequest.getId());
        UserDTO user;
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(
                "http://host.docker.internal:8080/users/" + orderRequest.getId(), UserDTO.class);
            user = userResponse.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "❌ User not found.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "⚠️ User Service is unavailable.");
        }

        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        // Check product stock and calculate total price
        for (OrderItemRequest item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("❌ Product not found: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("❌ Insufficient stock for product: " + product.getName());
            }

            System.out.println("📦 Updating stock for product ID " + product.getProduct_id() + 
                               " from " + product.getStockQuantity() + " to " + 
                               (product.getStockQuantity() - item.getQuantity()));

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            totalPrice += product.getPrice() * item.getQuantity();
            orderItems.add(new OrderItem(null, null, product.getProduct_id(), item.getQuantity()));
        }

        // Apply discount if it's the user's first order
        boolean isFirstOrder = !user.isDiscountAvailed();
        if (isFirstOrder) {
            totalPrice *= 0.9;  // Apply 10% discount
            System.out.println("🛒 Applying first-order discount: New total = " + totalPrice);
            try {
                restTemplate.put("http://host.docker.internal:8080/users/" + orderRequest.getId()+"/discount", 
                    new UserDTO(orderRequest.getId(), true));
            } catch (Exception e) {
                System.out.println("⚠️ Warning: Could not update discount status for user.");
            }
        }

        // Debit wallet
        System.out.println("💰 Deducting " + totalPrice + " from user " + orderRequest.getId() + "'s wallet.");
        WalletRequest walletRequest = new WalletRequest("debit", totalPrice);
        try {
            restTemplate.exchange("http://host.docker.internal:8082/wallets/" + orderRequest.getId(), 
                HttpMethod.PUT, new HttpEntity<>(walletRequest), WalletResponse.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("❌ Insufficient balance in wallet for user ID " + orderRequest.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "⚠️ Wallet Service is unavailable.");
        }

        // Save order
        Order order = new Order(null, orderRequest.getId(), totalPrice, "PLACED", orderItems);
        orderRepository.save(order);
        orderItems.forEach(item -> item.setOrder(order));

        System.out.println("✅ Order placed successfully with ID: " + order.getId());
        return order;
    }

    @Transactional
public boolean cancelOrder(Integer orderId) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (orderOptional.isPresent()) {
        Order order = orderOptional.get();
        if (!order.getStatus().equals("PLACED")) {
            return false;
        }

        // Restore stock
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        System.out.println("Order restocked.");





        // Refund money 
        WalletRequest walletRequest = new WalletRequest("credit", order.getTotalPrice());
        System.out.println(order.getTotalPrice());
       try {
    ResponseEntity<WalletResponse> response = restTemplate.exchange(
        "http://host.docker.internal:8082/wallets/" + order.getUserId(),
        HttpMethod.PUT,
        new HttpEntity<>(walletRequest),
        WalletResponse.class
    );
    
    System.out.println("Wallet refund response: " + response.getStatusCode() + " - " + response.getBody());
} catch (Exception e) {
    System.out.println("⚠️ Error while refunding money: " + e.getMessage());
    e.printStackTrace();
}


        // Mark order as cancelled
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return true;
    }
    return false;
}


    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void resetMarketplace() {
        orderRepository.findAll().forEach(order -> cancelOrder(order.getId())); // Cancel all orders
    }

    @Transactional
    public boolean deleteUserOrders(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        boolean deleted = false;
        for (Order order : orders) {
            if (order.getStatus().equals("PLACED")) {
                cancelOrder(order.getId());
                deleted = true;
            }
        }
        return deleted;
    }

    public boolean markAsDelivered(Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (!order.getStatus().equals("PLACED")) {
                return false;
            }
            order.setStatus("DELIVERED");
            orderRepository.save(order);
            return true;
        }
        return false;
    }
}
