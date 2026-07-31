package com.offerbase.backend.controller;

import com.offerbase.backend.dto.LoginRequest;
import com.offerbase.backend.dto.LoginResponse;
import com.offerbase.backend.dto.RegisterRequest;
import com.offerbase.backend.dto.RegisterResponse;
import com.offerbase.backend.entity.User;
import com.offerbase.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RegisterResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        String token = userService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}