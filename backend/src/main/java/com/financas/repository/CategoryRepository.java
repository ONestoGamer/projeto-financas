package com.financas.repository;

import com.financas.model.Category;
import com.financas.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByUserId(String userId);
    List<Category> findByUserIdAndType(String userId, Transaction.TransactionType type);
}