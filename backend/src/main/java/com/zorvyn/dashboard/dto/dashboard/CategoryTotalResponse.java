package com.zorvyn.dashboard.dto.dashboard;

import java.math.BigDecimal;

import com.zorvyn.dashboard.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTotalResponse {
    private String category;
    private TransactionType type;
    private BigDecimal total;
    
}
