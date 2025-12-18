package com.financas.dto;

import com.financas.model.Transaction;
import lombok.Data;

@Data
public class CategoryDTO {
    private String id;
    private String name;
    private String icon;
    private String color;
    private Transaction.TransactionType type;
}