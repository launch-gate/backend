package com.launchgate.identity.repository;

import com.launchgate.identity.entity.AccessToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByTokenId(String tokenId);

    void deleteAllByExpiresAtBefore(Instant now);
}
