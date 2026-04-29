package com.launchgate.identity.controller;


import lombok.RequiredArgsConstructor;

import com.launchgate.identity.dto.*;
import com.launchgate.identity.service.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Identity", description = "User profile API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public UserProfileResponse profile(@AuthenticationPrincipal AuthenticatedUser user) {
        return userService.profile(user.id());
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update current user profile")
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(user.id(), request);
    }
}
