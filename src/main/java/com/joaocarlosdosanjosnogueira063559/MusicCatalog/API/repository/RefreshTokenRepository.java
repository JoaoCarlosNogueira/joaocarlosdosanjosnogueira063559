package com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.repository;

import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.RefreshToken;
import com.joaocarlosdosanjosnogueira063559.MusicCatalog.API.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);
}