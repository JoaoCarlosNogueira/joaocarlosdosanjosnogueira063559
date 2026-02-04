package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthenticationRequestDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve estar em um formato válido")
    @Schema(example = "joao@gmail.com")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Schema(example = "senha123")
    private String password;

    public AuthenticationRequestDTO() {
    }

    public AuthenticationRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}