package com.financas.dto;

import com.financas.model.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String id;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String attachment;
    private CategoryDTO category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}