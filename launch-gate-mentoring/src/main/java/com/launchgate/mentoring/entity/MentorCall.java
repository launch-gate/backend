package com.launchgate.mentoring.entity;

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
 * Встреча с ментором.
 */
@Entity
@Table(schema = "mentoring", name = "mentor_calls")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserAccount mentor;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(length = 500)
    private String link;

    @Column(columnDefinition = "text")
    private String notes;

    public MentorCall(Team team, UserAccount mentor, Instant startsAt, Instant endsAt, String link, String notes) {
        this.team = team;
        this.mentor = mentor;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.link = link;
        this.notes = notes;
    }

    public Long getTeamId() {
        return team.getId();
    }

    public Long getMentorId() {
        return mentor.getId();
    }

}
