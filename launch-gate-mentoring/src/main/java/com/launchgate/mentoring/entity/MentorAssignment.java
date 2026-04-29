package com.launchgate.mentoring.entity;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(schema = "mentoring", name = "mentor_assignments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contest_id", nullable = false)
    private Long contestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", insertable = false, updatable = false)
    private Contest contest;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", insertable = false, updatable = false)
    private UserAccount mentor;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    public MentorAssignment(Long contestId, Long teamId, Long mentorId, Instant assignedAt) {
        this.contestId = contestId;
        this.teamId = teamId;
        this.mentorId = mentorId;
        this.assignedAt = assignedAt;
    }

}
