package com.financas.dto;

import com.financas.model.Transaction;
import jakarta.validation.constraints. NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String icon;

    private String color;

    @NotNull(message = "Tipo é obrigatório")
    private Transaction.TransactionType type;
}