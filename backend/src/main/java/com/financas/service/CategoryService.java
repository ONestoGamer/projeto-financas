package com.financas.service;

import com.financas.dto.CategoryRequest;
import com.financas.dto.CategoryDTO;
import com.financas.model.Category;
import com.financas.model.User;
import com.financas.repository.CategoryRepository;
import com.financas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

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
    public CategoryDTO createCategory(CategoryRequest request) {
        User user = getCurrentUser();

        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setType(request.getType());
        category.setUser(user);

        category = categoryRepository.save(category);

        return mapToDTO(category);
    }

    public List<CategoryDTO> getAllCategories() {
        User user = getCurrentUser();
        List<Category> categories = categoryRepository. findByUserId(user.getId());
        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        User currentUser = getCurrentUser();
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        return mapToDTO(category);
    }

    @Transactional
    public CategoryDTO updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        User currentUser = getCurrentUser();
        if (!category. getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request. getColor());
        category.setType(request.getType());

        category = categoryRepository. save(category);

        return mapToDTO(category);
    }

    @Transactional
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        User currentUser = getCurrentUser();
        if (!category. getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (! category.getTransactions().isEmpty()) {
            throw new RuntimeException("Não é possível excluir categoria com transações associadas");
        }

        categoryRepository. delete(category);
    }

    @Transactional
    public void createDefaultCategories(User user) {
        // Categorias de despesa padrão
        String[][] expenseCategories = {
                {"Alimentação", "🍔", "#EF4444"},
                {"Transporte", "🚗", "#F59E0B"},
                {"Moradia", "🏠", "#8B5CF6"},
                {"Saúde", "⚕️", "#10B981"},
                {"Educação", "📚", "#3B82F6"},
                {"Lazer", "🎮", "#EC4899"},
                {"Compras", "🛍️", "#F97316"},
                {"Outros", "📦", "#6B7280"}
        };

        for (String[] cat : expenseCategories) {
            Category category = new Category();
            category.setName(cat[0]);
            category.setIcon(cat[1]);
            category.setColor(cat[2]);
            category.setType(com.financas.model.Transaction. TransactionType.EXPENSE);
            category.setUser(user);
            categoryRepository.save(category);
        }

        // Categorias de receita padrão
        String[][] incomeCategories = {
                {"Salário", "💰", "#10B981"},
                {"Freelance", "💼", "#3B82F6"},
                {"Investimentos", "📈", "#8B5CF6"},
                {"Outros", "💵", "#6B7280"}
        };

        for (String[] cat : incomeCategories) {
            Category category = new Category();
            category.setName(cat[0]);
            category.setIcon(cat[1]);
            category.setColor(cat[2]);
            category.setType(com.financas.model.Transaction. TransactionType.INCOME);
            category.setUser(user);
            categoryRepository.save(category);
        }
    }

    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setColor(category.getColor());
        dto.setType(category.getType());
        return dto;
    }
}