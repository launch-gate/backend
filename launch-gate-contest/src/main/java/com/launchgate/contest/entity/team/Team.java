package com.launchgate.contest.entity.team;

import com.launchgate.contest.entity.Contest;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "contest", name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private UserAccount leader;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "invite_token", nullable = false, unique = true, length = 80)
    private String inviteToken;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "team")
    private Set<TeamMember> members = new HashSet<>();

    @OneToMany(mappedBy = "team")
    private Set<TeamJoinRequest> joinRequests = new HashSet<>();

    public Team(
            Contest contest,
            UserAccount leader,
            String name,
            String inviteToken,
            Instant createdAt
    ) {
        this.contest = contest;
        this.leader = leader;
        this.name = name;
        this.inviteToken = inviteToken;
        this.createdAt = createdAt;
    }

    public Long getContestId() {
        return contest.getId();
    }

    public Long getLeaderId() {
        return leader.getId();
    }

}
