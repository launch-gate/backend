package com.launchgate.contest.entity.team;

import com.launchgate.contest.enums.TeamJoinRequestStatus;
import com.launchgate.identity.entity.UserAccount;
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
import java.time.Instant;

@Entity
@Table(schema = "contest", name = "team_join_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private UserAccount participant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TeamJoinRequestStatus status = TeamJoinRequestStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TeamJoinRequest(Team team, UserAccount participant, Instant createdAt) {
        this.team = team;
        this.participant = participant;
        this.createdAt = createdAt;
    }

    public void approve() {
        status = TeamJoinRequestStatus.APPROVED;
    }

    public void reject() {
        status = TeamJoinRequestStatus.REJECTED;
    }

}
