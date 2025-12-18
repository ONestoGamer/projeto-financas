package com.financas.service;

import com.financas.dto.*;
import com.financas.model.User;
import com.financas.repository.UserRepository;
import com.financas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final CategoryService categoryService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentando registrar usuário com email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email já cadastrado:  {}", request.getEmail());
            throw new RuntimeException("Email já cadastrado");
        }

        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            user = userRepository.save(user);
            log.info("Usuário salvo com sucesso.  ID: {}", user.getId());

            // Criar categorias padrão para o novo usuário
            categoryService.createDefaultCategories(user);
            log.info("Categorias padrão criadas para o usuário: {}", user.getId());

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());

            log.info("Registro concluído com sucesso para:  {}", user.getEmail());
            return new AuthResponse(token, userDTO);

        } catch (Exception e) {
            log.error("Erro ao registrar usuário: ", e);
            throw new RuntimeException("Erro ao registrar usuário:  " + e.getMessage());
        }
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Tentando fazer login com email: {}", request. getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            UserDetails userDetails = userDetailsService. loadUserByUsername(request.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());

            log.info("Login bem-sucedido para: {}", user.getEmail());
            return new AuthResponse(token, userDTO);

        } catch (Exception e) {
            log.error("Erro ao fazer login: ", e);
            throw new RuntimeException("Email ou senha inválidos");
        }
    }
}