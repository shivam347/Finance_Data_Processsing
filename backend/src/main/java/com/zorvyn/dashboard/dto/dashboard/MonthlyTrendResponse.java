package com.zorvyn.dashboard.dto.dashboard;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendResponse {

    private String month; //eg 2024 - 01
    private BigDecimal income;
    private BigDecimal expense;
    
}
