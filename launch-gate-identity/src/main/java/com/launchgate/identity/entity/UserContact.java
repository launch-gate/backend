package com.launchgate.identity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "identity", name = "user_contacts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ContactType type;

    @Column(nullable = false, length = 320)
    private String value;

    @Column(name = "primary_contact", nullable = false)
    private boolean primaryContact;

    public UserContact(UserAccount user, ContactType type, String value, boolean primaryContact) {
        this.user = user;
        this.type = type;
        this.value = value;
        this.primaryContact = primaryContact;
    }

    public Long getUserId() {
        return user.getId();
    }

}
