package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth;

public class AuthenticationResponseDTO {
    private String accessToken;
    private String refreshToken;

    public AuthenticationResponseDTO() {
    }

    public AuthenticationResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}