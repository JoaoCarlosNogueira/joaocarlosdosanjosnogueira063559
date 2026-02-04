package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.dto.auth;


public class RefreshRequestDTO {
    private String refreshToken;

    public RefreshRequestDTO() {}

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}