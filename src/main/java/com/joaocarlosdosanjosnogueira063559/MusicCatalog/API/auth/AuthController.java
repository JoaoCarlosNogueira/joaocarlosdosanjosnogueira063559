package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.RefreshToken;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.security.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "01. Auth")
public class AuthController {

    private final AuthenticationService service;
    private final RefreshTokenService refreshTokenService;
    public AuthController(AuthenticationService service, RefreshTokenService refreshTokenService) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid  @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = service.generateToken(user);
                    return ResponseEntity.ok(new AuthenticationResponse(accessToken, request.getRefreshToken()));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token inválido ou não encontrado!"));
    }

}