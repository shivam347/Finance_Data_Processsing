package com.zorvyn.dashboard.dto.record;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.zorvyn.dashboard.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class UpdateRecordRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @NotBlank(message = "category is required")
    private String category;


    @NotNull(message = "Record date is required")
    private LocalDate recordDate;

    
    private String notes;
    
}
