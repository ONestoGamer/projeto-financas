package com.financas.repository;

import com.financas.model.Budget;
import org.springframework.data.jpa. repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {
    List<Budget> findByUserId(String userId);
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            String userId, String categoryId, Integer month, Integer year
    );
}