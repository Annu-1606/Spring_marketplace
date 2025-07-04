package com.microservice.h2db.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets") // Explicit table name for clarity
public class Wallet {
    
    @Id
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer balance = 0; // Default balance to avoid null values

    // Atomic balance update methods for concurrency safety
    public synchronized void credit(Integer amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public synchronized boolean debit(Integer amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false; // Prevent overdraft
    }
}

