package com.launchgate.identity.service;

import com.launchgate.identity.entity.UserContact;
import com.launchgate.identity.utils.AuthMapper;
import lombok.RequiredArgsConstructor;

import com.launchgate.identity.dto.UpdateProfileRequest;
import com.launchgate.identity.dto.UserProfileResponse;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.repository.UserAccountRepository;
import com.launchgate.identity.repository.UserContactRepository;
import com.launchgate.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAccountRepository userRepository;
    private final UserContactRepository contactRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse profile(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return profileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.updateProfile(request.fullName(), request.nickname(), request.bio());
        contactRepository.deleteAllByUserId(userId);
        if (request.contacts() != null) {
            request.contacts().stream()
                    .map(contact -> new UserContact(userId, contact.type(), contact.value(), contact.primaryContact()))
                    .forEach(contactRepository::save);
        }
        return profileResponse(user);
    }

    @Transactional(readOnly = true)
    public Long findUserId(Long userId) {
        return userRepository.findById(userId)
                .map(UserAccount::getId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserAccount getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserProfileResponse profileResponse(UserAccount user) {
        return AuthMapper.toProfile(user, contactRepository.findAllByUserId(user.getId()));
    }
}
