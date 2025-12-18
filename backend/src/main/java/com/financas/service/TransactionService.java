package com. financas.service;

import com.financas.dto.TransactionRequest;
import com.financas.dto.TransactionResponse;
import com.financas.dto.CategoryDTO;
import com.financas.model.Category;
import com.financas.model.Transaction;
import com.financas.model.User;
import com.financas.repository.CategoryRepository;
import com.financas.repository.TransactionRepository;
import com.financas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = getCurrentUser();

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Transaction transaction = new Transaction();
        transaction. setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction. setAttachment(request.getAttachment());
        transaction.setCategory(category);
        transaction.setUser(user);

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getAllTransactions() {
        User user = getCurrentUser();
        List<Transaction> transactions = transactionRepository. findByUserIdOrderByDateDesc(user. getId());
        return transactions.stream()
                .map(this:: mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByPeriod(LocalDate startDate, LocalDate endDate) {
        User user = getCurrentUser();
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(user. getId(), startDate, endDate);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(String id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser. getId())) {
            throw new RuntimeException("Acesso negado");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        transaction. setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction. setDescription(request.getDescription());
        transaction.setDate(request.getDate());
        transaction. setAttachment(request.getAttachment());
        transaction.setCategory(category);

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        User currentUser = getCurrentUser();
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setDescription(transaction.getDescription());
        response.setDate(transaction.getDate());
        response.setAttachment(transaction.getAttachment());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(transaction.getCategory().getId());
        categoryDTO.setName(transaction.getCategory().getName());
        categoryDTO.setIcon(transaction. getCategory().getIcon());
        categoryDTO.setColor(transaction. getCategory().getColor());
        categoryDTO.setType(transaction. getCategory().getType());

        response.setCategory(categoryDTO);

        return response;
    }
}