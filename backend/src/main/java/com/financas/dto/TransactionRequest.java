package com.financas.dto;

import com.financas.model.Transaction;
import jakarta.validation.constraints. NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Tipo é obrigatório")
    private Transaction.TransactionType type;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal amount;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotNull(message = "Data é obrigatória")
    private LocalDate date;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoryId;

    private String attachment;
}