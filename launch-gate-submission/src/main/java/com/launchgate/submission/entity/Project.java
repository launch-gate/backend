package com.launchgate.submission.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



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

@Entity
@Table(schema = "submission", name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contest_id", nullable = false)
    private Long contestId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "owner_participant_id")
    private Long ownerParticipantId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", insertable = false, updatable = false)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_participant_id", insertable = false, updatable = false)
    private UserAccount ownerParticipant;

    @OneToMany(mappedBy = "project")
    private Set<StageSubmission> stageSubmissions = new HashSet<>();

    public Project(Long contestId, Long teamId, Long ownerParticipantId, Instant createdAt) {
        this.contestId = contestId;
        this.teamId = teamId;
        this.ownerParticipantId = ownerParticipantId;
        this.createdAt = createdAt;
    }
}
