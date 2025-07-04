package com.marketplace.marketplace.dto;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference; 

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {
    @JsonProperty("user_id")  // Correctly map "user_id" to JSON
    private Integer id;  // Keep as id for internal consistency
    private Integer balance;
}

