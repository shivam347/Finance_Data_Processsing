package com.zorvyn.dashboard.dto.record;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zorvyn.dashboard.entity.FinanceRecord;
import com.zorvyn.dashboard.enums.TransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecordResponse {

    private String id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate recordDate;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecordResponse fromEntity(FinanceRecord record) {
        return RecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .category(record.getCategory())
                .type(record.getType())
                .recordDate(record.getRecordDate())
                .notes(record.getNotes())
                .createdBy(record.getCreatedBy().getId())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();

    }

}
