package com.launchgate.identity.utils;

import com.launchgate.identity.dto.ContactResponse;
import com.launchgate.identity.dto.UserProfileResponse;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.entity.UserContact;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthMapper {

    public static UserProfileResponse toProfile(UserAccount user, List<UserContact> contacts) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getAccountType(),
                user.getFullName(),
                user.getNickname(),
                user.getBio(),
                contacts.stream().map(AuthMapper::toContact).toList()
        );
    }

    private static ContactResponse toContact(UserContact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getType(),
                contact.getValue(),
                contact.isPrimaryContact()
        );
    }
}
