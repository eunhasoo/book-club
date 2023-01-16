package com.eunhasoo.bookclub.auth.ui;

import com.eunhasoo.bookclub.auth.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthenticationController {

    private static final String BEARER = "Bearer ";

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/api/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authenticationService.login(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .body(new LoginResponse(token));
    }
}
