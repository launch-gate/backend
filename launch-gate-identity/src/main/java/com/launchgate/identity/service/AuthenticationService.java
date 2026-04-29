package com.launchgate.identity.service;

import com.launchgate.common.ConflictException;
import com.launchgate.common.DomainException;
import com.launchgate.identity.dto.*;
import com.launchgate.identity.entity.AccessToken;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.entity.UserContact;
import com.launchgate.identity.repository.AccessTokenRepository;
import com.launchgate.identity.repository.UserAccountRepository;
import com.launchgate.identity.repository.UserContactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.launchgate.identity.utils.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserAccountRepository userRepository;
    private final UserContactRepository contactRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Clock clock;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        var email = request.email().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User with this email already exists");
        }
        var user = new UserAccount(
                email,
                passwordEncoder.encode(request.password()),
                request.accountType(),
                request.fullName(),
                request.nickname(),
                request.bio()
        );
        user = userRepository.save(user);
        replaceContacts(user, request.contacts());
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new DomainException("bad_credentials", "Email or password is invalid"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new DomainException("bad_credentials", "Email or password is invalid");
        }
        return issueToken(user);
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser requireAuthenticatedUser(String authorizationHeader) {
        var claims = jwtService.verify(authorizationHeader);
        var token = accessTokenRepository.findByTokenId(claims.tokenId())
                .orElseThrow(() -> new DomainException("bad_token", "JWT token is not stored"));
        if (!token.isActive(Instant.now(clock))) {
            throw new DomainException("expired_token", "JWT token expired");
        }
        var user = token.getUser();
        if (!user.getId().equals(claims.userId())) {
            throw new DomainException("bad_token", "JWT subject is invalid");
        }
        return new AuthenticatedUser(user.getId(), user.getEmail(), user.getAccountType());
    }

    private AuthResponse issueToken(UserAccount user) {
        accessTokenRepository.deleteAllByExpiresAtBefore(Instant.now(clock));
        var tokenId = generateTokenId();
        var expiresAt = Instant.now(clock).plusSeconds(jwtService.expiresInSeconds());
        accessTokenRepository.save(new AccessToken(tokenId, user, expiresAt, Instant.now(clock)));
        return new AuthResponse(
                jwtService.issue(user, tokenId),
                "Bearer",
                jwtService.expiresInSeconds(),
                AuthMapper.toProfile(user, contactRepository.findAllByUserId(user.getId()))
        );
    }

    private void replaceContacts(UserAccount user, List<ContactRequest> contacts) {
        contactRepository.deleteAllByUserId(user.getId());
        if (contacts == null) {
            return;
        }
        contacts.stream()
                .map(contact -> new UserContact(user.getId(), contact.type(), contact.value(), contact.primaryContact()))
                .forEach(contactRepository::save);
    }

    private String generateTokenId() {
        var high = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 36);
        var low = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 36);
        return high + low;
    }
}
