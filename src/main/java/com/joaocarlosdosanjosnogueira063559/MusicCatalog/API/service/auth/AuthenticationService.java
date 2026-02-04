package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.service.auth;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth.AuthenticationRequestDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth.AuthenticationResponseDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth.RegisterRequestDTO;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.UserRepository;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.security.JwtService;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    public AuthenticationService(UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager,RefreshTokenService refreshTokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Usuário já cadastrado com este e-mail.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? "USER" : request.getRole());

        repository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

        return new AuthenticationResponseDTO(jwtToken, refreshToken);
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new RuntimeException("E-mail ou senha inválidos.");
        }

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação."));

        String jwtToken = jwtService.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail()).getToken();

        return new AuthenticationResponseDTO(jwtToken, refreshToken);
    }
    public String generateToken(User user) {
        return jwtService.generateToken(user.getEmail());
    }
}
