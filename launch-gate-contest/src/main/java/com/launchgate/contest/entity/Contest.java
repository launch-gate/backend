package com.launchgate.contest.entity;

import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.entity.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "contest", name = "contests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "rules", columnDefinition = "text")
    private String rules;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ContestStatus status = ContestStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_mode", nullable = false, length = 32)
    private ParticipationMode participationMode;

    @Column(name = "min_team_size")
    private Integer minTeamSize;

    @Column(name = "max_team_size")
    private Integer maxTeamSize;

    @Column(name = "registration_ends_at")
    private LocalDate registrationEndsAt;

    @Column(name = "team_building_ends_at")
    private LocalDate teamBuildingEndsAt;

    @Column(name = "starts_at")
    private LocalDate startsAt;

    @Column(name = "ends_at")
    private LocalDate endsAt;

    @Column(name = "contacts", columnDefinition = "text")
    private String contacts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "contest")
    private Set<ContestOrganizer> organizers = new HashSet<>();

    @OneToMany(mappedBy = "contest")
    private Set<ContestStage> stages = new HashSet<>();

    @OneToMany(mappedBy = "contest")
    private Set<ContestRegistration> registrations = new HashSet<>();

    @OneToMany(mappedBy = "contest")
    private Set<Team> teams = new HashSet<>();

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Contest(String title,
                   String description,
                   String rules,
                   ParticipationMode participationMode,
                   Integer minTeamSize,
                   Integer maxTeamSize,
                   LocalDate registrationEndsAt,
                   LocalDate teamBuildingEndsAt,
                   LocalDate startsAt,
                   LocalDate endsAt,
                   String contacts) {
        update(title,
                description,
                rules,
                participationMode,
                minTeamSize,
                maxTeamSize,
                registrationEndsAt,
                teamBuildingEndsAt,
                startsAt,
                endsAt,
                contacts
        );
    }

    public void update(
            String title,
            String description,
            String rules,
            ParticipationMode participationMode,
            Integer minTeamSize,
            Integer maxTeamSize,
            LocalDate registrationEndsAt,
            LocalDate teamBuildingEndsAt,
            LocalDate startsAt,
            LocalDate endsAt,
            String contacts
    ) {
        this.title = title;
        this.description = description;
        this.rules = rules;
        this.participationMode = participationMode;
        this.minTeamSize = minTeamSize;
        this.maxTeamSize = maxTeamSize;
        this.registrationEndsAt = registrationEndsAt;
        this.teamBuildingEndsAt = teamBuildingEndsAt;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.contacts = contacts;
    }

    public void publish() {
        status = ContestStatus.PUBLISHED;
    }

}
