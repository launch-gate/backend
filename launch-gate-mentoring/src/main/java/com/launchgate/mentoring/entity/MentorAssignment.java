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

/**
 * Назначение ментора.
 */
@Entity
@Table(schema = "mentoring", name = "mentor_assignments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserAccount mentor;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    public MentorAssignment(Contest contest, Team team, UserAccount mentor, Instant assignedAt) {
        this.contest = contest;
        this.team = team;
        this.mentor = mentor;
        this.assignedAt = assignedAt;
    }

    public Long getContestId() {
        return contest.getId();
    }

    public Long getTeamId() {
        return team.getId();
    }

    public Long getMentorId() {
        return mentor.getId();
    }

}
