package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.auth;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.security.JwtService;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() == null) {
            user.setRole("USER");
        } else {
            user.setRole(request.getRole());
        }

        repository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthenticationResponse(jwtToken);
    }
}