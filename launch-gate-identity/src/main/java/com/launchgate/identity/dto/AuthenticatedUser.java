package com.launchgate.identity.dto;

import com.launchgate.identity.entity.*;


public record AuthenticatedUser(
        Long id,
        String email,
        AccountType accountType
) {
    public boolean organizer() {
        return accountType == AccountType.ORGANIZER;
    }
}
