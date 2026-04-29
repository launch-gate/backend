package com.launchgate.identity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "identity", name = "user_contacts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ContactType type;

    @Column(nullable = false, length = 320)
    private String value;

    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact;

    public UserContact(Long userId, ContactType type, String value, boolean primaryContact) {
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.primaryContact = primaryContact;
    }

}
