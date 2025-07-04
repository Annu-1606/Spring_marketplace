package com.marketplace.marketplace.controller;

import com.marketplace.marketplace.dto.OrderRequest;
import com.marketplace.marketplace.entity.Order;
import com.marketplace.marketplace.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
     
     
     // Get all orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Place a new order
    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.placeOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get order by ID
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId) {
    Optional<Order> order = orderService.getOrderById(orderId);
    if (order.isPresent()) {
        return ResponseEntity.ok(order.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
    }
}

    // Get all orders for a user
    @GetMapping("/orders/users/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // Cancel an order
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId) {
        boolean canceled = orderService.cancelOrder(orderId);
        if (canceled) {
            return ResponseEntity.ok("Order canceled successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot cancel order");
        }
    }
    
    
     @PutMapping("/orders/{orderId}")
      public ResponseEntity<?> markOrderAsDelivered(@PathVariable Integer orderId) {
      boolean updated = orderService.markAsDelivered(orderId);
      if (updated) {
          return ResponseEntity.ok("Order marked as delivered");
      } else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot mark order as delivered");
      }
  }
    

    @DeleteMapping("/marketplace")
    public ResponseEntity<?> resetMarketplace() {
    orderService.resetMarketplace();
    return ResponseEntity.ok("Marketplace reset successful");
    }
    
    @DeleteMapping("/marketplace/users/{userId}")
    public ResponseEntity<?> deleteUserOrders(@PathVariable Integer userId) {
    boolean deleted = orderService.deleteUserOrders(userId);
    if (deleted) {
        return ResponseEntity.ok("User's placed orders deleted");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found for user");
    }
   }
   }
