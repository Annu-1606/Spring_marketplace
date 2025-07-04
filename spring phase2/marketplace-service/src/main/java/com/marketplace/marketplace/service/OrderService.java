package com.marketplace.marketplace.service;

import com.marketplace.marketplace.dto.*;
import com.marketplace.marketplace.entity.*;
import com.marketplace.marketplace.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.marketplace.marketplace.repository.ProductRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


     
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository; // Inject ProductRepository directly

    @Autowired
    private RestTemplate restTemplate;

    private static final String ACCOUNT_SERVICE_URL = "http://account-service:8080/users/";
    private static final String WALLET_SERVICE_URL = "http://wallet-service:8082/wallets/";

    /**
     * Places an order with transactional safety.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public Order placeOrder(OrderRequest orderRequest) {
        System.out.println(" Received Order Request: " + orderRequest);

        if (orderRequest.getId() == null) {
            throw new RuntimeException(" Error: user_id is null in request!");
        }

        // Fetch user details
        UserDTO user = getUserData(orderRequest.getId());
        System.out.println(" User details: " + user);

        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        // **Step 1: Check product stock via JPA Repository**
        for (OrderItemRequest item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("\u274c Product not found in H2DB."));

            System.out.println(" Received product: " + product);
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(" Insufficient stock for product: " + product.getName());
            }

            totalPrice += product.getPrice() * item.getQuantity();
            orderItems.add(new OrderItem(null, null, item.getProductId(), item.getQuantity()));
        }

        // **Step 2: Deduct stock via JPA**
        for (OrderItemRequest item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
        System.out.println(" Stock deducted");

        // **Step 3: Apply discount if first order**
        boolean isFirstOrder = !user.isDiscountAvailed();
        if (isFirstOrder) {
            totalPrice *= 0.9;
            updateUserDiscountStatus(orderRequest.getId(),true);
        }

        // **Step 4: Deduct wallet balance**
        try {
        debitWallet(orderRequest.getId(), totalPrice);
    } catch (Exception e) {
        // **ROLLBACK DISCOUNT if insufficient balance**
        System.out.println(" ❌ Order failed due to insufficient balance. Resetting discount status.");
        if (isFirstOrder) {
            
            updateUserDiscountStatus(orderRequest.getId(), false);
            System.out.println(" ❌ order statusbupdated to false");
        }

        rollbackStock(orderItems);
        throw e;
    }

        // **Step 5: Save order**
        Order order = new Order(null, orderRequest.getId(), totalPrice, "PLACED", orderItems);
        orderRepository.save(order);
        orderItems.forEach(item -> item.setOrder(order));

        System.out.println(" Order placed successfully with ID: ");
        return order;
    }


   
   

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public boolean cancelOrder(Integer orderId) {
        Optional<Order> orderOptional = orderRepository.findByOrderId(orderId);
        if (orderOptional.isEmpty()) return false;

        Order order = orderOptional.get();
        if (!order.getStatus().equals("PLACED")) {
            return false;
        }

        rollbackStock(order.getItems());
        refundWallet(order.getUserId(), order.getTotalPrice());
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        System.out.println("✅ Order ID " + orderId + " cancelled and refunded.");
        return true;
    }

    /**
     * Fetches product details from H2DB Service.
     */

private Product getProductFromH2DB(Integer productId) {
    System.out.println(" Fetching product from ProductRepository: " + productId);
    return productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Product not found in H2DB."));
}



    /**
     * Deducts stock in H2DB.
     */
  private void reduceStockInH2DB(Integer productId, Integer quantity) {
    try {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Product not found in H2DB."));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException(" Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product); // ✅ Save updated stock in database

        System.out.println(" Stock reduced successfully for product ID: " + productId);
    } catch (Exception e) {
        System.err.println(" Failed to reduce stock in H2DB: " + e.getMessage());
        throw new RuntimeException(" Failed to reduce stock in H2DB.");
    }
}


    /**
     * Rolls back stock if an order fails.
     */
  private void rollbackStock(List<OrderItem> orderItems) {
    for (OrderItem item : orderItems) {
        try {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Product not found during rollback."));

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product); // ✅ Restore stock in database

            System.out.println(" Restored stock for product ID: " + item.getProductId());
        } catch (Exception e) {
            System.err.println(" Error restoring stock for product ID: " + item.getProductId());
        }
    }
}


    /**
     * Retrieves user data from Account Service.
     */
    private UserDTO getUserData(Integer userId) {
        try {
            ResponseEntity<UserDTO> userResponse = restTemplate.getForEntity(ACCOUNT_SERVICE_URL + userId, UserDTO.class);
            return userResponse.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "❌ User not found.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "⚠️ User Service is unavailable.");
        }
    }

    /**
     * Updates user discount status in Account Service.
     */
   private void updateUserDiscountStatus(Integer userId, boolean status) {
    try {
        restTemplate.put(ACCOUNT_SERVICE_URL + userId + "/discount",
            new UserDTO(userId, status));
        System.out.println(" ✅ Updated discount status for user ID: " + userId + " to " + status);
    } catch (Exception e) {
        System.out.println(" Warning: Could not update discount status for user.");
    }
}


    /**
     * Deducts amount from the wallet service.
     */
    private void debitWallet(Integer userId, int amount) {
        try {
            WalletRequest walletRequest = new WalletRequest("debit", amount);
            restTemplate.exchange(WALLET_SERVICE_URL + userId, HttpMethod.PUT, new HttpEntity<>(walletRequest), WalletResponse.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("❌ Insufficient balance in wallet for user ID " + userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "⚠️ Wallet Service is unavailable.");
        }
    }

    /**
     * Refunds amount to the wallet service.
     */
    private void refundWallet(Integer userId, int amount) {
        try {
            WalletRequest walletRequest = new WalletRequest("credit", amount);
            restTemplate.exchange(WALLET_SERVICE_URL + userId, HttpMethod.PUT, new HttpEntity<>(walletRequest), WalletResponse.class);
        } catch (Exception e) {
            System.out.println("⚠️ Error while refunding money: " + e.getMessage());
        }
    }
    
    @Transactional
    public List<Order> getAllOrders(){
    return orderRepository.findAll();
    }
    
    @Transactional
    public Optional<Order> getOrderById(Integer orderId){
    return orderRepository.findByOrderId(orderId);
    }
    
    @Transactional
    public List<Order> getOrdersByUserId(Integer UserId){
    return orderRepository.findByUserId(UserId);
    }
    
    @Transactional
   public boolean markAsDelivered(Integer orderId){
   Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
   if(orderOpt.isPresent()){
   Order order =orderOpt.get();
   if(!order.getStatus().equals("PLACED")){
   return false;
   }
   order.setStatus("DELIVERED");
   orderRepository.save(order);
   return true;
   }return false;
   }
   
    @Transactional
    public void resetMarketplace(){
    orderRepository.findAll().forEach(order -> cancelOrder(order.getOrderId()));
    }
    
    
    @Transactional
    public boolean deleteUserOrders(Integer userId){
    return orderRepository.findByUserId(userId)
    .stream()
    .filter(order -> order.getStatus().equals("PLACED"))
    .map(order -> cancelOrder(order.getOrderId()))
    .reduce(false, Boolean::logicalOr);
    }
    
}
