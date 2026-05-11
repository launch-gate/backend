package com.launchgate.submission.entity;

import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.identity.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "submission", name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_participant_id")
    private UserAccount ownerParticipant;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "project")
    private Set<StageSubmission> stageSubmissions = new HashSet<>();

    public Project(Contest contest, Team team, UserAccount ownerParticipant, Instant createdAt) {
        this.contest = contest;
        this.team = team;
        this.ownerParticipant = ownerParticipant;
        this.createdAt = createdAt;
    }

    public Long getContestId() {
        return contest.getId();
    }

    public Long getTeamId() {
        return team == null ? null : team.getId();
    }

    public Long getOwnerParticipantId() {
        return ownerParticipant == null ? null : ownerParticipant.getId();
    }
}
