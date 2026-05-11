package com.launchgate.submission.entity;

import com.launchgate.contest.entity.stage.ContestStage;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "submission", name = "stage_submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StageSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private ContestStage stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private UserAccount submittedByUser;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "submission")
    private Set<SubmissionValue> values = new HashSet<>();

    public StageSubmission(Project project, ContestStage stage, Instant createdAt) {
        this.project = project;
        this.stage = stage;
        this.createdAt = createdAt;
    }

    public void submit(UserAccount submittedByUser, Instant now) {
        status = SubmissionStatus.SUBMITTED;
        this.submittedByUser = submittedByUser;
        submittedAt = now;
    }

    public Long getProjectId() {
        return project.getId();
    }

    public Long getStageId() {
        return stage.getId();
    }

    public Long getSubmittedBy() {
        return submittedByUser == null ? null : submittedByUser.getId();
    }
}
