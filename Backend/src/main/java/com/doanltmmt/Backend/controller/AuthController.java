package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.dto.LoginRequest;
import com.doanltmmt.Backend.dto.LoginResponse;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.UserRepository;
import com.doanltmmt.Backend.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            String roleName = user.getRole() != null ? user.getRole().getName() : "STUDENT";
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", roleName);
            claims.put("userId", user.getId());

            String token = tokenProvider.generateToken(user.getUsername(), claims);
            return new LoginResponse(token, user.getId(), user.getUsername(), user.getFullName(), roleName);
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Sai tên đăng nhập hoặc mật khẩu"
            );
        }
    }
}
