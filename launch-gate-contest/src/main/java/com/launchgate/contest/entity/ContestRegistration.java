package com.launchgate.contest.entity;

import com.launchgate.identity.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.Instant;

@Entity
@Table(schema = "contest", name = "contest_registrations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContestRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private UserAccount participant;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    public ContestRegistration(Contest contest, UserAccount participant, Instant registeredAt) {
        this.contest = contest;
        this.participant = participant;
        this.registeredAt = registeredAt;
    }

    public Long getContestId() {
        return contest.getId();
    }

    public Long getParticipantId() {
        return participant.getId();
    }

}
