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

@Entity
@Table(schema = "mentoring", name = "mentor_calls")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(length = 500)
    private String link;

    @Column(columnDefinition = "text")
    private String notes;

    public MentorCall(Long teamId, Long mentorId, Instant startsAt, Instant endsAt, String link, String notes) {
        this.teamId = teamId;
        this.mentorId = mentorId;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.link = link;
        this.notes = notes;
    }

}
