package com.marketplace.marketplace.dto;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class OrderRequest {
    
    @JsonProperty("user_id")
    private Integer id;

    @JsonProperty("items")
    private List<OrderItemRequest> items;
}

