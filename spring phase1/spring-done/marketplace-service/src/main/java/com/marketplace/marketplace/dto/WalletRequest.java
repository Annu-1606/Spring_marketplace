package com.marketplace.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor  // ✅ Add constructor to accept "action" and "amount"
@NoArgsConstructor
public class WalletRequest {
    private String action; // "debit" or "credit"
    private Integer amount;
}
