package com.marketplace.marketplace.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private boolean discountAvailed;

    public UserDTO() {}  // Default no-args constructor

    public UserDTO(Integer id, boolean discountAvailed) {
        this.id = id;
        this.discountAvailed = discountAvailed;
    }

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public boolean isDiscountAvailed() { return discountAvailed; }
    public void setDiscountAvailed(boolean discountAvailed) { this.discountAvailed = discountAvailed; }
}

